# Zoom Admin Dashboard (using Zoom sdk)

코로나로 인해 여러 학회들이 온라인 학회를 Zoom으로 개최하게 되었습니다.<br/>
학회에서는 여러 Session을 동시에 진행하게 되었고, 이로 인해 학회 관리자가 동시에 진행되는 Session들을 한 번에 관리할 필요성을 느끼게 되었습니다.<br/>
하지만 Zoom Client에서 여러 Zoom Room에 동시 접속하는 것은 불가능했고, 접속할 때마다 묻는 등록 과정을 거쳐야하는 불편함이 있었습니다.<br/>

Zoom Admin Dashboard는 Zoom의 Web SDK를 사용하여 모든 Session의 Zoom을 한 눈에 볼 수 있게 제작되었으며,<br/>
참석자와 토론자, QnA 수를 한 눈에 볼 수 있게 해줍니다.<br/>

<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/kist/zoom_dashboard/main_image.png" width="100%">

## 1.특징

- 하나의 웹에서 동시 여러 Zoom화면 시청 가능
- Zoom Client에서 사용하는 동일한 기능 (소리, 영상, 화면공유, 채팅, QnA, 참석자보기 등) 사용 가능
- Zoom Room 번호와 Password를 넣어서 쉽게 접속/접속해제
- Zoom Admin Dashboard에 접속된 Zoom의 참석자와 토론자, QnA 수 집계
- Zoom Admin Dashboard에 접속된 Zoom의 참석자와 토론자, QnA 전체 보기 (차후 업데이트)

