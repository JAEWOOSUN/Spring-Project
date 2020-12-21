# Prize Lottery Service (using Zoom API and Excel)

***코로나***로 인해 여러 학회들이 온라인 학회를 개최하게 되었습니다.<br/>
하지만 온라인이라는 한정된 공간으로 인해 행사 많은 부분에 제약이 걸리게 되었고 특히, 폐회식에서 '경품 추천'을 하지못하는 학회들이 많아지게 되었습니다.<br/>
Prize Lottery Service는 Online Conference 홈페이지에 이식되어 온라인에서도 경품추천을 재밌게 즐길수 있으며, 여러 기능들을 통해 사용자가 Service를 쉽게 이용할 수 있습니다.<br/> 

주목할점은 Zoom을 사용해서 논문을 발표하는 학회들이 많아졌고, 많은 학회 회원들은 Zoom을 통해 온라인학회를 참여하게 되었다는 것입니다.<br/>
Prize Lottery Service는 Zoom Registrants Api를 사용하여 실시간으로 Zoom에 참여한 참가자들을 '경품 추천' 서비스에 참석할 수 있게 합니다. <br/>

<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/prize_lottery/main2.png" width="100%">

## 1.특징

- 학회마다 다른 회원 목록으로 경품추첨 가능
- gsap을 이용한 매끄러운 애니메이션 효과
- **[Prize Lottery play 영상 (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/gJ6-KSiiCCA/0.jpg)](https://youtu.be/gJ6-KSiiCCA) <br/>

- Prize Exclude 기능(경품추첨에 제외시키는 기능) 추가
- **[Prize Exclude 기능 (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/tSZepg_wtTY/0.jpg)](https://youtu.be/tSZepg_wtTY)

- Setting 페이지에서는 Excel과 Zoom API(JWT 사용)를 통해 참석자 목록을 가지고 올 수 있음
- **[Setting Excel 기능 (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/07YB6ioiww0/0.jpg)](https://youtu.be/07YB6ioiww0)
- **[Setting Zoom 기능 (▼ youtube video)]**<br/>
[![user and user denied](https://img.youtube.com/vi/E5up_3CXFvw/0.jpg)](https://youtu.be/E5up_3CXFvw)

## 2. System Configuration Diagram
- Prize Lottery architecture<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/prize_lottery/prize_lottery_structure.png" width="100%">
- 구조
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/prize_lottery/structure.png" height="100%">

## 3. Key Code Description

### (1) java/society/controller/conference/soConfConferencePrizeLotteryController.java

- @RequestMapping(value="/ajax")
view에서 보내오는 기본적인 ajax는 "/prizeLottery/ajax"에서 처리한다.

        @RequestMapping(value="/ajax")
        public Object lotteryAJAX(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                                  @ModelAttribute("soConfConference") SoConfConference soConfConference,
                                  @RequestParam(value="idx", required = false) String idx,
                                  @RequestParam(value="init", required = false) String init,
                                  HttpServletRequest request,
                                  @RequestParam(value="prizeExclude[]", required = false) List<String> prizeExclude,
                                  Model model) {
            if (soConfConference == null) {
                return String.format("redirect:/society/%s", societyAbbr);
            }

            if(idx != null){
                soConfTempMemberMapper.updateAlreadyPrize(idx);
            }

            if(init != null && init.equalsIgnoreCase("on")){
                soConfTempMemberMapper.initAlreadyPrize(soConfConference.getId());
            }

            if(prizeExclude != null){
                for(String val : prizeExclude)
                    soConfTempMemberMapper.updatePrizeExclude(val);
            }

            ArrayList<SoConfTempMember> registrants = (ArrayList) soConfTempMemberMapper.findByConfIdAndExcludePrizeExcludeAndAlreadyPrize(soConfConference.getId());
            int randValue = societyLotteryService.getRandValue(registrants.size());

            SoConfTempMember registrant = registrants.get(randValue);
            String encodedPhone = (registrant.getPhone() != null && registrant.getPhone().trim().length() > 2) ? registrant.getPhone().trim().substring(0,registrant.getPhone().trim().length()-2)+"**" : "";
            registrant.setPhone(encodedPhone);

            model.addAttribute("registrant", registrant);
            model.addAttribute("soConfConference", soConfConference);
            model.addAttribute("soConfConferenceMainImageList", soConfConferenceService.getSoConfConferenceMainImages(soConfConference));
            model.addAttribute("soConfConferenceDivControl", soConfConferenceService.getSoConfConferenceDivControl(soConfConference.getId()));
            model.addAttribute("soConfConferenceContactList", soConfConferenceService.getSoConfConferenceContactList(society, soConfConference));
            return String.format("society/conference/%s/prizeLotteryAjax", soConfConference.getViewType());
        }

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
- https://greensock.com/gsap/ (animation - gsap)

zoom api : 
- https://marketplace.zoom.us/docs/api-reference/zoom-api/webinars/webinarregistrants (Add Zoom Registrants)
