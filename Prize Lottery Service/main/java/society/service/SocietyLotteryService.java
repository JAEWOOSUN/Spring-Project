package thinkonweb.ml.society.service;

import com.amazonaws.services.elastictranscoder.model.TimeSpan;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thinkonweb.ml.common.service.FileService;
import thinkonweb.ml.common.service.user.CustomPasswordEncoder;
import thinkonweb.ml.society.domain.conference.SoConfTempMember;
import thinkonweb.ml.society.repository.conference.SoConfTempMemberMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class SocietyLotteryService {

    private final long MAX_SIZE = 15485760;

    @Inject
    private FileService fileService;
    @Inject
    private AmazonS3 amazonS3;
    @Inject
    private SoConfTempMemberMapper soConfTempMemberMapper;
    @Inject
    private CustomPasswordEncoder passwordEncoder;

    @Value("${aws.s3.societyBucketName}")
    private String bucket;

    private String zoomAPIBaseURL = "https://api.zoom.us/v2";
    private String jwtToken = "${Zoom_JWT}";

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
//                JSONObject jsonObject = (JSONObject) obj;
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

    public Object getZoomRegistrantsList(String zoomId) throws UnsupportedOperationException{
        try {
            System.out.println(zoomId);
            String url = zoomAPIBaseURL+"/webinars/"+zoomId+"/registrants?page_size=300";
//            String jsonMessage = "{\n" +
//                    "  \"email\": \"sjfkghdsg@someemail.dfgjd\",\n" +
//                    "  \"first_name\": \"Jill\",\n" +
//                    "  \"last_name\": \"Chill\"}";

            HttpContext context = new BasicHttpContext();
            HttpGet getRequest = new HttpGet(url);
//            HttpPost postRequest = new HttpPost(url);

            getRequest.setHeader("authorization",  "Bearer "+jwtToken);
            getRequest.setHeader("content-type", "application/json");


//            postRequest.setHeader("authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOm51bGwsImlzcyI6IkRMRGxYdGpGUVRPQ3pqenFhSWpMdUEiLCJleHAiOjE2OTk4NDg5MDAsImlhdCI6MTYwNTIzNTUzN30.6nJzPUkEx65x1bN7rrFH5hvGDgLqYpNkT2bVTgrw-14");
//            postRequest.setHeader("content-type", "application/json");
            //https://zoom.us/oauth/authorize?response_type=code&client_id=THCZgK84QHYjWY2tJjpYg&redirect_uri=https://5899d41686a3.ngrok.io/zoom/redirect
            //https://zoom.us/oauth/authorize?response_type=code&client_id=dyYdV6pySe2rR2_AhUUPA&redirect_uri=https://5899d41686a3.ngrok.io/zoom/redirect
            HttpResponse response = client.execute(getRequest, context);

//            postRequest.setEntity(new StringEntity(jsonMessage));
//            HttpResponse response = client.execute(postRequest, context);

            if (response.getStatusLine().getStatusCode() == 200) {

                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
//                HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
//                HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
//
//                String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
//                System.out.println(currentUrl);
//                String tokenUrl = "https://zoom.us/oauth/token?grant_type=authorization_code&code="+code+"&redirect_uri=https://b9c67c74ef77.ngrok.io/zoom";

                JSONParser parser = new JSONParser();
                Object bodyParser = parser.parse(body);
//                JSONObject jsonObject = (JSONObject) obj;
                JSONObject jsonObject = (JSONObject) bodyParser;

                JSONArray registrants = (JSONArray) jsonObject.get("registrants");
//                String resp = EntityUtils.toString(response.getEntity());
//                System.out.println(resp);
//                String location = response.get
//                System.out.println(location);

//                String result = EntityUtils.toString(response.getEntity());
//                System.out.println("result = " + result);
//
//                JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);
//                String jsonResult = convertedObject.get("result").getAsString();
//                String jsonMsg = convertedObject.get("message").getAsString();
//                System.out.println("username = " + username);
//                return "";
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

    public int getRandValue(int list_length){

        Random rand = new Random();

        return rand.nextInt(list_length);
    }

    public String uploadExcelFile(MultipartFile file, int conf_id) throws IOException {
        String randomString = UUID.randomUUID().toString();
        String key =  randomString ;

        //file upload to AWS S3
        String aws_key = fileService.fileUploadToS3(file, bucket, key);

//        if(file.getSize() > MAX_SIZE) return null;

        return aws_key;
    }

    public Boolean extractExcel(MultipartFile file, int conf_id) throws IOException {

        try{
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            final int RESERVED_SKIP = 1;

            for (int rowIndex = RESERVED_SKIP; rowIndex <= 10000; rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);

                if (row != null) {

                    Cell cell0 = row.getCell(0);
                    if(cell0 == null || cell0.getStringCellValue() == "") continue;
                    String name = cell0.getStringCellValue();

                    Cell cell1 = row.getCell(1);
                    Cell cell2 = row.getCell(2);
                    Cell cell3 = row.getCell(3);
                    Cell cell4 = row.getCell(4);

                    String department = cell1.getStringCellValue();
                    String email = cell2.getStringCellValue();
                    String phone = cell3.getStringCellValue();
                    String pw = "";
                    if(cell4 == null){}
                    else if(cell4.getCellType()==0){
                        double _pw = cell4.getNumericCellValue();
                        pw = String.valueOf((int)_pw);
                    }else{
                        pw=cell4.getStringCellValue();
                    }

                    String encodedPW = passwordEncoder.encodePassword(pw, "temp");

                    SoConfTempMember tempMember = soConfTempMemberMapper.findByConfIdAndEmail(conf_id, email);
                    if(tempMember == null) {
                        tempMember = new SoConfTempMember();
                        tempMember.setConfId(conf_id);
                        tempMember.setEmail(email);
                        tempMember.setName(name);
                        tempMember.setPassword(encodedPW);
                        tempMember.setPhone(phone);
                        tempMember.setDepartment(department);

                        soConfTempMemberMapper.insert(tempMember);

                    } else {
                        tempMember.setEmail(email);
                        tempMember.setName(name);
                        tempMember.setPassword(encodedPW);
                        tempMember.setPhone(phone);
                        tempMember.setDepartment(department);
                        soConfTempMemberMapper.update(tempMember);
                    }

                    System.out.println(email +" " + name +" "+ phone);
                }
            }
            inputStream.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public Resource downloadExcelFile(String key, String storedFileName) throws IOException {

        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, key));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);


//        if(file.getSize() > MAX_SIZE) return null;

        Resource resource = new ByteArrayResource(bytes);

        return resource;
    }

}
