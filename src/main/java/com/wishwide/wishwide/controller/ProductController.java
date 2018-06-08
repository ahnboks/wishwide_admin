package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.partner.CustomPartnerRepository;
import com.wishwide.wishwide.persistence.product.CustomProductRepository;
import com.wishwide.wishwide.persistence.product.GiftProductRepository;
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

@Log
@Controller
@RequestMapping("/wishwide/product/")
public class ProductController {
    @Autowired
    CustomProductRepository customProductRepository;

    @Autowired
    GiftProductRepository giftProductRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomPartnerRepository customPartnerRepository;

    //매장 리스트
    @GetMapping("/listStoreProduct")
    public void listStoreProduct(HttpServletRequest request,
                                 @ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "productNo");

        Page<Object[]> result = customProductRepository.getStoreProductPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getProductSellStatusCode(),
                pageVO.getGiftProductRegisterCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(product -> {
            log.info("매장 상품 정보"+ Arrays.toString(product));
        });

        //상품 리스트
        model.addAttribute("productVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //파트너 리스트
    @GetMapping("/listPartnerProduct")
    public void listPartnerProduct(HttpServletRequest request,
                                 @ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "productNo");

        Page<Object[]> result = customProductRepository.getPartnerProductPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getProductSellStatusCode(),
                pageVO.getGiftProductRegisterCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(product -> {
            log.info("파트너 상품 정보"+ Arrays.toString(product));
        });

        //상품 리스트
        model.addAttribute("productVO", new PageMaker<>(result));

        //파트너명 셀렉트 박스
        model.addAttribute("partnerNameList", customPartnerRepository.getPartnerNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //상품 판매 코드 변경
    @GetMapping("/updateProductSellStatusCode/{productNo}/{productSellStatusCode}")
    public ResponseEntity<String> updateServiceOperationCode(@PathVariable("productNo") Long productNo,
                                                             @PathVariable("productSellStatusCode") int productSellStatusCode) {
        log.info("판매 코드 : " + productSellStatusCode);

        String resultCode = "";

        //판매 코드 변경
        customProductRepository.findById(productNo).ifPresent(product -> {
            if(productSellStatusCode == 1)
                product.setProductSellStatusCode(0);
            else
                product.setProductSellStatusCode(1);

            customProductRepository.save(product);
        });

        if(productSellStatusCode == 1)
            resultCode = "0";
        else
            resultCode = "1";

        return new ResponseEntity<>(resultCode, HttpStatus.CREATED);
    }

}
