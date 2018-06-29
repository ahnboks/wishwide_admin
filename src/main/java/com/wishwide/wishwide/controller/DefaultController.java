package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.LoginInfoRepository;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Log
@Controller
public class DefaultController {
    @Autowired
    LoginInfoRepository loginInfoRepository;

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
            model.addAttribute("message", "아이디와 비밀번호를 다시 입력해주세요.");

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

    @PostMapping("/login/checkSign")
    public ResponseEntity<Integer> checkSignUp(@RequestBody String userId,
                                               RedirectAttributes redirectAttributes){
        int checkResult = loginInfoRepository.checkSignId(userId);

        log.info("아이디값 :"+userId+ " 결과 값: "+checkResult);

        return new ResponseEntity<>(checkResult, HttpStatus.CREATED);
    }

    public static void pageRedirectProperty(RedirectAttributes redirectAttributes, PageVO pageVO) {
        redirectAttributes.addAttribute("page", pageVO.getPage());
        redirectAttributes.addAttribute("size", pageVO.getSize());
        redirectAttributes.addAttribute("keyword", pageVO.getKeyword());
        redirectAttributes.addAttribute("type", pageVO.getType());
        redirectAttributes.addAttribute("visibleCode", pageVO.getVisibleCode());
    }
}
