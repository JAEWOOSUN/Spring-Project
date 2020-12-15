# Login System (j_spring_security + Google Oauth)

Spring FrameWork의 j_spring_security_check 기능과 Google 계정으로 로그인할 수 있는 Google Oauth를 사용하여 Login System을 구현합니다.

## 1. 특징

### (1) j_spring-security_check
- UserDetails, UserDetailsService interface 사용
UserDetailsService 인터페이스는 DB에서 유저 정보를 가져오는 역할을 한다. AuthenticationProvider로 유저 정보를 
- loginResult, admin에서 authority가 없으면 -> signin으로 직행
- Bcrypt를 사용한 복호화
 
## 2. System Configuration Diagram

   ![](https://dv8u54qddgb7y.cloudfront.net/society/images/logos/keeg/logo_large.png)


## 3. Key Code Description

### (1) resources/common/security.xml

 

## Reference

front-end : 
- https://startbootstrap.com/theme/sb-admin-2 (admin form)
- https://www.w3schools.com/howto/howto_css_login_form.asp (login form)

j_spring_security : 
- https://to-dy.tistory.com/86 (전반적인 j_spring_security 구조 참조)
- https://velog.io/@sa833591/Spring-Security-4-Authentication-SecurityContextHolder%EC%9D%98-%EC%9D%B4%ED%95%B4 (SecurityContextHolder )


 
