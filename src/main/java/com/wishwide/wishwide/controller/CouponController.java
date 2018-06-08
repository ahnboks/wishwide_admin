package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.coupon.CustomCouponBoxRepository;
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
@RequestMapping("/wishwide/coupon/")
public class CouponController {
    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomCouponBoxRepository customCouponBoxRepository;

    //리스트
    @GetMapping("/listCouponBox")
    public void listCouponBox(HttpServletRequest request,
                           @ModelAttribute("pageVO") PageVO pageVO,
                           Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "couponBoxNo");

        Page<Object[]> result = customCouponBoxRepository.getCouponBoxPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getCouponTypeCode(),
                pageVO.getCouponTargetTypeCode(),
                pageVO.getCouponPublishTypeCode(),
                pageVO.getCouponUseCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(device -> {
            log.info("쿠폰 발급 내역 정보"+ Arrays.toString(device));
        });

        //디바이스 리스트
        model.addAttribute("couponBoxVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }
}