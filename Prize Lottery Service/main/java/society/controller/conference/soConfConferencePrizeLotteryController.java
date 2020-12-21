package thinkonweb.ml.society.controller.conference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.net.HttpHeaders;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scala.util.parsing.json.JSON;
import thinkonweb.ml.common.service.user.CustomPasswordEncoder;
import thinkonweb.ml.society.domain.conference.SoConfConference;
import thinkonweb.ml.society.domain.conference.SoConfTempMember;
import thinkonweb.ml.society.domain.society.Society;
import thinkonweb.ml.society.repository.UserClassCodeMapper;
import thinkonweb.ml.society.repository.conference.*;
import thinkonweb.ml.society.service.*;
import thinkonweb.ml.society.service.conference.*;
import thinkonweb.ml.society.service.user.SocietyUserService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes({"tempMember"})
@RequestMapping("/society/{societyAbbr}/conference/{nameId}/prizeLottery")
public class soConfConferencePrizeLotteryController {
    @Inject
    private SocietyService societyService;
    @Inject
    private SoConfConferenceService soConfConferenceService;
    @Inject
    private SoConfPartnerService soConfPartnerService;
    @Inject
    private SocietyUserService societyUserService;
    @Inject
    private UserClassCodeMapper userClassCodeMapper;
    @Inject
    private SoConfRegistrantMapper soConfRegistrantMapper;
    @Inject
    private SoConfRegistrantService soConfRegistrantService;
    @Inject
    private SoConfTempMemberMapper soConfTempMemberMapper;
    @Inject
    private SoConfFeeService soConfFeeService;
    @Inject
    private SocietyFeeService societyFeeService;
    @Inject
    private SoConfCommentMapper soConfCommentMapper;
    @Inject
    private SoConfManuscriptMapper soConfManuscriptMapper;
    @Inject
    private CustomPasswordEncoder passwordEncoder;
    @Inject
    private SocietyEmailService societyEmailService;
    @Inject
    private SoConfAnnouncementService soConfAnnouncementService;
    @Inject
    private SocietyActivityLogService societyActivityLogService;
    @Inject
    private SoConfActivityLogMapper soConfActivityLogMapper;
    @Inject
    private SocietyLotteryService societyLotteryService;
    @Value("${aws.s3.societyBucketName}")
    private String soConfAwsBucket;

    @Value("${super.test}")
    private boolean superTest;

    @ModelAttribute("society")
    public Society getSociety(@PathVariable String societyAbbr) {
        return societyService.getSocietyByAbbr(societyAbbr);
    }

    @ModelAttribute("soConfConference")
    public SoConfConference getSoConfConference(@ModelAttribute("society") Society society, @PathVariable("nameId") String nameId) {
        return soConfConferenceService.getSoConfConferenceByNameId(society, nameId);
    }

    @RequestMapping("")
    public String prizeLottery(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                               @ModelAttribute("soConfConference") SoConfConference soConfConference, Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        model.addAttribute("soConfConference", soConfConference);
        model.addAttribute("soConfConferenceMainImageList", soConfConferenceService.getSoConfConferenceMainImages(soConfConference));
        model.addAttribute("soConfConferenceDivControl", soConfConferenceService.getSoConfConferenceDivControl(soConfConference.getId()));
        model.addAttribute("soConfConferenceContactList", soConfConferenceService.getSoConfConferenceContactList(society, soConfConference));
        return String.format("society/conference/%s/prizeLottery", soConfConference.getViewType());
    }

    @RequestMapping("/setting")
    public String lotterySetting(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                                 @ModelAttribute("soConfConference") SoConfConference soConfConference,
                                 Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        //from Excel
        List<SoConfTempMember> listExcelRegistrants = soConfTempMemberMapper.findByConfId(soConfConference.getId());

        //from zoom
        Object listWebinars = societyLotteryService.getZoomRoomList("support@manuscriptlink.com");


        model.addAttribute("listExcelRegistrants", listExcelRegistrants);
        model.addAttribute("listWebinars", listWebinars);
        model.addAttribute("soConfConference", soConfConference);
        model.addAttribute("soConfConferenceMainImageList", soConfConferenceService.getSoConfConferenceMainImages(soConfConference));
        model.addAttribute("soConfConferenceDivControl", soConfConferenceService.getSoConfConferenceDivControl(soConfConference.getId()));
        model.addAttribute("soConfConferenceContactList", soConfConferenceService.getSoConfConferenceContactList(society, soConfConference));
        return String.format("society/conference/%s/lotterySetting", soConfConference.getViewType());
    }

