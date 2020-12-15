# Login System (j_spring_security + Google Oauth)

Spring FrameWork의 j_spring_security_check 기능과 Google 계정으로 로그인할 수 있는 Google Oauth를 사용하여 Login System을 구현합니다.<br/>
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

### (2) Google Oauth
- 사용자가 Registration 절차를 받지 않아도 Google계정으로 로그인 가능
- Google Development에 등록 후 apikey와 apiSecret 받은 후 사용
<img src="loginResult_google" width="50%">
<iframe youtube google_user>
 
## 2. System Configuration Diagram

   ![](https://dv8u54qddgb7y.cloudfront.net/society/images/logos/keeg/logo_large.png)


## 3. Key Code Description

### (1) resources/common/security.xml

UserDetails, UserDetailsService interface 사용
 <br/>
UserDetailsService 인터페이스는 DB에서 유저 정보를 가져오는 역할을 한다. AuthenticationProvider로 유저 정보를 

## Reference

front-end : 
- https://startbootstrap.com/theme/sb-admin-2 (admin form)
- https://www.w3schools.com/howto/howto_css_login_form.asp (login form)

j_spring_security : 
- https://to-dy.tistory.com/86 (전반적인 j_spring_security 구조 참조)
- https://velog.io/@sa833591/Spring-Security-4-Authentication-SecurityContextHolder%EC%9D%98-%EC%9D%B4%ED%95%B4 (SecurityContextHolder )


 
