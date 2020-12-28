package solis.pl.service.zoomEnter;

import javafx.util.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.util.parsing.json.JSON;
import solis.pl.repository.ZoomRoomMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.util.List;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

@Service
public class ZoomEnterService {

    @Value("${zoomAPI.base.url}")
    private String zoomAPIBaseURL;

    @Value("${zoom.jwt}")
    private String jwtToken;

    @Value("${zoom.hostAccount}")
    private List<String> hostAccount;

    @Inject
    private ZoomRoomMapper zoomRoomMapper;

    int timeout=3;
    RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();

    CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

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

    public Object getZoomRegistrantsList(String zoomId) throws UnsupportedOperationException{
        try {
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

    public Object getZoomQnAList(String zoomId) throws UnsupportedOperationException{
        try {
            String url = zoomAPIBaseURL+"/past_webinars/"+zoomId+"/qa?page_size=300";

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

                JSONArray questions = (JSONArray) jsonObject.get("questions");

                return (Object) questions;
            } else {
                client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                System.out.println("response is error : " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isZoomRoomExist(String zoomRoomNumber){

        try{
            for(String account : hostAccount){

                JSONArray hostAccountZoomRoom = (JSONArray) getZoomRoomList(account);

                for(int i=0; i<hostAccountZoomRoom.size(); i++){
                    JSONObject webinar = (JSONObject) hostAccountZoomRoom.get(i);
                    if(zoomRoomNumber.equalsIgnoreCase(String.valueOf(webinar.get("id")))) return true;
                }

            }

            return false;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

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
//                        String temp_qna = getZoomQnACount(room);

                        total_registrants_cnt += Integer.parseInt(temp_registrants);
                        total_panelists_cnt += Integer.parseInt(temp_panelists);
                        total_qna_cnt += Integer.parseInt("0");

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

    public String getZoomRegistrantsCount(String roomId){

        JSONArray room = (JSONArray) getZoomRegistrantsList(roomId);
        return String.valueOf(room.size());
    }

    public String getZoomPanelistCount(String roomId){

        JSONArray panelists = (JSONArray) getZoomPanelists(roomId);
        return String.valueOf(panelists.size());
    }

    public String getZoomQnACount(String roomId){

        JSONArray qna = (JSONArray) getZoomQnAList(roomId);
        return String.valueOf(qna.size());
    }

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

}
