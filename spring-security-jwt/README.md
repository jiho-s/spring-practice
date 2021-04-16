# Spring Security & Jwt

[스프링 시큐리티 레퍼런스]: https://docs.spring.io/spring-security/site/docs/5.4.6/reference/html5/#servlet-authentication

## 목차

1. [Email Password Authentication](#email-password-authentication)
2. [Authentication Handler](#authentication-handler)
3. [Email Password Authentication 등록하기](#email-password-authentication-등록하기)
4. [Jwt Authentication](#jwt-authentication)
5. [Jwt Authentication 등록하기](#jwt-authentication-등록하기)

## Email Password Authentication

사용자가 login을 시도할때 로그인 결과가 성공하면, Jwt 토큰을 발급해주는 과정을 구현해보자. 사용자 요청은 다음과 같이 필터를 거쳐 SecurityFilterChain의 `UserPasswordAuthenticationFilter`에서 인증을 수행하게된다. 따라서 커스텀 인증을 수행하기 위해 `EmailPasswordAuthenticationFilter`를 다음과 같이 구현해준다.

![multi-securityfilterchain](./Asset/multi-securityfilterchain.png)

### EmailPasswordAuthenticationFilter

```java
public class EmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    //...

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/v1/login",
            "POST");

    @Getter
    private String emailParameter = EMAIL_KEY;

    @Getter
    private String passwordParameter = PASSWORD_KEY;

    public EmailPasswordAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String email = obtainEmail(request);
        email = (email != null) ? email : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        EmailPasswordAuthenticationToken authRequest = new EmailPasswordAuthenticationToken(email, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

  
  	//....


}
```

`DEFAULT_ANT_PATH_REQUEST_MATCHER`의 주소로 login을 요청시 필터에서 걸려 인증과정을 수행하게된다.  이 때 `EmailPasswordAuthenticationToken`이라는 토큰을 만들어서 실제 인증은 `AuthenticationManager`에서 수행하게 되는데 `AuthenticationManager`는 등록된 `AuthenticationProvider`에서 해당 `EmailPasswordAuthenticationToken`을 처리할 수 있는 `AuthenticationProvider`를 찾아 처리하게된다.

### `EmailPasswordAuthenticationToken`

```java
public class EmailPasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private String credentials;

    public EmailPasswordAuthenticationToken(String principal, String credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    public EmailPasswordAuthenticationToken(Object principal, String credentials,
                                            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }
  
  //....
}
```

인증 전에는 인증하고자하는 정보가 담겨있고 인증완료후에는 인증된 정보가 들어있다.

### `EmailPasswordAuthenticationProvider`

실제 인증을 수행하는 클래스다. `memberService`에서 `member`를 조회한후 해당 `member`가 존재하면, Jwt 토큰을 발급하고 발급된 토큰을 `EmailPasswordAuthenticationToken`에 넣어서 전달한다. 전달받은 정보를 `AuthenticationSuccessHandler`에서 응답을 만든다.

```java
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

  	//...

    private final Jwt jwt;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(EmailPasswordAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("EmailPasswordAuthenticationProvider.onlySupports",
                        "Only JwtAuthenticationToken is supported"
                        ));
        Member member = retrieveMember((EmailPasswordAuthenticationToken) authentication);
        authenticationChecks(member, (EmailPasswordAuthenticationToken) authentication);
        return createSuccessAuthentication(member);
    }

    private void authenticationChecks(Member member, EmailPasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("EmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, member.getPassword())) {
            this.logger.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("EmailPasswordAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    private String determineEmail(EmailPasswordAuthenticationToken authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

    private Member retrieveMember(EmailPasswordAuthenticationToken authentication) throws AuthenticationException {
        String email = determineEmail(authentication);
        return this.memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Authentication createSuccessAuthentication(Member member) {
        Claims claims = Claims.of(member);
        EmailPasswordAuthenticationToken result = new EmailPasswordAuthenticationToken(
                JwtPrincipal.builder()
                        .id(member.getId())
                        .build(),
                null,
                createAuthorityList(claims.getRoles()));
        String token = jwt.generateToken(claims);
        result.setDetails(EmailPasswordAuthenticationDetail.builder()
                .token(token)
                .member(member)
                .build()
        );
        this.logger.debug("Authenticated member");
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

## Authentication Handler

`EmailPasswordAuthentication`이 성공 및 실패시 응답을 넣어주는 handler를 구현한다.

### `EmailPasswordAuthenticationSuccessHandler`

인증 성공시 `authentication`의 `details`에 넣어둔 토큰과 member 정보를 `objectMapper`를 이용해 JSON으로 바꾼후 응답에 넣어준다.

```java
@RequiredArgsConstructor
public class EmailPasswordAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().print(objectMapper.writeValueAsString(ApiResponse.ok(authentication.getDetails())));
    }
}
```

### `EmailPasswordAuthenticationFailureHandler`

인증 실패시 예외의 종류(아이디 없음, 비밀번호 틀림 등)에 따라 적절한 메시지를 `objectMapper`를 이용해 넣어준다.

```java
@RequiredArgsConstructor
public class EmailPasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public static ApiResponse<?> CONFLICT_EMAIL = ApiResponse.error("Authentication error (cause: email not found)", HttpStatus.CONFLICT);

    public static ApiResponse<?> DEFAULT_CONFLICT = ApiResponse.error("Authentication error (cause: badCredentials)", HttpStatus.CONFLICT);

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ApiResponse<?> apiResponse = DEFAULT_CONFLICT;

        if (UsernameNotFoundException.class.isAssignableFrom(exception.getClass())) {
            apiResponse = CONFLICT_EMAIL;
        }
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
```

## Email Password Authentication 등록하기

### `EmailPasswordAuthenticationFilter` 등록하기

`WebSecurityConfigure`클래스를 만들고 `configure`메소드 안에 `EmailPasswordAuthenticationFilter`를 만들고 핸들러또한 등록해준다.

```java
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Jwt jwt;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    private final JwtTokenProperty jwtTokenProperty;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .formLogin()
                    .disable();
        EmailPasswordAuthenticationFilter authFilter = new EmailPasswordAuthenticationFilter(this.authenticationManager());
        authFilter.setAuthenticationFailureHandler(new EmailPasswordAuthenticationFailureHandler(objectMapper));
        authFilter.setAuthenticationSuccessHandler(new EmailPasswordAuthenticationSuccessHandler(objectMapper));
        http
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);


    }
}
```

### `EmailPasswordAuthenticationProvider` 등록하기

`WebSecurityConfigure`안에 `configure`메소드를 이용해 다음과 같이 등록해준다.

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(new EmailPasswordAuthenticationProvider(jwt, memberService, passwordEncoder));
}
```



