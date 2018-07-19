package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.coupon.CustomCouponBoxRepository;
import com.wishwide.wishwide.persistence.customer.CustomCustomerRepository;
import com.wishwide.wishwide.persistence.gift.CustomGiftBoxRepository;
import com.wishwide.wishwide.persistence.gift.CustomGiftPaymentRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import com.wishwide.wishwide.vo.PageMaker;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Log
@Controller
@RequestMapping("/wishwide/customer/")
public class CustomerController {
    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomCustomerRepository customCustomerRepository;

    @Autowired
    CustomGiftPaymentRepository customGiftPaymentRepository;

    @Autowired
    CustomGiftBoxRepository customGiftBoxRepository;

    @Autowired
    CustomCouponBoxRepository customCouponBoxRepository;

    //리스트
    @GetMapping("/listCustomer")
    public void listCustomer(HttpServletRequest request,
                              @ModelAttribute("pageVO") PageVO pageVO,
                              Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "membershipCustomerNo");

        Page<Object[]> result = customCustomerRepository.getCustomerPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getCustomerBenefitTypeCode(),
                pageVO.getCustomerGradeTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(device -> {
            log.info("고객 정보"+ Arrays.toString(device));
        });

        // 리스트
        model.addAttribute("customerVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //선물거래내역 리스트 가져오기
    @GetMapping("/selectCustomerGiftPayment/{customerNo}")
    public ResponseEntity<List<Object[]>> selectStoreGiftPayment(@PathVariable("customerNo") Long customerNo) {
        log.info("코드 : " + customerNo);
        return new ResponseEntity<>(customGiftPaymentRepository.getCustomerGiftPaymentList(customerNo), HttpStatus.CREATED);
    }

    //선물 리스트 가져오기
    @GetMapping("/selectCustomerGiftBox/{customerNo}")
    public ResponseEntity<List<Object[]>> selectCustomerGiftBox(@PathVariable("customerNo") Long customerNo) {
        log.info("코드 : " + customerNo);
        return new ResponseEntity<>(customGiftBoxRepository.getCustomerGiftBox(customerNo), HttpStatus.CREATED);
    }

    //쿠폰 리스트 가져오기
    @GetMapping("/selectCustomerCouponBox/{customerNo}")
    public ResponseEntity<List<Object[]>> selectCustomerCouponBox(@PathVariable("customerNo") Long customerNo) {
        log.info("코드 : " + customerNo);
        return new ResponseEntity<>(customCouponBoxRepository.getCustomerCouponBoxList(customerNo), HttpStatus.CREATED);
    }
}
