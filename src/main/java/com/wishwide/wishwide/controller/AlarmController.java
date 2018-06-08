package com.wishwide.wishwide.controller;


import com.wishwide.wishwide.domain.AlarmTemplate;
import com.wishwide.wishwide.persistence.alarm.CustomAlarmSendHistoryRepository;
import com.wishwide.wishwide.persistence.alarm.CustomAlarmSetRepository;
import com.wishwide.wishwide.persistence.alarm.CustomAlarmTemplateRepository;
import com.wishwide.wishwide.persistence.customer.CustomCustomerRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;

@Log
@Controller
@RequestMapping("/wishwide/alarm/")
public class AlarmController {
    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomAlarmTemplateRepository customAlarmTemplateRepository;

    @Autowired
    CustomAlarmSetRepository customAlarmSetRepository;

    @Autowired
    CustomCustomerRepository customCustomerRepository;

    @Autowired
    CustomAlarmSendHistoryRepository customAlarmSendHistoryRepository;

    /*알림 발송 설정*/

    //리스트
    @GetMapping("/listAlarmSet")
    public void listAlarmSet(@ModelAttribute("pageVO") PageVO pageVO,
                             HttpServletRequest request,
                             Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "alarmTemplateNo");

        Page<Object[]> result = customAlarmSetRepository.getAlarmSetPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getAlarmJoinCode(),
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmSetVO -> {
            log.info("알림 정보"+ Arrays.toString(alarmSetVO));
        });

        //알림템플릿 리스트
        model.addAttribute("alarmSetVO", new PageMaker<>(result));

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //알림수동발송 페이지
    @GetMapping("/registerAlarmSet")
    public void getRegisterAlarmSet(@ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        log.info("알림 수동 발송 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());
    }

    //알림수동발송
    @PostMapping("/postRegisterAlarmSet")
    public void postRegisterAlarmSet(@ModelAttribute("pageVO") PageVO pageVO,
                                    Model model) {
        log.info("알림 수동 발송 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());
    }

    //알림설정여부코드 변경
    @GetMapping("/updateAlarmVisibleCode/{alarmNo}/{alarmVisibleCode}")
    public ResponseEntity<String> updateAlarmVisibleCode(@PathVariable("alarmNo") Long alarmNo,
                                                         @PathVariable("alarmVisibleCode") int alarmVisibleCode) {
        log.info("알림설정여부코드 : " + alarmVisibleCode);

        String resultCode = "";

        //알림설정여부코드 변경
        customAlarmSetRepository.findById(alarmNo).ifPresent(alarm -> {
            if(alarmVisibleCode == 1)
                alarm.setAlarmVisibleCode(0);
            else
                alarm.setAlarmVisibleCode(1);

            customAlarmSetRepository.save(alarm);
        });

        if(alarmVisibleCode == 1)
            resultCode = "0";
        else
            resultCode = "1";

        return new ResponseEntity<>(resultCode,HttpStatus.CREATED);
    }


    //알림 메시지 가져오기
    @GetMapping("/selectAlarmMessage/{alarmNo}")
    public ResponseEntity<String> selectAlarmMessage(@PathVariable("alarmNo") Long alarmNo) {
        log.info("코드 : " + alarmNo);

        //알림메시지 가져오기
        String alarmMessage = customAlarmSetRepository.findById(alarmNo).get().getAlarmMessage();

        return new ResponseEntity<>(alarmMessage, HttpStatus.CREATED);
    }

    //알림 메시지 수정
    @GetMapping("/updateAlarmMessage/{alarmNo}")
    public ResponseEntity<String> updateAlarmMessage(@PathVariable("alarmNo") Long alarmNo,
                                                     @RequestParam("alarmMessage")String alarmMessage) {
        log.info("번호 : " + alarmNo);

        //알림메시지 수정
        customAlarmSetRepository.findById(alarmNo).ifPresent(alarm -> {
            alarm.setAlarmMessage(alarmMessage);

            customAlarmSetRepository.save(alarm);

            log.info("수정완료");
        });

        return new ResponseEntity<>("1", HttpStatus.CREATED);
    }

    //알림 템플릿 가져오기
    @GetMapping("/selectAlarmTemplatePreview")
    public ResponseEntity<List<Object[]>> selectAlarmTemplatePreview() {
        return new ResponseEntity<>(customAlarmSetRepository.getAlarmMessage(), HttpStatus.CREATED);
    }

    //매장 고객 리스트 가져오기
    @GetMapping("/selectStoreCustomer/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreCustomer(@PathVariable("storeId") String storeId,
                                                              @ModelAttribute("pageVO") PageVO pageVO) {
        Pageable pageable = pageVO.makePageable(0, "storeId");
        return new ResponseEntity<>(customCustomerRepository.getStoreCustomerList(storeId), HttpStatus.CREATED);
    }


    /*알림 템플릿*/

    //리스트
    @GetMapping("/listAlarmTemplate")
    public void listAlarmTemplate(@ModelAttribute("pageVO") PageVO pageVO,
                           Model model) {

        Pageable pageable = pageVO.makePageable(0, "alarmTemplateNo");

        Page<Object[]> result = customAlarmTemplateRepository.getAlarmTemplatePage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getAlarmJoinCode(),
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmTemplateVO -> {
            log.info("알림 템플릿 정보"+ Arrays.toString(alarmTemplateVO));
        });

        //알림템플릿 리스트
        model.addAttribute("alarmTemplateVO", new PageMaker<>(result));

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //등록
    @GetMapping("/registerAlarmTemplate")
    public void getRegisterAlarmTemplate(@ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        log.info("템플릿 추가 페이지");
    }

    @PostMapping("/postRegisterAlarmTemplate")
    public String postRegisterAlarmTemplate(@ModelAttribute("alarmTemplateVO") AlarmTemplate alarmTemplate,
                                            @ModelAttribute("pageVO") PageVO pageVO,
                                            RedirectAttributes redirectAttributes) {
        log.info("등록 데이터 : " + alarmTemplate);

        //VO에 세션값 세팅
        String alarmPurposeName = "";
        String alarmPurposeCode = "";
        alarmPurposeCode += alarmTemplate.getAlarmTypeCode();
        alarmPurposeCode += alarmTemplate.getAlarmPurposeCode();

        alarmPurposeName += setAlarmPurposeName(alarmTemplate.getAlarmTypeCode(), alarmTemplate.getAlarmPurposeCode());

        //알림목적이 '유효기간 만료'가 아닐 경우 발송시점은 전부 즉시로 설정
        if(!alarmTemplate.getAlarmPurposeCode().equals("3")) {
            alarmTemplate.setAlarmSendPointCode("IMME");
            alarmTemplate.setAlarmSendPointName("즉시");
        }
        else {
            alarmTemplate.setAlarmSendPointName(alarmTemplate.getAlarmSendPointCode() + "일전");
        }

        alarmTemplate.setAlarmPurposeCode(alarmPurposeCode);
        alarmTemplate.setAlarmPurposeName(alarmPurposeName);

        //알림 템플릿 정보 저장
        customAlarmTemplateRepository.save(alarmTemplate);

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("알림 템플릿 등록 성공");

        return "redirect:/wishwide/alarm/listAlarmTemplate";
    }

    //상세
    @GetMapping("/detailAlarmTemplate/{alarmTemplateNo}")
    public String detailAlarmTemplate(@PathVariable("alarmTemplateNo") Long alarmTemplateNo,
                                      @ModelAttribute("pageVO") PageVO pageVO,
                                      Model model) {
        //알림 템플릿 정보
        model.addAttribute("alarmTemplateVO", customAlarmTemplateRepository.getAlarmTemplateDetail(alarmTemplateNo));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        return "wishwide/alarm/detailAlarmTemplate";
    }

    //수정
    @PostMapping("/update")
    public String updateAlarmTemplate(@ModelAttribute("alarmTemplateVO") AlarmTemplate alarmTemplateVO,
                                            @ModelAttribute("pageVO") PageVO pageVO,
                                            RedirectAttributes redirectAttributes) {
        log.info("수정 데이터 : " + alarmTemplateVO);

        //VO에 세션값 세팅
        String alarmPurposeName = "";
        String alarmPurposeCode = "";
        alarmPurposeCode += alarmTemplateVO.getAlarmTypeCode();
        alarmPurposeCode += alarmTemplateVO.getAlarmPurposeCode();

        alarmPurposeName += setAlarmPurposeName(alarmTemplateVO.getAlarmTypeCode(), alarmTemplateVO.getAlarmPurposeCode());

        //알림목적이 '유효기간 만료'가 아닐 경우 발송시점은 전부 즉시로 설정
        if(!alarmTemplateVO.getAlarmPurposeCode().equals("3")) {
            alarmTemplateVO.setAlarmSendPointCode("IMME");
            alarmTemplateVO.setAlarmSendPointName("즉시");
        }
        else {
            alarmTemplateVO.setAlarmSendPointName(alarmTemplateVO.getAlarmSendPointCode() + "일전");
        }
        alarmTemplateVO.setAlarmPurposeCode(alarmPurposeCode);
        alarmTemplateVO.setAlarmPurposeName(alarmPurposeName);

        customAlarmTemplateRepository.findById(alarmTemplateVO.getAlarmTemplateNo()).ifPresent(alarmTemplate -> {
            alarmTemplate.setAlarmTypeCode(alarmTemplateVO.getAlarmTypeCode());
            alarmTemplate.setAlarmJoinCode(alarmTemplateVO.getAlarmJoinCode());
            alarmTemplate.setAlarmPurposeCode(alarmTemplateVO.getAlarmPurposeCode());
            alarmTemplate.setAlarmPurposeName(alarmTemplateVO.getAlarmPurposeName());
            alarmTemplate.setAlarmSendPointCode(alarmTemplateVO.getAlarmSendPointCode());
            alarmTemplate.setAlarmSendPointName(alarmTemplateVO.getAlarmSendPointName());
            alarmTemplate.setAlarmTargetTypeCode(alarmTemplateVO.getAlarmTargetTypeCode());
            alarmTemplate.setAlarmMessage(alarmTemplateVO.getAlarmMessage());

            //알림 템플릿 정보 수정
            customAlarmTemplateRepository.save(alarmTemplate);

            redirectAttributes.addFlashAttribute("message", "successUpdate");
            pageRedirectProperty(redirectAttributes, pageVO);
        });

        log.info("알림 템플릿 수정 성공");

        return "redirect:/wishwide/alarm/listAlarmTemplate";
    }


    //알림 메시지 가져오기
    @GetMapping("/selectAlarmTemplate/{alarmTemplateNo}")
    public ResponseEntity<String> selectAlarmTemplate(@PathVariable("alarmTemplateNo") Long alarmTemplateNo) {
        log.info("코드 : " + alarmTemplateNo);

        //알림메시지 가져오기
        String alarmMessage = customAlarmTemplateRepository.findById(alarmTemplateNo).get().getAlarmMessage();

        return new ResponseEntity<>(alarmMessage, HttpStatus.CREATED);
    }

    //알림 변수 가져오기
    @GetMapping("/selectAlarmVariable")
    public ResponseEntity<List<Object[]>> selectAlarmTemplate() {
        //알림메시지 가져오기
        List<Object[]> alarmVariableList = customAlarmTemplateRepository.getAlarmVariable();

        return new ResponseEntity<>(alarmVariableList, HttpStatus.CREATED);
    }

    /*알림발송내역*/

    //리스트
    @GetMapping("/listAlarmSendHistory")
    public void listAlarmSendHistory(@ModelAttribute("pageVO") PageVO pageVO,
                                     HttpServletRequest request,
                                     Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "alarmSendHistoryNo");

        Page<Object[]> result = customAlarmSendHistoryRepository.getAlarmSendHistoryPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getAlarmJoinCode(),
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmSetVO -> {
            log.info("알림발송내역 정보"+ Arrays.toString(alarmSetVO));
        });

        //알림발송내역 리스트
        model.addAttribute("alarmSendHistoryVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //알림발송내역 메시지 가져오기
    @GetMapping("/selectAlarmSendHistoryMessage/{alarmSendHistoryNo}")
    public ResponseEntity<String> selectAlarmSendHistoryMessage(@PathVariable("alarmSendHistoryNo") Long alarmSendHistoryNo) {
        log.info("코드 : " + alarmSendHistoryNo);

        //알림메시지 가져오기
        String alarmMessage = customAlarmSendHistoryRepository.findById(alarmSendHistoryNo).get().getAlarmMessage();

        return new ResponseEntity<>(alarmMessage, HttpStatus.CREATED);
    }


    /*메소드*/
    //알림목정명 set
    private String setAlarmPurposeName(String alarmTypeCode, String alarmPurposeCode){
        String returnValue = "";

        if(alarmTypeCode.equals("G")){
            returnValue += "선물 ";
        }
        else if(alarmTypeCode.equals("C")){
            returnValue += "쿠폰 ";
        }
        else if(alarmTypeCode.equals("S")){
            returnValue += "도장 ";
        }
        else if(alarmTypeCode.equals("P")){
            returnValue += "포인트 ";
        }

        if(alarmPurposeCode.equals("0")){
            returnValue += "도착";
        }
        else if(alarmPurposeCode.equals("1")){
            returnValue += "사용";
        }
        else if(alarmPurposeCode.equals("2")){
            returnValue += "구매";
        }
        else if(alarmPurposeCode.equals("3")){
            returnValue += "유효기간 만료";
        }
        else if(alarmPurposeCode.equals("4")){
            returnValue += "적립";
        }
        else if(alarmPurposeCode.equals("5")){
            returnValue += "차감";
        }

        return returnValue;
    }
}
