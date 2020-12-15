# Login System (j_spring_security + Google OAuth)

Spring FrameWork의 j_spring_security_check 기능과 Google 계정으로 로그인할 수 있는 Google OAuth를 사용하여 Login System을 구현합니다.<br/>
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/signin.png" width="100%">
## 1. 특징

### (1) j_spring-security_check
- UserDetails, UserDetailsService interface 사용
- /loginResult에서 ROLE_USER 또는 ROLE_ADMIN authority가 없으면 -> /signin으로 직행 <br/>
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/loginResult_user.png" width="50%">

- **[user and user denied (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/mKUCU8xRItY/0.jpg)](https://youtu.be/mKUCU8xRItY) <br/>
- /admin은 admin 계정만 접속이 가능하고 admin authority가 없으면 -> denied됨
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/loginResult_admin.png" width="50%">
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/admin_denied.png" width="50%"> <br/>

- **[Google User 영상 (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/i-T_k176k2E/0.jpg)](https://youtu.be/i-T_k176k2E) <br/><br/>
- Bcrypt를 사용한 암호화 사용
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/database2.png" width="50%">

### (2) Google OAuth
- 사용자가 Registration 절차를 받지 않아도 Google계정으로 로그인 가능
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/signin_google.png" width="50%">
- Google Development에 등록 후 apikey와 apiSecret 받은 후 사용
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/Login-System/google api.png" width="50%">
- **[Google User 영상 (▼ youtube video)]**<br/>
[![Google User2](https://img.youtube.com/vi/gIIGy0rvpjg/0.jpg)](https://youtu.be/gIIGy0rvpjg)
 
## 2. System Configuration Diagram


## 3. Key Code Description

### (1) resources/common/security.xml

j_spring_security 전체 Control를 담당하고 있다.

    <sec:authentication-manager id="loginTestAuthManger">
        <sec:authentication-provider user-service-ref="loginTestUserDetailsService">

        </sec:authentication-provider>
    </sec:authentication-manager>

user-service-ref는 custom한 loginTestUserDetailsService를 사용한다.

    <sec:http pattern="/loginTest/**" use-expressions="true" authentication-manager-ref="loginTestAuthManger">
        <sec:form-login login-page="/loginTest/signin" authentication-success-handler-ref="loginTestLoginHandler" authentication-failure-handler-ref="loginTestLoginFailureHandler"/>
        <sec:logout logout-url="/loginTest/signout" delete-cookies="JSESSIONID"/>

        <sec:intercept-url pattern="/loginTest/payment" access="isAuthenticated()"/>
        <sec:intercept-url pattern="/loginTest/registration-form" access="permitAll"/>
        <sec:intercept-url pattern="/loginTest/loginResult" access="isAuthenticated()"/>
        <sec:intercept-url pattern="/loginTest/admin" access="hasRole('ROLE_ADMIN')"/>
    </sec:http>
   
   
http pattern은 /loginTest/** 이후에 모두 Autority check가 필요하며 이부분을 통해 login과 logout 거점을 설정할 수 있다. login이 success할 경우 loginTestLoginHandler를 거치며, failure할 경우 loginTestLoginFailureHandler를 거쳐 j_session이 삭제된다.

intercept-url에서는 여러가지 access가 있으며, isAuthenticated(), permitAll, 특정 ROLE만 출입 가능하게 만들 수 있다.

### (2) java/domain/loginTest/loginTestUserDetails


    @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
         ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
         auth.add(new SimpleGrantedAuthority(AUTHORITY));
         return auth;
     }
 
 
UserDetails를 implements해 사용하기 때문에 getAuthorities()를 override해준다. ArrayList에는 권한 목록이 들어가 있고 목록을 return해준다.

### (3) java/service/loginTest/LoginTestService.java


    RestTemplate restTemplate = new RestTemplate();

    //Google Request Domain에다가 param들을 추가한다.
    GoogleOAuthRequest googleOAuthRequestParam = new GoogleOAuthRequest();
    googleOAuthRequestParam.setClientId(googleAPIkey);
    googleOAuthRequestParam.setClientSecret(googleAPIsecret);
    googleOAuthRequestParam.setCode(code);
    googleOAuthRequestParam.setRedirectUri("http://localhost:8080/loginTest/google-redirect");
    googleOAuthRequestParam.setGrantType("authorization_code");

    //JWT TOKEN을 받아온다.
    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    ResponseEntity<String> resultEntity = restTemplate.postForEntity("https://accounts.google.com/o/oauth2/token", googleOAuthRequestParam, String.class);
    GoogleOAuthResponse result = mapper.readValue(resultEntity.getBody(), new TypeReference<GoogleOAuthResponse>() {});
    String jwtToken = result.getIdToken();
    System.out.println("jwt token : "+jwtToken);

    //받아온 TOKEN의 INFO를 얻기위해 해당 url로 정보를 보낸다.
    String requestUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/tokeninfo")
            .queryParam("id_token", jwtToken).toUriString();

    String resultJson = restTemplate.getForObject(requestUrl, String.class);

    userInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>() {});


Google의 OAuth를 사용하기 위해 Token을 받아온 후, token info를 받아온다.


    loginTestUserDetails userTemp = new loginTestUserDetails();

    userTemp.setID(userInfo.get("email"));
    userTemp.setNAME(userInfo.get("email"));
    userTemp.setPW(userInfo.get("sub"));
    userTemp.setAUTHORITY("ROLE_USER");

    Authentication requestAUTH = new UsernamePasswordAuthenticationToken(userTemp, null);
    Authentication resultAUTH = am.authenticate(requestAUTH);

    //Save Google jwt information in j_spring_security authentication context
    SecurityContextHolder.getContext().setAuthentication(resultAUTH);
        
        
        
 loginTestUserDetails에 정보를 저장한 후 SecurityContextHolder에 context를 저장해서 로그인 권한을 사용할 수 있게 한다.
 
 

## 4. Reference

front-end : 
- https://startbootstrap.com/theme/sb-admin-2 (admin form)
- https://www.w3schools.com/howto/howto_css_login_form.asp (login form)

j_spring_security : 
- https://to-dy.tistory.com/86 (전반적인 j_spring_security 구조 참조)
- https://velog.io/@sa833591/Spring-Security-4-Authentication-SecurityContextHolder%EC%9D%98-%EC%9D%B4%ED%95%B4 (SecurityContextHolder )


Google OAtuth : 

- https://gdtbgl93.tistory.com/182 (전반적인 Google OAuth 구조 참조)
- https://sjh836.tistory.com/141 (RestTemplate 설명) 
