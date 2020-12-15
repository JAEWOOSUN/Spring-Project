# Login System (j_spring_security + Google OAuth)

Spring FrameWork의 j_spring_security_check 기능과 Google 계정으로 로그인할 수 있는 Google OAuth를 사용하여 Login System을 구현합니다.<br/>
<img src="signin" width="50%">

## 1. 특징

### (1) j_spring-security_check
- UserDetails, UserDetailsService interface 사용
- /loginResult에서 ROLE_USER 또는 ROLE_ADMIN authority가 없으면 -> /signin으로 직행<br/>
<img src="loginResult_user" width="50%">
<iframe youtube user계정+admin denied>
- /admin은 admin 계정만 접속이 가능하고 admin authority가 없으면 -> denied됨<br/>
<img src="loginResult_admin" width="50%">
<iframe youtube admin 계정>
- Bcrypt를 사용한 암호화 사용
 <img src="database2" width="50%">

### (2) Google OAuth
- 사용자가 Registration 절차를 받지 않아도 Google계정으로 로그인 가능
- Google Development에 등록 후 apikey와 apiSecret 받은 후 사용
<img src="loginResult_google" width="50%">
<iframe youtube google_user>
 
## 2. System Configuration Diagram
<div class="mxgraph" style="max-width:100%;border:1px solid transparent;" data-mxgraph="{&quot;highlight&quot;:&quot;#0000ff&quot;,&quot;nav&quot;:true,&quot;resize&quot;:true,&quot;toolbar&quot;:&quot;zoom layers lightbox&quot;,&quot;edit&quot;:&quot;_blank&quot;,&quot;xml&quot;:&quot;&lt;mxfile host=\&quot;app.diagrams.net\&quot; modified=\&quot;2020-12-15T07:08:08.613Z\&quot; agent=\&quot;5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36\&quot; etag=\&quot;UeYR044cWsWruwqfslUX\&quot; version=\&quot;14.0.1\&quot; type=\&quot;device\&quot;&gt;&lt;diagram name=\&quot;Page-1\&quot; id=\&quot;c7558073-3199-34d8-9f00-42111426c3f3\&quot;&gt;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&lt;/diagram&gt;&lt;/mxfile&gt;&quot;}"></div>
<script type="text/javascript" src="https://viewer.diagrams.net/js/viewer-static.min.js"></script>


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
