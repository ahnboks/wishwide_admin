package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.benefit.CustomBenefitRepository;
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
@RequestMapping("/wishwide/benefit/")
public class BenefitController {
    @Autowired
    CustomBenefitRepository customBenefitRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;


    //리스트
    @GetMapping("/listBenefit")
    public void listBenefit(@ModelAttribute("pageVO") PageVO pageVO,
                             HttpServletRequest request,
                             Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "membershipCustomerNo");

        Page<Object[]> result = customBenefitRepository.getBenefitPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmSetVO -> {
            log.info("혜택 정보"+ Arrays.toString(alarmSetVO));
        });

        //혜택조회 리스트
        model.addAttribute("benefitVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //도장내역 리스트 가져오기
    @GetMapping("/selectStampHistory/{storeId}/{membershipCustomerNo}")
    public ResponseEntity<List<Object[]>> selectStampHistory(@PathVariable("storeId") String storeId,
                                                             @PathVariable("membershipCustomerNo") Long membershipCustomerNo) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customBenefitRepository.getStampHistoryList(membershipCustomerNo,storeId), HttpStatus.CREATED);
    }

    //포인트내역 리스트 가져오기
    @GetMapping("/selectPointHistory/{storeId}/{membershipCustomerNo}")
    public ResponseEntity<List<Object[]>> selectPointHistory(@PathVariable("storeId") String storeId,
                                                             @PathVariable("membershipCustomerNo") Long membershipCustomerNo) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customBenefitRepository.getPointHistoryList(membershipCustomerNo,storeId), HttpStatus.CREATED);
    }
}
