package solis.pl.controller.zoom;

import javafx.util.Pair;
import org.apache.maven.artifact.transform.ArtifactTransformation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import solis.pl.repository.ZoomRoomMapper;
import solis.pl.service.zoomEnter.ZoomEnterService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/zoom")
public class ZoomEnterController {

    @Value("${zoom.apikey}")
    private String zoomAPIkey;

    @Value("${zoom.apisecret}")
    private String zoomAPIsecret;

    @Inject
    private ZoomEnterService zoomEnterService;

    @Inject
    private ZoomRoomMapper zoomRoomMapper;

    @RequestMapping("/zoomWebSDK")
    public String zoomwebSDK(@RequestParam(value="roomNum") String roomNum,
                             Model model) throws IOException {

        model.addAttribute("roomNum",roomNum);
        model.addAttribute("password",zoomRoomMapper.findPassword(roomNum));

        return "zoom/zoomWebSDK";
    }

    @RequestMapping("/dashboard")
    public String zoomDashboard(Model model) throws IOException {

        try{
            List<String> roomList = zoomRoomMapper.findAll();
            org.json.simple.JSONArray roomInfos = (JSONArray) zoomEnterService.getZoomRoomTopicAndRegistrantsCount(roomList);

            model.addAttribute("roomInfos", roomInfos);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "zoom/dashboard";
    }

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

    @RequestMapping("/getCnt")
    @ResponseBody
    public Object zoomGetCnt(){

        try{
            List<String> roomList = zoomRoomMapper.findAll();
            JSONObject cntValue = (JSONObject) zoomEnterService.timeIntervalCntValue(roomList);

            return (Object) cntValue;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/dashboard/generateSignature")
    @ResponseBody
    public String generateSignature(@RequestBody String meetingData,
                               Model model) throws IOException {

        try{

            JSONParser parser = new JSONParser();
            Object meetingDataParser = parser.parse(meetingData);
            JSONObject meetingDataJSON = (JSONObject) meetingDataParser;

            String signature = zoomEnterService.generateSignature(zoomAPIkey, zoomAPIsecret, (String) meetingDataJSON.get("meetingNumber"), 0);

            return signature;

        }catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }
}