- **[Zoom Admin Dashboard 영상 (▼ youtube video)]**<br/>
[![Zoom Admin Dashboard](https://img.youtube.com/vi/gJ6-KSiiCCA/0.jpg)](https://youtu.be/gJ6-KSiiCCA) <br/>

## 2. System Configuration Diagram
- Prize Lottery architecture<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/kist/zoom_dashboard/zoom_dashboard_structure.png" width="100%">
- 구조
<img src="https://manuscriptlink-society-file.s3-ap-northeast-1.amazonaws.com/kist/zoom_dashboard/structure.png" height="100%">

## 3. Key Code Description

### (1) java/society/controller/zoom/ZoomEnterController.java

- @RequestMapping("/zoomWebSDK") <br/>
dashboard에서 iframe으로 접속해서 들어간다. param으로는 roomNum을 받고, password를 DB에서 찾아서 view로 보낸다.

        @RequestMapping("/zoomWebSDK")
                public String zoomwebSDK(@RequestParam(value="roomNum") String roomNum,
                                     Model model) throws IOException {

                model.addAttribute("roomNum",roomNum);
                model.addAttribute("password",zoomRoomMapper.findPassword(roomNum));

                return "zoom/zoomWebSDK";
        }


- @RequestMapping("/modify/{method}")<br/>
method가 insert이면 host Account에서 Zoom Room이 생성이 되어있는지 검사한다. (참석자 권한으로 접속하기 때문에 Room은 생성되어 있어야한다.)<br/>
생성되어 있다면 DB에 insert한다.<br/>
method가 delete면 db에서 해당 Zoom Room을 삭제한다.<br/>


        @RequestMapping("/modify/{method}")
    public String zoomDashboardModify(@PathVariable("method") String method,
                                      @RequestParam(value = "zoomRoomNumber") String zoomRoomNumber,
                                      @RequestParam(value = "password", required =false) String password,
                                      RedirectAttributes ra){

        try{
            if(method.equalsIgnoreCase("insert")){

                boolean isZoomRoomExist = zoomEnterService.isZoomRoomExist(zoomRoomNumber);

                if(isZoomRoomExist)
                    zoomRoomMapper.insert(zoomRoomNumber, password);
                else
                    ra.addFlashAttribute("msg", "noExist");
            }
            else if(method.equalsIgnoreCase("delete")){
                zoomRoomMapper.delete(zoomRoomNumber);
            }
        }catch(Exception e){
            ra.addFlashAttribute("msg", "error");
            e.printStackTrace();
        }

        return "redirect:/zoom/dashboard";
    }

### (2) java/society/service/ZoomEnterService.java

- getZoomPanelists(String zoomId)<br/>
Zoom Webinar의 토론자 리스트를 요청하는 함수이다. 토론자 권한은 음성과 영상, 화면공유 등의 기능을 쓸 수 있다.<br/>
response의 statusCode가 200일 때 JSONArray로 Zoom Panelist의 Info를 가지고 온다.

        public Object getZoomPanelists(String zoomId) throws UnsupportedOperationException{
        try {
            String url = zoomAPIBaseURL+"/webinars/"+zoomId+"/panelists?page_size=300";

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

                JSONArray panelists = (JSONArray) jsonObject.get("panelists");

                return (Object) panelists;
            } else {
                client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        }

- getZoomRoomTopicAndRegistrantsCount(List<String> roomList)<br/>
초기에 host account의 줌 방을 찾고, 해당 줌 방의 info와 참석자 수를 반환해주는 함수<br/>
JSONArray roomInfos를 만들고 hostAccount의 Zoom Room을 불러와서 거기서 topic과 정보들을 반환한다.

        public Object getZoomRoomTopicAndRegistrantsCount(List<String> roomList){

                try{

                    JSONArray roomInfos = new JSONArray();

                    for(String account : hostAccount){

                        JSONArray hostAccountZoomRoom = (JSONArray) getZoomRoomList(account);

                        for(int i=0; i<hostAccountZoomRoom.size(); i++){
                            JSONObject webinar = (JSONObject) hostAccountZoomRoom.get(i);

                            if(roomList.contains(String.valueOf(webinar.get("id")))){

                                JSONObject roomInfo = new JSONObject();

                                roomInfo.put("id", webinar.get("id"));
                                roomInfo.put("topic", webinar.get("topic"));
                                roomInfo.put("registrantsCnt", getZoomRegistrantsCount(String.valueOf(webinar.get("id"))));
                                roomInfo.put("password", zoomRoomMapper.findPassword(String.valueOf(webinar.get("id"))));

                                roomInfos.add(roomInfo);

                            }

                        }

                    }

                    return (Object) roomInfos;

                }catch(Exception e){
                    e.printStackTrace();
                }

                return null;
        }


- timeIntervalCntValue(List<String> roomList)<br/>
일정한 시간마다 참가자 수, 토론자 수, qna 수를 api를 통해 가지고 오는 함수<br/>
Zoom Room을 찾은 후, 각 room의 id를 key로 registrants와 panelist 수를 JSONObject에 저장하고 <br/>
registrants total, panelist total, qna total값을 계산해서 JSONObject에 저장한다.

        public Object timeIntervalCntValue(List<String> roomList){

                try{
                    JSONObject cntValue = new JSONObject();

                    int total_registrants_cnt = 0;
                    int total_panelists_cnt = 0;
                    int total_qna_cnt = 0;

                    for(String account : hostAccount){

                        JSONArray hostAccountZoomRoom = (JSONArray) getZoomRoomList(account);

                        for(int i=0; i<hostAccountZoomRoom.size(); i++){
                            JSONObject webinar = (JSONObject) hostAccountZoomRoom.get(i);

                            if(roomList.contains(String.valueOf(webinar.get("id")))){

                                String room = String.valueOf(webinar.get("id"));

                                String temp_registrants = getZoomRegistrantsCount(room);
                                String temp_panelists = getZoomPanelistCount(room);
                                String temp_qna = getZoomQnACount(room);

                                total_registrants_cnt += Integer.parseInt(temp_registrants);
                                total_panelists_cnt += Integer.parseInt(temp_panelists);
                                total_qna_cnt += Integer.parseInt(temp_qna);

                                cntValue.put(room+"_cnt",temp_registrants);

                            }

                        }

                    }

                    cntValue.put("registrantsTotal", String.valueOf(total_registrants_cnt));
                    cntValue.put("panelistTotal", String.valueOf(total_panelists_cnt));
                    cntValue.put("qnaCount", String.valueOf(total_qna_cnt));

                    return (Object) cntValue;

                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }

        }
        
- generateSignature(String apiKey, String apiSecret, String meetingNumber, Integer role)<br/>
Base64기반의 signature key를 만드는 함수<br/>
apikey, meetingNumber, role 등을 사용해 SHA256의 암호화 키를 만든다.

        public static String generateSignature(String apiKey, String apiSecret, String meetingNumber, Integer role) {
                try {

                    Mac hasher = Mac.getInstance("HmacSHA256");
                    String ts = Long.toString(System.currentTimeMillis() - 30000);
                    String msg = String.format("%s%s%s%d", apiKey, meetingNumber, ts, role);

                    hasher.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));

                    String message = Base64.getEncoder().encodeToString(msg.getBytes());
                    byte[] hash = hasher.doFinal(message.getBytes());

                    String hashBase64Str = DatatypeConverter.printBase64Binary(hash);
                    String tmpString = String.format("%s.%s.%s.%d.%s", apiKey, meetingNumber, ts, role, hashBase64Str);
                    String encodedString = Base64.getEncoder().encodeToString(tmpString.getBytes());

                    return encodedString.replaceAll("\\=+$", "");

                }
                catch (NoSuchAlgorithmException e) {}
                catch (InvalidKeyException e) {}
                return "";
        }


### (3) webapp/WEB-INF/views/zoom/zoomWebSDK.jsp

/table로 ajax를 보내서 registrants list를 가지고 온다.<br/>
만약 prize exclude를 Yes, No로 check하고 confirm을 누르면 다시 한번, ajax로 보내서 prize_exclude 값을 갱신한다.

        $(document).ready(function () {
                // For CDN version default
                ZoomMtg.setZoomJSLib('https://dmogdx0jrul3u.cloudfront.net/1.8.1/lib', '/av');
                ZoomMtg.preLoadWasm();
                ZoomMtg.prepareJssdk();

                getSignature(meetingConfig);
                console.log('${baseUrl}');
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
