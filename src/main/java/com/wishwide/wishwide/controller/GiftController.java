package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.gift.CustomGiftPaymentRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import com.wishwide.wishwide.vo.PageMaker;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Log
@Controller
@RequestMapping("/wishwide/gift/")
public class GiftController {
    @Autowired
    CustomGiftPaymentRepository customGiftPaymentRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    //리스트
    @GetMapping("/listGiftPayment")
    public void listGiftPayment(HttpServletRequest request,
                           @ModelAttribute("pageVO") PageVO pageVO,
                           Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "giftPaymentNo");

        Page<Object[]> result = customGiftPaymentRepository.getGiftPaymentPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getGiftPaymentStatusCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(giftPayment -> {
            log.info("선물결제 정보"+ Arrays.toString(giftPayment));
        });

        //선물결제 리스트
        model.addAttribute("giftPaymentVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }
}
