# OAuth2 연습

## 목차

1. [의존성 추가](#의존성-추가)
2. [`application.yml` 설정](#application.yml-설정)
3. [UserService](#UserService)
4. [User](#user)

## 의존성 추가

소셜 로그인 기능을 추가하기 위해 Spring Security OAuth2 Client를 추가한다.

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

## `application.yml` 설정

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            clientId: 1
            clientSecret: 2
          google:
            clientId: 1
            clientSecret: 2
```

## UserService

OAuth2(git) Oidc(google) 로그인을 위해서 `OAuth2UserService`를 각각 구현해야 한다. 기본값으로 `DefaultOAuth2UserService`, `OidcUserService`가 구현되어있지만 추가기능을 위해 CustomUserservice를 각각 구현해주었다.

#### Member

```java
@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    private String name;

    private String email;

    private String password;

    @ElementCollection
    @Enumerated(value = EnumType.ORDINAL)
    Set<MemberRole> role;

    @Builder
    public Member(String name, String email, String password,Set<MemberRole> role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
```

#### BaseUserService

멤버의 `MemberRole` 를 공통적으로 변환하는 메소드를 위해 만들었다. 모든 `UserService`는 `BaseUserService` 를 상속받는다.

```java
@RequiredArgsConstructor
public abstract class BaseUserService {
    protected final MemberRepository memberRepository;

    protected Member loadOrCreate(OAuthAttributes attributes) {
        return memberRepository.findByEmail(attributes.getEmail())
                .orElseGet(() ->memberRepository.save(attributes.toMember()));
    }

    protected static Collection<? extends GrantedAuthority> authorities(Set<MemberRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getKey()))
                .collect(Collectors.toSet());
    }
}
```

#### CustomOAuth2UserService

`loadUser()`를 `DefaultOAuth2UserService`에 위임하여 처리한다. `OAuth2UserRequest`를 처리한다.

```java
@Service
public class CustomOAuth2UserService extends BaseUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = loadOrCreate(attributes);
        return CustomOAuth2User.builder()
                .id(member.getId())
                .authorities(authorities(member.getRole()))
                .attributes(attributes.getAttributes())
                .nameAttributeKey(attributes.getNameAttributeKey())
                .build();
    }
}
```

#### CustomOidcUserService

`loadUser()`를 `OidcUserService`에 위임하여 처리한다.

```java
@Service
public class CustomOidcUserService extends BaseUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    public CustomOidcUserService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oidcUser.getAttributes());

        Member member = loadOrCreate(attributes);

        return CustomOidcUser.builder()
                .id(member.getId())
                .authorities(authorities(member.getRole()))
                .idToken(userRequest.getIdToken())
                .nameAttributeKey(attributes.getNameAttributeKey())
                .build();
    }
}
```

### User

`UserService`에서 `loadUser`를 오버라이딩 할 때 결과로 `OidcUSer`, `OAuth2User`를 리턴해야한다. 하지만 해당 User에는 id가 포함되어있지 않아 id를 포함한 Custom User를 만든다.

```java
@Getter
public class CustomOidcUser extends DefaultOidcUser {
    private Long id;

    @Builder
    public CustomOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, String nameAttributeKey, Long id) {
        super(authorities, idToken, nameAttributeKey);
        this.id = id;
    }
}
```

```java
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private Long id;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, Long id) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
    }
}
```

## 정보

## 정보

- [OAuth2 튜토리얼](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [스프링 OAuth2 레퍼런스](https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#oauth2)