    @RequestMapping(value="/setting", method= RequestMethod.POST)
    public String lotterySettingPOST(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                                     @ModelAttribute("soConfConference") SoConfConference soConfConference,
                                     @ModelAttribute("chooseZoomMail") String[] chooseZoomMail,
                                     Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        System.out.println(chooseZoomMail);

        model.addAttribute("soConfConference", soConfConference);
        model.addAttribute("soConfConferenceMainImageList", soConfConferenceService.getSoConfConferenceMainImages(soConfConference));
        model.addAttribute("soConfConferenceDivControl", soConfConferenceService.getSoConfConferenceDivControl(soConfConference.getId()));
        model.addAttribute("soConfConferenceContactList", soConfConferenceService.getSoConfConferenceContactList(society, soConfConference));
        return String.format("society/conference/%s/lotterySetting", soConfConference.getViewType());
    }

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


//        JSONParser parser = new JSONParser();
//        String registrants_parser = new Gson().toJson(registrants);

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

    @RequestMapping(value="/setting/zoomRoom/ajax", method=RequestMethod.GET)
    @ResponseBody
    public Object lotterySettingAJAX(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                                     @ModelAttribute("soConfConference") SoConfConference soConfConference,
                                     @RequestParam("zoomId") String zoomId,
                                     Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        System.out.println("controller:"+zoomId);

        Object zoomRegistrants = societyLotteryService.getZoomRegistrantsList(zoomId);

        return zoomRegistrants;
    }

    @RequestMapping(value="/setting/zoomRoom/ajax/submit", method = RequestMethod.POST)
    @ResponseBody
    public String lotterySettingSubmit(@PathVariable String societyAbbr, @ModelAttribute("society") Society society,
                                     @ModelAttribute("soConfConference") SoConfConference soConfConference,
                                     @RequestParam String zoomRoomList,
                                     Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        boolean success_flag = societyLotteryService.insertZoomRegistrants(zoomRoomList, soConfConference.getId());

        return String.valueOf(success_flag);
    }

    @RequestMapping(value="/excelUpload", method=RequestMethod.POST)
    public String ExcelUpload(@PathVariable String societyAbbr, @PathVariable("nameId") String nameId,
                              @RequestParam("file") MultipartFile multipartFile,
                              @ModelAttribute("soConfConference") SoConfConference soConfConference,
                              HttpServletRequest request,
                              Model model) {
        if (soConfConference == null) {
            return String.format("redirect:/society/%s", societyAbbr);
        }

        Boolean confirm = false;
        try {
            confirm = societyLotteryService.extractExcel(multipartFile, soConfConference.getId());
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println(confirm);

//        String key = societyLotteryService.uploadExcelFile(multipartFile, 11);
//        File targetFile = new File("/home1/irteam/"+multipartFile.getOriginalFilename());
//        try{
//            InputStream fileStream = multipartFile.getInputStream();
//            FileUtils.copyInputStreamToFile(fileStream, targetFile);
//        } catch(IOException e){
//            FileUtils.deleteQuietly(targetFile);
//            e.printStackTrace();
//        }

        return "redirect:/society/" + societyAbbr + "/conference/" + nameId + "/prizeLottery/setting";
    }

    @RequestMapping(value="/excelDownload")
    @ResponseBody
    public ResponseEntity ExcelDownload(@PathVariable String societyAbbr, @PathVariable("nameId") String nameId,
                                @RequestParam("key") String key,
                                @RequestParam("filename") String filename,
                              @ModelAttribute("soConfConference") SoConfConference soConfConference,
                              HttpServletRequest request,
                              Model model) {

        try{
            Resource resource =  societyLotteryService.downloadExcelFile(key,filename);

            String contentType = null;
            try{
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            }catch(IOException ex){
                System.out.println("could not determine file type.");
            }

            if(contentType == null){
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                    .body(resource);

        }catch(IOException e){
            e.printStackTrace();
        }

//        File targetFile = new File("/home1/irteam/"+multipartFile.getOriginalFilename());
//        try{
//            InputStream fileStream = multipartFile.getInputStream();
//            FileUtils.copyInputStreamToFile(fileStream, targetFile);
//        } catch(IOException e){
//            FileUtils.deleteQuietly(targetFile);
//            e.printStackTrace();
//        }

        return null;
    }
}