## Jwt Authentication

다음은 토큰을 헤더에 넣었을 때 인증하는 과정을 구현해보자

### `JwtAuthenticationFilter`

`JwtAuthenticationFilter`는 `GenericFilterBean`을 상속하여 구현한다. 먼저 헤더에 headerKey에 해당하는 `key`로 `Bearer`로 시작하는 값이 있는지 확인 후 있으면, `JwtAuthenticationToken`을 생성후 `AuthenticationManager`에 넘겨 인증을 수행한다. 인증이 성공하면, `SecurityContextHolder.getContext()` 메소드를 이용해 인증정보를 넣어준다.

```java
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean
        implements ApplicationEventPublisherAware {
  
  	//...

    private static final Pattern BEARER = Pattern.compile("^Bearer\\s?(.+)$", Pattern.CASE_INSENSITIVE);

    private final AuthenticationManager authenticationManager;

    private String headerKey;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    @Builder
    public JwtAuthenticationFilter(ApplicationEventPublisher eventPublisher, AuthenticationManager authenticationManager, String headerKey, AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
        this.headerKey = headerKey != null ? headerKey : DEFAULT_HEADER_KEY;
        this.authenticationDetailsSource = authenticationDetailsSource != null ? authenticationDetailsSource : new WebAuthenticationDetailsSource();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
    }

    private void doAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
            return;
        }
        Authentication authenticationResult = attemptAuthentication(request, response);
        if (authenticationResult == null) {
            return;
        }
        successfulAuthentication(request, response, authenticationResult);
    }

    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String authorizationToken = obtainAuthorizationToken(request);
        if (authorizationToken == null) {
            return null;
        }
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(null, authorizationToken);
        setDetails(request, jwtAuthenticationToken);
        return this.authenticationManager.authenticate(jwtAuthenticationToken);
    }

    private String obtainAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (token != null) {
            if (this.logger.isDebugEnabled()) {
                logger.debug("Jwt authorization api detected: {}", token);
            }
            token = URLDecoder.decode(token, StandardCharsets.UTF_8);
            Matcher matcher = BEARER.matcher(token);
            return matcher.find() ? matcher.group(1) : null;
        }
        return null;
    }




    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          Authentication authResult) {
        if (authResult.getCredentials() != null) {
            // credential이 null이 아니면 refresh token이므로 헤더에 넣어준다.
            setRefreshToken(response, (JwtAuthenticationToken) authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Set SecurityContextHolder to {}", authResult);
        }
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

    }

    private void setRefreshToken(HttpServletResponse response, JwtAuthenticationToken authResult) {
        response.setHeader(headerKey, authResult.getCredentials().toString());
        authResult.eraseCredentials();
    }

    private void setDetails(HttpServletRequest request, JwtAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }
}
```

