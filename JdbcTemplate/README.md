# JDBC Template 연습

## 목차

1. [JdbcTemplate](#jdbctemplate)
2. [Validation](#validation)
3. [UnitTest](#unittest)

## JdbcTemplate

### JDBC 데이터베이스 접근 방식

Spring Framework JDBC에서는 데이터베이스에 접근할 때 다음과 같은 방식중에 선택할 수 있다.

- `JdbcTemplate`

  가장 고전적인 방법으로 모든 접근 방식은 내부적으로 `JdbcTemplate`를 사용한다

- `NamedParameterJdbcTemplate`

  `JdbcTemplate` 의 `?`를 네임드 파라미터로 접근하기위해 사용한다.

- `SimpleJdbcInsert`와 `SimpleJdbcCall`

  테이블의 이름과 또는 프로시저 이름과 매개 변수 그리고 해당 컬럼의 이름을 주어서 sql 구문을 자동으로 만들어 준다.

- RDBMS objects(`MappingSqlQuery`, `SqlUpdate`, `StoredProcedure`)

### 데이터베이스와 객체 연결

Spring Framework JDBC에서 자바 객체과 데이터베이스 테이블 연결을 위해 다음 인터페이스를 사용한다.

- `RowMapper`

  `ResultSet`에서 값을 가져와서 객체로 반환

  ```java
  private final RowMapper<User> userRowMapper = ((resultSet, i) -> new User(
              resultSet.getLong("seq"),
              resultSet.getString("email"),
              resultSet.getString("passwd"),
              resultSet.getInt("login_count"),
              resultSet.getObject("last_login_at", LocalDateTime.class),
              resultSet.getObject("create_at", LocalDateTime.class)
      ));
  ```

- `SqlParameterSource`

  객체에서 값을 받아와 데이터베이스에 들어갈 값으로 변환

  ```java
  SqlParameterSource parameters = new MapSqlParameterSource()
                  .addValue("seq", user.getSeq())
                  .addValue("email", user.getEmail())
                  .addValue("passwd", user.getPasswd())
                  .addValue("login_count", user.getLoginCount())
                  .addValue("last_login_at", user.getLastLoginAt())
                  .addValue("create_at", user.getCreateAt());
  ```

### `Template` 객체 생성

#### `NamedParameterJdbcTemplate` 생성

`DataSource` 를 주입 받아 생성 할 수 있다.

```java
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    

		public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
```

#### `SimpleJdbcInsert`

`SimpleJdbcInsert` 는 데이터베이스와 객체의 속성이 일치하면 자동으로 insert문을 만들어 준다.

```java
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("seq");
    }
```

### Query

#### `SELECT` Query

`RowMapper` 를 다음과 같이 선언하여 `SELECT` 를 구현하였다

```java
    private final RowMapper<User> userRowMapper = ((resultSet, i) -> new User(
            resultSet.getLong("seq"),
            resultSet.getString("email"),
            resultSet.getString("passwd"),
            resultSet.getInt("login_count"),
            resultSet.getObject("last_login_at", LocalDateTime.class),
            resultSet.getObject("create_at", LocalDateTime.class)
    ));
```

다음 쿼리는 여러건을 조회한다.

```java
    @Override
    public Collection<User> findAll() throws DataAccessException {
        return this.namedParameterJdbcTemplate.query(
                "SELECT * FROM users",
                userRowMapper
                );
    }
```

다음쿼리는 parameter 를 넣어 id로 조회 하였다. 

```java
    @Override
    public Optional<User> findById(Long id) throws DataAccessException {
        Map<String, Long> params = Map.of("seq", id);
        User user;
        try {
            user = this.namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE seq = :seq",
                    params,
                    userRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            user = null;
        }

        return Optional.ofNullable(user);
    }
```

#### `INSERT`, `UPDATE` Query

`id`가 설정되어 있으면 `Update` 를 하고 설정되어있지 않으면 `INSERT` 를 한다.

```java
    @Override
    public User save(User user) throws DataAccessException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("seq", user.getSeq())
                .addValue("email", user.getEmail())
                .addValue("passwd", user.getPasswd())
                .addValue("login_count", user.getLoginCount())
                .addValue("last_login_at", user.getLastLoginAt())
                .addValue("create_at", user.getCreateAt());
        if (user.getSeq() == null) {
            Number newSeq = insertUser.executeAndReturnKey(parameters);
            user = User.toSequencedUser(newSeq.longValue(), user);
        } else {
            this.namedParameterJdbcTemplate.update(
                    "UPDATE users SET email=:email, passwd=:passwd, login_count=:login_count, " +
                            "last_login_at=:last_login_at WHERE seq=:seq",
                    parameters);
        }
        return user;
    }
```

## Validation

### 의존성 추가

다음 의존성을 추가한다.

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
```

### `@Valid` 

사용자에게 값을 입력받고 해당 값을 검증하려는 경우 검증하고자 하는 매개변수에 `@Valid` annotation을 선언한다.

```java
public ResponseEntity<CommonResponseDto<String>> createUser(@RequestBody @Valid UserRequestDto userRequestDto, UriComponentsBuilder uriComponentsBuilder) {
}
```

### 검증할 필드 선택

`@Min`, `@Email`, `@Notnull`, `@Notblank` 등을 이용해 검증하고자 하는 필드를 설정한다.

```java
public class UserRequestDto {
    @Email
    private final String principal;

    @NotBlank(message = "must be not blank")
    private final String credentials;

    public UserRequestDto(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }
}

```

### 예외 처리

예외 메시지를 커스텀 하려는 경우 `MethodArgumentNotValidException` 를 예외처리하면 커스텀 할 수 있다.

```java
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponseDto<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return CommonResponseDto.fail(errors);
    }
```

## UnitTest

### `@WebMvcTest`

`@WebMvcTest` 를 이용해 controller하나에대한 테스트를 작성할 수 있다.

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
		
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
  	//...
}
```



### `@BeforEach`

`@BeforEach` 를 이용하여 각 테스트 메소드 전에 실행할 코드를 지정할 수 있다.

mockito를 이용하였다.

```java
    @BeforeEach
    void setup() {
        user1 = new User(
                1L,
                "test1@email.com",
                "pass",
                0,
                null,
                LocalDateTime.now()
        );
        user2 = new User(
                2L,
                "test2@email.com",
                "pass",
                0,
                null,
                LocalDateTime.now()
        );
        given(this.userService.findAllUser()).willReturn(List.of(user1, user2));
        given(this.userService.findUserById(anyLong())).will((answer) -> {
            Long argument = answer.getArgument(0);
            if (argument.equals(user1.getSeq())) {
                return Optional.of(user1);
            } else if (argument.equals(user2.getSeq())) {
                return Optional.of(user2);
            }
            throw new IdNotFoundException(String.valueOf(argument));
        });
        given(this.userService.saveUser(any(User.class))).will((answer) -> {
            User argument = answer.getArgument(0);
            return new User(
                    3L,
                    argument.getEmail(),
                    argument.getPasswd(),
                    argument.getLoginCount(),
                    argument.getLastLoginAt(),
                    argument.getCreateAt()
            );
        });

    }
```

