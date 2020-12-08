package solis.pl.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solis.pl.domain.loginTest.loginTestUser;
import solis.pl.domain.loginTest.loginTestUserDetails;

import java.security.Principal;
import java.util.Collection;

@Controller
@RequestMapping("/loginTest")
public class loginTestController {

    @RequestMapping
    @ResponseBody
    public String loginTest(){


        return"loginTest Home";
    }

    @RequestMapping("/signup")
    public String signup(Model model, @RequestParam(value="signinError", required=false) String msg){


        System.out.println(msg);
        model.addAttribute("msg", msg);
        return "loginTest/login";

    }

    @RequestMapping("/loginResult")
    public String loginResult(Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loginTestUserDetails user = (loginTestUserDetails) principal;

        model.addAttribute("user", user);

      return "loginTest/loginResult";
    }

    @RequestMapping("/admin")
    public String adminPage(Model model){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loginTestUserDetails user = (loginTestUserDetails) principal;

        model.addAttribute("user", user);

        return "loginTest/admin";
    }

}