### `JwtAuthenticationToken`

`JwtAuthenticationToken`은 `EmailPasswordAuthenticationToken`와 같으므로 생략하겠다.

### `JwtAuthenticationProvider`

실제 `JwtAuthenticationToken`인증을 수행한다. 토큰을 검증후 검증결과 이상이 없으면 인증된 토큰을 반환한다.

```java
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private final Jwt jwt;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("JwtAuthenticationProvider.onlySupports",
                        "Only JwtAuthenticationToken is supported"
                ));
        Claims claims = retrieveClaims((JwtAuthenticationToken) authentication);
        if (claims == null) {
            return null;
        }
        return createSuccessAuthentication(claims, authentication);
    }

    private Claims retrieveClaims(JwtAuthenticationToken authentication) {
        String token = authentication.getCredentials().toString();
        try {
            Claims claims = verify(token);
            logger.debug("Jwt parse result: {}", claims);
            return claims;
        } catch (Exception e) {
            logger.warn("Jwt processing failed: {}", e.getMessage());
        }
        return null;
    }

    private Authentication createSuccessAuthentication(Claims claims, Authentication authentication) {
        String refreshedToken = null;
        if (canRefresh(claims)) {
            refreshedToken = refreshToken(claims);
        }
        JwtAuthenticationToken result = new JwtAuthenticationToken(
                JwtPrincipal.builder()
                        .id(claims.getId())
                        .build(),
                refreshedToken,
                createAuthorityList(claims.getRoles()));
        result.setDetails(authentication.getDetails());
        this.logger.debug("Authenticated token");
        return result;
    }

    private Claims verify(String token) {
        return jwt.verify(token);
    }

    private boolean canRefresh(Claims claims) {
        return this.jwt.canRefresh(claims);
    }

    private String refreshToken(Claims claims) {
        return  jwt.refreshToken(claims);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

## Jwt Authentication 등록하기

`EmailPasswordAuthenticationFilter` 등록하기보다 간단하게 핸들러를 등록하지 않아도 된다.

```java
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Jwt jwt;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    private final JwtTokenProperty jwtTokenProperty;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-resources", "/webjars/**", "/static/**", "/templates/**", "/h2/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new EmailPasswordAuthenticationProvider(jwt, memberService, passwordEncoder));
        auth.authenticationProvider(new JwtAuthenticationProvider(jwt));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                    .and()
                .formLogin()
                    .disable();
        EmailPasswordAuthenticationFilter authFilter = new EmailPasswordAuthenticationFilter(this.authenticationManager());
        authFilter.setAuthenticationFailureHandler(new EmailPasswordAuthenticationFailureHandler(objectMapper));
        authFilter.setAuthenticationSuccessHandler(new EmailPasswordAuthenticationSuccessHandler(objectMapper));
        JwtAuthenticationFilter jwtAuthenticationFilter = JwtAuthenticationFilter.builder()
                .authenticationManager(this.authenticationManager())
                .headerKey(jwtTokenProperty.getHeader())
                .build();
        http
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, authFilter.getClass());


    }
}

```

