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

- @RequestMapping(value="/ajax") <br/>
view에서 보내오는 기본적인 ajax는 "/prizeLottery/ajax"에서 처리한다.<br/><br/>
[1] @RequestParam(value="idx")는 뽑힌 회원의 id number를 DB에서 제외(updateAlreadyPrize)한다.<br/>
[2] @RequestParam(value="init")는 이미 뽑힌 회원(already_prize)를 초기화시킨다.<br/>
[2] @RequestParam(value="prizeExclude[]")는 prize exclude를 시킬 회원 id number 리스트를 받아 exclude한다.<br/><br/>

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

만약 @RequestParam값이 없다면 prize_exclude와 already_prize값이 없는 참가자 중에 rand값을 돌려 한명의 참석자를 view로 보낸다.

        ArrayList<SoConfTempMember> registrants = (ArrayList) soConfTempMemberMapper.findByConfIdAndExcludePrizeExcludeAndAlreadyPrize(soConfConference.getId());
        int randValue = societyLotteryService.getRandValue(registrants.size());

        SoConfTempMember registrant = registrants.get(randValue);

- @RequestMapping(value="/table")<br/>
'참석자 보기','당첨된 참석자 보기' Modal를 통해 보여지는 table들의 정보를 view로 보내주는 역할을 한다.<br/>
@RequestParam(value="list")에서 list=all로 보내면 모든 참석자 정보를 가지고 오고, list=already로 보내면 당첨된 참석자 목록을 가지고온다.<br/>
Service부문에서 처리해야했지만 처리 속도로 인해 Controller에 삽입하게 되었다.<br/>


        @RequestMapping(value="/table", produces = "application/text; charset=utf8")
        @ResponseBody
        public String lotteryTable(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                               @ModelAttribute("soConfConference") SoConfConference soConfConference,
                               @RequestParam(value="list", required = false) String list,
                               Model model) {

        try{
            StringBuilder sbb = new StringBuilder();
            ArrayList<SoConfTempMember> registrants = new ArrayList<>();


            if (soConfConference == null) {
                return sbb.toString();
            }

            if(list != null && list.equalsIgnoreCase("all")){
                registrants = (ArrayList) soConfTempMemberMapper.findByConfId(soConfConference.getId());

                for(int i=0; i<registrants.size(); i++){
                    SoConfTempMember curTempMember = registrants.get(i);

                    //*모양으로 사용자 정보를 가린다. service에서 코드를 작성하지 않은 이유는 ajax로 가지고 오면서 시간이 너무 오래 걸린다.
                    String encodedName = curTempMember.getName().trim().substring(0, curTempMember.getName().trim().length()-1)+"*";
                    int pos = curTempMember.getEmail().indexOf('@');
                    String encodedEmail = pos == -1 ? curTempMember.getEmail().substring(0,curTempMember.getEmail().length()-2)+"**"
                            : curTempMember.getEmail().substring(0,pos-2)+"**"+ curTempMember.getEmail().substring(pos);
                    String encodedPhone = (curTempMember.getPhone() != null && curTempMember.getPhone().trim().length() > 2) ? curTempMember.getPhone().trim().substring(0,curTempMember.getPhone().trim().length()-2)+"**" : "";

                    sbb.append("<tr>\n"
                            + "<td >"+String.valueOf(i+1)+"<span style='display:none;' class='tempId'>"+curTempMember.getId()+"</span>"+"</td>\n"
                            + "<td >"+encodedName+"</td>\n"
                            + "<td >"+encodedEmail+"</td>\n"
                            + "<td >"+encodedPhone+"</td>\n"
                            + "<td >"+(curTempMember.getPrizeExclude() == 1 ? "<span class='prize_exclude_yes'>YES</span>" : "<span class='prize_exclude_no'>NO</span>")+"</td>\n"
                            + "<td >"+(curTempMember.getAlreadyPrize() == 1 ? "<span class='already_prize_yes'>YES</span>" : "<span class='already_prize_no'>NO</span>")+"</td>\n"
                            + "</tr>\n\n"
                    );
                }
            }

            else if(list != null && list.equalsIgnoreCase("already")){
                registrants = (ArrayList) soConfTempMemberMapper.findByConfIdAndAlreadyPrize(soConfConference.getId());

                for(int i=0; i<registrants.size(); i++){
                    SoConfTempMember curTempMember = registrants.get(i);

                    //*모양으로 사용자 정보를 가린다. service에서 코드를 작성하지 않은 이유는 ajax로 가지고 오면서 시간이 너무 오래 걸린다.
                    String encodedName = curTempMember.getName().trim().substring(0, curTempMember.getName().trim().length()-1)+"*";
                    int pos = curTempMember.getEmail().indexOf('@');
                    String encodedEmail = pos == -1 ? curTempMember.getEmail().substring(0,curTempMember.getEmail().length()-2)+"**"
                            : curTempMember.getEmail().substring(0,pos-2)+"**"+ curTempMember.getEmail().substring(pos);
                    String encodedPhone = (curTempMember.getPhone() != null && curTempMember.getPhone().trim().length() > 2) ? curTempMember.getPhone().trim().substring(0,curTempMember.getPhone().trim().length()-2)+"**" : "";

                    sbb.append("<tr>\n"
                            + "<td >"+String.valueOf(i+1)+"</td>\n"
                            + "<td >"+encodedName+"</td>\n"
                            + "<td >"+encodedEmail+"</td>\n"
                            + "<td >"+encodedPhone+"</td>\n"
                            + "<td >"+(curTempMember.getPrizeExclude() == 1 ? "<span style='color:green;'>YES</span>" : "<span style='color:red;'>NO</span>")+"</td>\n"
                            + "<td >"+(curTempMember.getAlreadyPrize() == 1 ? "<span style='color:green;'>YES</span>" : "<span style='color:red;'>NO</span>")+"</td>\n"
                            + "</tr>\n\n"
                    );
                }
            }

            return sbb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        }

### (2) java/society/service/SocietyLotteryService.java

- JWT를 사용한 Host계정의 Zoom Room을 가지고 오는 함수<br/>
Zoom Room을 가지고 오는 api는 get방식을 사용하므로 HttpGet을 사용해서 Request한다.<br/>
response의 statusCode가 200일 때 JSONArray로 Zoom Room들의 Info를 가지고 온다.

        public Object getZoomRoomList(String userId){
        //get zoom Room list and 해당학회의 zoom 등록

        try{
            String url = zoomAPIBaseURL+"/users/"+userId+"/webinars?page_size=300";

            HttpContext context = new BasicHttpContext();
            HttpGet getRequest = new HttpGet(url);

            getRequest.setHeader("authorization",  "Bearer "+jwtToken);
            getRequest.setHeader("content-type", "application/json");

            HttpResponse response = client.execute(getRequest, context);

            if (response.getStatusLine().getStatusCode() == 200) {

                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser parser = new JSONParser();
                Object bodyParser = parser.parse(body);
                JSONObject jsonObject = (JSONObject) bodyParser;

                JSONArray webinars = (JSONArray) jsonObject.get("webinars");

                System.out.println(webinars);
                return (Object) webinars;
            }else{
                client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
        }

- JWT를 사용한 Host계정의 Zoom Registrants를 가지고 오는 함수<br/>
Zoom Id가 매개변수로 필요하다.

        public Object getZoomRegistrantsList(String zoomId) throws UnsupportedOperationException{
        try {
            System.out.println(zoomId);
            String url = zoomAPIBaseURL+"/webinars/"+zoomId+"/registrants?page_size=300";
            HttpContext context = new BasicHttpContext();
            HttpGet getRequest = new HttpGet(url);

            getRequest.setHeader("authorization",  "Bearer "+jwtToken);
            getRequest.setHeader("content-type", "application/json");

            HttpResponse response = client.execute(getRequest, context);

            if (response.getStatusLine().getStatusCode() == 200) {

                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);

                JSONParser parser = new JSONParser();
                Object bodyParser = parser.parse(body);
                JSONObject jsonObject = (JSONObject) bodyParser;

                JSONArray registrants = (JSONArray) jsonObject.get("registrants");

                return (Object) registrants;
            } else {
                client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        }


- 사용자가 선택한 Zoom Room의 Registrants를 insert하기 위한 함수<br/>
ZoomRoom의 ID List를 받아서 각각의 정보들을 DB에 삽입한다.

        public boolean insertZoomRegistrants(String zoomRoomList, int conf_id){

        try{
            JSONParser parser = new JSONParser();
            Object zoomRoomListObj = parser.parse(zoomRoomList);
            JSONArray zoomRoomListjsonArray = (JSONArray) zoomRoomListObj;
            System.out.println(zoomRoomListjsonArray);

            for(Object roomInfoObj : zoomRoomListjsonArray){
                JSONObject roomInfo = (JSONObject) roomInfoObj;

                String roomNum = (String) roomInfo.get("num");

                JSONArray zoomRegistrants = (JSONArray) getZoomRegistrantsList(roomNum);

                for(Object registrantsInfoObj : zoomRegistrants){

                    JSONObject registrantsInfo = (JSONObject) registrantsInfoObj;

                    String firstName = registrantsInfo.get("first_name") != null ? (String) registrantsInfo.get("first_name") : "";
                    String lastName =  registrantsInfo.get("last_name") != null ? (String) registrantsInfo.get("last_name") : "";
                    String email = (String) registrantsInfo.get("email");
                    String phone = (String) registrantsInfo.get("phone");

                    SoConfTempMember member = soConfTempMemberMapper.findByConfIdAndEmail(conf_id, email);
                    if(member == null){
                        member = new SoConfTempMember();
                        member.setConfId(conf_id);
                        member.setName(firstName+lastName);
                        member.setEmail(email);
                        member.setPassword("0000");
                        member.setPhone(phone);
                        soConfTempMemberMapper.insert(member);
                    }
                    else{
                        member.setConfId(conf_id);
                        member.setName(firstName+lastName);
                        member.setEmail(email);
                        member.setPhone(phone);
                        soConfTempMemberMapper.update(member);
                    }
                }
            }

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
        }


### (3) webapp/WEB-INF/views/society/conference/prizeLottery.jsp

- prize lottery의 참석자 목록 버튼을 클릭하면 나오는 table 목록<br/>
/table로 ajax를 보내서 registrants list를 가지고 온다.<br/>
만약 prize exclude를 Yes, No로 check하고 confirm을 누르면 다시 한번, ajax로 보내서 prize_exclude 값을 갱신한다.

        $('.registrants_list').click(function(){

        var table_head = '<table class="table">\n'
            + '<thead>\n'
            + '<tr>\n'
            + '<th scope="col">#</th>\n'
            + '<th scope="col">Name</th>\n'
            + '<th scope="col">Email</th>\n'
            + '<th scope="col">Phone Number</th>\n'
            + '<th scope="col"><span style="color:red;font-size:0.5em;">*modifiable</span><br/>Prize Exclude</th>\n'
            + '<th scope="col">Already Prize</th>\n'
            + '</tr>\n'
            + '</thead>\n'
            + '<tbody>\n';

        var table_foot = '</tbody>\n' +
            '                </table>';

        $.ajax({
            url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/table?list=all',
            success:function(data){
                bootbox.confirm({
                    title: "참석자 목록",
                    message: table_head+data+table_foot,
                    buttons: {
                        cancel: {
                            label: '<i class="fa fa-times"></i> Cancel'
                        },
                        confirm: {
                            label: '<i class="fa fa-check"></i> Confirm'
                        }
                    },
                    callback: function (result) {
                        if(result){
                            $.ajax({
                                url:'${baseUrl}/society/${societyAbbr}/conference/${soConfConference.nameId}/prizeLottery/ajax',
                                type:"POST",
                                dataType:"json",
                                contentType:"application/x-www-form-urlencoded; charset=UTF-8",
                                data: {
                                    'prizeExclude': Array.from(prize_exclude_set)
                                },
                                // dataType:"json",
                                // contentType : "application/json;charset=UTF-8",
                                success : function(data){
                                    $('.prizeLotteryAjax').html(data);
                                    prize_exclude_set.clear();
                                },
                                complete : function(){
                                    prize_exclude_set.clear();
                                }
                            })
                        }
                    }
                });
            }
        })
        });
 
 
### (4) webapp/WEB-INF/views/society/conference/prizeLotteryAjax.jsp

- gsap을 사용해서 prizeLottery의 애니메이션 효과를 만듦<br/>
해당 코드는 처음 선물박스가 생겼다가 사라지고 30개의 네모박스가 생기는 애니메이션 js

        document.querySelector(".gift").addEventListener("click", async function() {
                await gsap.to(".gift", {
                    duration: 0.5,
                    opacity: 0,
                    y: -100,
                    stagger: 0.1,
                    ease: "back.in"
                });

                $('.gift').css('display','none');
                $('.boxes').css('display','');
                gsap.from(".box", {
                    duration: 2,
                    scale: 0.5,
                    opacity: 0,
                    delay: 0.5,
                    stagger: 0.2,
                    ease: "elastic",
                    force3D: true,
                    onStart: showMessage,
                    onStartParams: ["원하시는 카드번호를 선택해주세요."],
                });
            });

## 4. Reference

front-end : 
- https://greensock.com/gsap/ (animation - gsap)

zoom api : 
- https://marketplace.zoom.us/docs/api-reference/zoom-api/webinars/webinarregistrants (Add Zoom Registrants)
