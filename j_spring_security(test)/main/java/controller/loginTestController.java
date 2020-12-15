package solis.pl.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptXPathRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import solis.pl.domain.apiTest.GoogleOAuthRequest;
import solis.pl.domain.apiTest.GoogleOAuthResponse;
import solis.pl.domain.loginTest.loginTestUser;
import solis.pl.domain.loginTest.loginTestUserDetails;
import solis.pl.repository.LoginTestMapper;
import solis.pl.service.loginTest.LoginTestService;
import solis.pl.service.user.CustomPasswordEncoder;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/loginTest")
public class loginTestController {

    @Inject
    private CustomPasswordEncoder passwordEncoder;

    @Inject
    private LoginTestService loginTestService;

    @Inject
    private LoginTestMapper loginTestMapper;

    @Value("${google.apikey}")
    private String googleAPIkey;

    @Value("${google.apisecret}")
    private String googleAPIsecret;

    @RequestMapping
    @ResponseBody
    public String loginTest(){
        return"loginTest Home";
    }

    @RequestMapping("/signin")
    public String signin(Model model,
                         HttpSession session,
                         @RequestParam(value="signinError", required=false) String msg){

        return "loginTest/login";

    }

    @RequestMapping("/signout")
    public String signout(Model model, @RequestParam(value="signinError", required=false) String msg){

        model.addAttribute("msg", msg);
        return "redirect:/loginTest/login";
    }

    @RequestMapping(value = "/registration", method=RequestMethod.GET)
    public String registrationGET(){
        return "loginTest/registration-form";
    }

    @RequestMapping(value = "/registration", method=RequestMethod.POST)
    public String registrationPOST(@RequestParam (value="email")String email,
                                   @RequestParam (value="psw") String password){


        try{
            if(loginTestMapper.getUserById("email") != null){
                return "redirct:/loginTest/registration";
            }

            loginTestUserDetails register = new loginTestUserDetails();

            register.setID(email);
            register.setNAME(email);
            register.setAUTHORITY("ROLE_USER");
            register.setPW(password);

            loginTestMapper.insert(register);

            return "redirect:/loginTest/signin";

        }catch(Exception e){
            e.printStackTrace();
            return "redirect:/loginTest/registration";
        }

    }

    @RequestMapping("/loginResult")
    public String loginResult(Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        loginTestUserDetails user = (loginTestUserDetails) principal;

        if(user.getAUTHORITY().equalsIgnoreCase("ROLE_ADMIN"))
            return "redirect:/loginTest/admin";

        model.addAttribute("user", user);

      return "loginTest/loginResult";
    }

    @RequestMapping("/admin")
    public String adminPage(Model model){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);

        loginTestUserDetails user = (loginTestUserDetails) principal;

        model.addAttribute("user", user);

        return "loginTest/admin";
    }

    @RequestMapping(value = "/google-redirect", method = RequestMethod.GET)
    @ResponseBody
    public String googleRedirectCodeGet(@RequestParam(value = "code") String code,
                                        HttpSession session,
                                        Model model) throws IOException {

        Map<String, String> userInfo = loginTestService.googleRedirectService(googleAPIkey, googleAPIsecret, code);

        if(userInfo == null){
            return "<script>window.close();</script>";
        }

        return "<script>window.close();</script>";

    }

}
