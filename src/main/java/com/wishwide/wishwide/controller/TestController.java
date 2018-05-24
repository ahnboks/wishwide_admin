package com.wishwide.wishwide.controller;

import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Log
@Controller
public class TestController {
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        log.info("index");
        log.info("ddd");

        //세션 등록 되어있으면 메인페이지로
        String url = "";

        HttpSession session = request.getSession();

        if(session.getAttribute("userId") != null){
            log.info("세션 유지 중"+session.getAttribute("userId"));
            url = "main";
        }
        else{
            log.info("세션 소멸"+session.getAttribute("userId"));
            url = "redirect:/login";
        }

       return url;
    }

    @GetMapping("/login")
    public void login(@RequestParam(value = "error", required = false) String error,
                      @RequestParam(value = "logout", required = false) String logout,
                      Model model) {
//        log.info("경로 : "+path);
        log.info("로그인" + error + logout);

        //로그인 실패 시
        if (error!=null && error.equals("true"))
            model.addAttribute("message", "로그인에 실패하였습니다.");

        //로그아웃 시
       if(logout!=null && logout.equals("true"))
            model.addAttribute("message", "정상적으로 로그아웃 되었습니다.");
    }

    @RequestMapping("/wishwide")
    public String main() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("connect 함" + auth.getName());
        return "main";
    }

    @GetMapping("/auth/accessDenied")
    public void accessDenied() {

    }
}
