package com.wishwide.wishwide.controller;


import com.wishwide.wishwide.alarm.AlarmManager;
import com.wishwide.wishwide.domain.*;
import com.wishwide.wishwide.persistence.alarm.*;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    MsgQueueRepository msgQueueRepository;

    @Autowired
    AlarmSendLogRepository alarmSendLogRepository;

    @Autowired
    private AlarmManager alarmManager;

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

        log.info("세션 : " + sessionId + roleCode);

        Pageable pageable = pageVO.makePageable(0, "alarmTpNo");

        Page<Object[]> result = customAlarmSetRepository.getAlarmSetPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmSetVO -> {
            log.info("알림 정보" + Arrays.toString(alarmSetVO));
        });

        //알림템플릿 리스트
        model.addAttribute("alarmSetVO", new PageMaker<>(result));

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //알림수동발송 페이지
    @GetMapping("/registerAlarmSet")
    public void getRegisterAlarmSet(@ModelAttribute("pageVO") PageVO pageVO,
                                    HttpServletRequest request,
                                    Model model) {
        log.info("알림 수동 발송 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //세션
        HttpSession session = request.getSession();

        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : " + sessionId + roleCode);

        if (roleCode.equals("ST")) {
            //가맹점아이디
            model.addAttribute("storeId", sessionId);
        }

    }

    //알림수동발송
    @PostMapping("/postRegisterAlarmSet")
    public String postRegisterAlarmSet(@ModelAttribute("alarmSendHistoryVO") AlarmSendHistory alarmSendHistoryVO,
                                       @RequestParam(value = "reserveDate", required = false) String reserveDate,
                                       @RequestParam(value = "reserveTime", required = false) String reserveTime,
                                       @RequestParam("membershipCustomerNo") List<Long> membershipCustomerNoList,
                                       @ModelAttribute("pageVO") PageVO pageVO,
                                       RedirectAttributes redirectAttributes) {
        log.info("데이터 : " + alarmSendHistoryVO + ", 날짜 : " + reserveDate + reserveTime + ", 고객" + membershipCustomerNoList);


        if (membershipCustomerNoList != null) {
            for (Long membershipCustomerNo : membershipCustomerNoList) {
                MembershipCustomer membershipCustomer = customCustomerRepository.findById(membershipCustomerNo).get();

                String message = setAlarmMessage(alarmSendHistoryVO.getAshAlarmMessage(), membershipCustomer, alarmSendHistoryVO.getStoreId());

                //내역 저장
                AlarmSendHistory alarmSendHistory = new AlarmSendHistory();
                alarmSendHistory.setMembershipCustomerNo(membershipCustomerNo);
                alarmSendHistory.setAshCustomerPhone(membershipCustomer.getMembershipCustomerPhone());
                alarmSendHistory.setAshCustomerGradeTypeCode(membershipCustomer.getMembershipCustomerGradeTypeCode());
                alarmSendHistory.setAshCustomerName(membershipCustomer.getMembershipCustomerName());
                alarmSendHistory.setAshAlarmMessage(message);
                alarmSendHistory.setStoreId(alarmSendHistoryVO.getStoreId());
                alarmSendHistory.setAshAlarmSendTypeCode(alarmSendHistoryVO.getAshAlarmSendTypeCode());
                alarmSendHistory.setAshAlarmSendWayCode(alarmSendHistoryVO.getAshAlarmSendWayCode());
                alarmSendHistory.setAshAlarmPurposeCode("ETC");
                alarmSendHistory.setAshAlarmPurposeName("기타");
                alarmSendHistory.setAshAlarmSendPointCode("IMME");
                alarmSendHistory.setAshAlarmSendPointName("즉시");
                alarmSendHistory.setAshAlarmPurposeName("기타");
                alarmSendHistory.setAshAlarmTargetTypeCode("RC");
                alarmSendHistory.setAshAlarmTypeCode("ETC");

                if (reserveDate != "" && reserveTime != "") {
                    LocalDateTime alarmReservationTime = LocalDateTime.parse(reserveDate + " " + reserveTime, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
                    alarmSendHistory.setAshAlarmReservationTime(alarmReservationTime);
                }

                customAlarmSendHistoryRepository.save(alarmSendHistory);

                //로그 저장
                AlarmSendLog alarmSendLog = new AlarmSendLog();
                alarmSendLog.setMembershipCustomerNo(membershipCustomerNo);
                alarmSendLog.setAslCustomerPhone(membershipCustomer.getMembershipCustomerPhone());
                alarmSendLog.setAslCustomerGradeTypeCode(membershipCustomer.getMembershipCustomerGradeTypeCode());
                alarmSendLog.setAslCustomerName(membershipCustomer.getMembershipCustomerName());
                alarmSendLog.setAslAlarmMessage(message);
                alarmSendLog.setStoreId(alarmSendHistoryVO.getStoreId());
                alarmSendLog.setAslAlarmSendTypeCode(alarmSendHistoryVO.getAshAlarmSendTypeCode());
                alarmSendLog.setAslAlarmSendWayCode(alarmSendHistoryVO.getAshAlarmSendWayCode());
                alarmSendLog.setAslAlarmPurposeCode("ETC");
                alarmSendLog.setAslAlarmPurposeName("기타");
                alarmSendLog.setAslAlarmSendPointCode("IMME");
                alarmSendLog.setAslAlarmSendPointName("즉시");
                alarmSendLog.setAslAlarmPurposeName("기타");
                alarmSendLog.setAslAlarmTargetTypeCode("RC");
                alarmSendLog.setAslAlarmTypeCode("ETC");

                if (reserveDate != "" && reserveTime != "") {
                    LocalDateTime alarmReservationTime = LocalDateTime.parse(reserveDate + " " + reserveTime, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
                    alarmSendLog.setAslAlarmReservationTime(alarmReservationTime);
                }

                alarmSendLogRepository.save(alarmSendLog);

                MsgQueue msgQueue = new MsgQueue();
                msgQueue.setMsg_type("3");
                msgQueue.setFilecnt(0);
                msgQueue.setDstaddr(membershipCustomer.getMembershipCustomerPhone());
                msgQueue.setCallback("01088041229");
                msgQueue.setText(alarmSendHistoryVO.getAshAlarmMessage());

                if (reserveDate != "" && reserveTime != "")
                    msgQueue.setRequest_time(LocalDateTime.parse(reserveDate + " " + reserveTime, DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
                else
                    msgQueue.setRequest_time(LocalDateTime.now());
                msgQueue.setStat("0");
                msgQueue.setText(message);

                msgQueueRepository.save(msgQueue);

                redirectAttributes.addFlashAttribute("message", "successRegister");
                pageRedirectProperty(redirectAttributes, pageVO);

                log.info("알림 발송 성공");
            }
        }
        return "redirect:/wishwide/alarm/listAlarmSet";
    }

    //알림설정여부코드 변경
    @GetMapping("/updateAlarmVisibleCode/{alarmNo}/{alarmVisibleCode}")
    public ResponseEntity<String> updateAlarmVisibleCode(@PathVariable("alarmNo") Long alarmNo,
                                                         @PathVariable("alarmVisibleCode") int alarmVisibleCode) {
        log.info("알림설정여부코드 : " + alarmVisibleCode);

        String resultCode = "";

        //알림설정여부코드 변경
        customAlarmSetRepository.findById(alarmNo).ifPresent(alarm -> {
            if (alarmVisibleCode == 1)
                alarm.setAlarmVisibleCode(0);
            else
                alarm.setAlarmVisibleCode(1);

            customAlarmSetRepository.save(alarm);
        });

        if (alarmVisibleCode == 1)
            resultCode = "0";
        else
            resultCode = "1";

        return new ResponseEntity<>(resultCode, HttpStatus.CREATED);
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
                                                     @RequestParam("alarmMessage") String alarmMessage) {
        log.info("번호 : " + alarmNo + " 알림메시지 : " + alarmMessage);

        //알림메시지 수정
        customAlarmSetRepository.findById(alarmNo).ifPresent(alarm -> {
            alarm.setAlarmMessage(alarmMessage);
            alarm.setAlarmMessageUpdateCode(1);

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
        return new ResponseEntity<>(customCustomerRepository.getStoreCustomerList(storeId), HttpStatus.CREATED);
    }

    //메시지 테스트 발송
    @GetMapping("/sendTestAlarm")
    public ResponseEntity<String> sendTestAlarm(@RequestParam("storeId") String storeId,
                                                @RequestParam("dstaddr") String dstaddr,
                                                @RequestParam("alarmMessage") String alarmMessage) {
        log.info("데이터 : " + storeId + ", " + dstaddr + "," + alarmMessage);

        if (alarmMessage.contains("#{매장명}")) {
            System.out.println("1");
            alarmMessage = alarmMessage.replace("#{매장명}", customStoreRepository.findById(storeId).get().getStoreName());
        }
        if (alarmMessage.contains("#{수신자전화번호}")) {
            System.out.println("2");
            alarmMessage = alarmMessage.replace("#{수신자전화번호}", dstaddr);
        }

        System.out.println(alarmMessage);

        MsgQueue msgQueue = new MsgQueue();
        msgQueue.setDstaddr(dstaddr);
        msgQueue.setCallback("01088041229");
        msgQueue.setText(alarmMessage);
        msgQueue.setRequest_time(LocalDateTime.now());
        msgQueue.setStat("0");

        msgQueueRepository.save(msgQueue);

        return new ResponseEntity<>("1", HttpStatus.CREATED);
    }


    /*알림 템플릿*/

    //리스트
    @GetMapping("/listAlarmTemplate")
    public void listAlarmTemplate(@ModelAttribute("pageVO") PageVO pageVO,
                                  Model model) {

        Pageable pageable = pageVO.makePageable(0, "alarmTpNo");

        Page<Object[]> result = customAlarmTemplateRepository.getAlarmTemplatePage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmTemplateVO -> {
            log.info("알림 템플릿 정보" + Arrays.toString(alarmTemplateVO));
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
    public String postRegisterAlarmTemplate(@ModelAttribute("alarmTemplateVO") AlarmTemplate alarmTemplateVO,
                                            @ModelAttribute("pageVO") PageVO pageVO,
                                            RedirectAttributes redirectAttributes) {
        log.info("등록 데이터 : " + alarmTemplateVO);

        //VO에 세션값 세팅
        String alarmPurposeName = "";
        String alarmPurposeCode = "";
        alarmPurposeCode += alarmTemplateVO.getAlarmTpPurposeCode();
        alarmPurposeCode += alarmTemplateVO.getAlarmTpPurposeCode();

        alarmPurposeName += setAlarmPurposeName(alarmTemplateVO.getAlarmTpTypeCode(), alarmTemplateVO.getAlarmTpPurposeCode());

        //알림목적이 '유효기간 만료'가 아닐 경우 발송시점은 전부 즉시로 설정
        if (!alarmTemplateVO.getAlarmTpPurposeCode().equals("3")) {
            alarmTemplateVO.setAlarmTpSendPointCode("IMME");
            alarmTemplateVO.setAlarmTpSendPointName("즉시");
        } else {
            alarmTemplateVO.setAlarmTpSendPointName(alarmTemplateVO.getAlarmTpSendPointCode() + "일전");
        }

        if (alarmTemplateVO.getAlarmTpPurposeCode().equals("6")) {
            alarmTemplateVO.setAlarmTpPurposeCode("ETC");
            alarmTemplateVO.setAlarmTpPurposeName("신규가입");
        } else {
            alarmTemplateVO.setAlarmTpPurposeCode(alarmPurposeCode);
            alarmTemplateVO.setAlarmTpPurposeName(alarmPurposeName);
        }

        //알림 템플릿 정보 저장
        AlarmTemplate alarmTemplate = customAlarmTemplateRepository.save(alarmTemplateVO);

        //매장 알림발송설정으로 자동저장
        customStoreRepository.getStoreList().forEach(storeList -> {
            Alarm alarm = new Alarm();
            alarm.setAlarmMessage(alarmTemplate.getAlarmTpMessage());
            alarm.setAlarmPurposeName(alarmTemplate.getAlarmTpPurposeName());
            alarm.setAlarmSendPointName(alarmTemplate.getAlarmTpSendPointName());
            alarm.setAlarmSendTypeCode("IMME");
            alarm.setAlarmSendWayCode("MESSAGE");
            alarm.setAlarmTargetTypeCode(alarmTemplate.getAlarmTpTargetTypeCode());
            alarm.setAlarmTpNo(alarmTemplate.getAlarmTpNo());
            alarm.setAlarmTypeCode(alarmTemplate.getAlarmTpTypeCode());
            alarm.setStoreId(storeList[0].toString());
            alarm.setAlarmPurposeCode(alarmTemplate.getAlarmTpPurposeCode());
            alarm.setAlarmSendPointCode(alarmTemplate.getAlarmTpSendPointCode());
            alarm.setAlarmVisibleCode(0);

            Long alarmNo = customAlarmSetRepository.save(alarm).getAlarmNo();

            //알림메시지 알림변수값 저장
            alarmManager.setAlarmMessageVariable(alarm.getAlarmMessage(), alarmNo, storeList[0].toString());

            log.info("알림발송설정 등록 성공");
        });

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("알림 템플릿 등록 성공");

        return "redirect:/wishwide/alarm/listAlarmTemplate";
    }

    //상세
    @GetMapping("/detailAlarmTemplate/{alarmTpNo}")
    public String detailAlarmTemplate(@PathVariable("alarmTpNo") Long alarmTpNo,
                                      @ModelAttribute("pageVO") PageVO pageVO,
                                      Model model) {
        //알림 템플릿 정보
        model.addAttribute("alarmTemplateVO", customAlarmTemplateRepository.getAlarmTemplateDetail(alarmTpNo));

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
        alarmPurposeCode += alarmTemplateVO.getAlarmTpTypeCode();
        alarmPurposeCode += alarmTemplateVO.getAlarmTpPurposeCode();

        alarmPurposeName += setAlarmPurposeName(alarmTemplateVO.getAlarmTpTypeCode(), alarmTemplateVO.getAlarmTpPurposeCode());

        //알림목적이 '유효기간 만료'가 아닐 경우 발송시점은 전부 즉시로 설정
        if (!alarmTemplateVO.getAlarmTpPurposeCode().equals("3")) {
            alarmTemplateVO.setAlarmTpSendPointCode("IMME");
            alarmTemplateVO.setAlarmTpSendPointName("즉시");
        } else {
            alarmTemplateVO.setAlarmTpSendPointName(alarmTemplateVO.getAlarmTpSendPointCode() + "일전");
        }
        alarmTemplateVO.setAlarmTpPurposeCode(alarmPurposeCode);
        alarmTemplateVO.setAlarmTpPurposeName(alarmPurposeName);

        customAlarmTemplateRepository.findById(alarmTemplateVO.getAlarmTpNo()).ifPresent(alarmTemplate -> {
            alarmTemplate.setAlarmTpTypeCode(alarmTemplateVO.getAlarmTpTypeCode());
            alarmTemplate.setAlarmTpPurposeCode(alarmTemplateVO.getAlarmTpPurposeCode());
            alarmTemplate.setAlarmTpPurposeName(alarmTemplateVO.getAlarmTpPurposeName());
            alarmTemplate.setAlarmTpSendPointCode(alarmTemplateVO.getAlarmTpSendPointCode());
            alarmTemplate.setAlarmTpSendPointName(alarmTemplateVO.getAlarmTpSendPointName());
            alarmTemplate.setAlarmTpTargetTypeCode(alarmTemplateVO.getAlarmTpTargetTypeCode());
            alarmTemplate.setAlarmTpMessage(alarmTemplateVO.getAlarmTpMessage());

            //알림 템플릿 정보 수정
            customAlarmTemplateRepository.save(alarmTemplate);

            redirectAttributes.addFlashAttribute("message", "successUpdate");
            pageRedirectProperty(redirectAttributes, pageVO);
        });

        log.info("알림 템플릿 수정 성공");

        return "redirect:/wishwide/alarm/listAlarmTemplate";
    }


    //알림 메시지 가져오기
    @GetMapping("/selectAlarmTemplate/{alarmTpNo}")
    public ResponseEntity<String> selectAlarmTemplate(@PathVariable("alarmTpNo") Long alarmTpNo) {
        log.info("코드 : " + alarmTpNo);

        //알림메시지 가져오기
        String alarmMessage = customAlarmTemplateRepository.findById(alarmTpNo).get().getAlarmTpMessage();

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

        log.info("세션 : " + sessionId + roleCode);

        Pageable pageable = pageVO.makePageable(0, "alarmSendHistoryNo");

        Page<Object[]> result = customAlarmSendHistoryRepository.getAlarmSendHistoryPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getAlarmTypeCode(),
                pageVO.getAlarmPurposeCode(),
                pageVO.getAlarmTargetTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(alarmSetVO -> {
            log.info("알림발송내역 정보" + Arrays.toString(alarmSetVO));
        });

        //알림발송내역 리스트
        model.addAttribute("alarmSendHistoryVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //알림발송내역 메시지 가져오기
    @GetMapping("/selectAlarmSendHistoryMessage/{alarmSendHistoryNo}")
    public ResponseEntity<String> selectAlarmSendHistoryMessage(@PathVariable("alarmSendHistoryNo") Long alarmSendHistoryNo) {
        log.info("코드 : " + alarmSendHistoryNo);

        //알림메시지 가져오기
        String alarmMessage = customAlarmSendHistoryRepository.findById(alarmSendHistoryNo).get().getAshAlarmMessage();

        return new ResponseEntity<>(alarmMessage, HttpStatus.CREATED);
    }

    private String originalAlarmMessage = "";

    //알림메시지 원래 알림메시지 템플릿으로 되돌리기
    @GetMapping("/updateOriginalAlarmMessage/{alarmNo}")
    public ResponseEntity<String> updateOriginalAlarmMessage(@PathVariable("alarmNo") Long alarmNo) {
        log.info("코드 : " + alarmNo);

        customAlarmSetRepository.findById(alarmNo).ifPresent(alarm -> {
            originalAlarmMessage = customAlarmTemplateRepository.findByAlarmMessageByalarmTpNo(alarm.getAlarmTpNo());

            alarm.setAlarmMessage(originalAlarmMessage);
        });

        return new ResponseEntity<>(originalAlarmMessage, HttpStatus.CREATED);
    }


    /*메소드*/

    //알림 메시지 set
    private String setAlarmMessage(String alarmMessage, MembershipCustomer membershipCustomer, String storeId) {
        if (alarmMessage.contains("#{매장명}")) {
            System.out.println("1");
            alarmMessage = alarmMessage.replace("#{매장명}", customStoreRepository.findById(storeId).get().getStoreName());
        }
        if (alarmMessage.contains("#{수신자명}")) {
            System.out.println("2");
            if (membershipCustomer.getMembershipCustomerName() != null)
                alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getMembershipCustomerName());
            else
                alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getMembershipCustomerPhone());
        }
        if (alarmMessage.contains("#{수신자전화번호}")) {
            System.out.println("3");
            alarmMessage = alarmMessage.replace("#{수신자전화번호}", membershipCustomer.getMembershipCustomerPhone());
        }

        System.out.println(alarmMessage);

        return alarmMessage;
    }


    //알림목정명 set
    private String setAlarmPurposeName(String alarmTypeCode, String alarmPurposeCode) {
        String returnValue = "";

        if (alarmTypeCode.equals("G")) {
            returnValue += "선물 ";
        } else if (alarmTypeCode.equals("C")) {
            returnValue += "쿠폰 ";
        } else if (alarmTypeCode.equals("S")) {
            returnValue += "도장 ";
        } else if (alarmTypeCode.equals("P")) {
            returnValue += "포인트 ";
        }

        if (alarmPurposeCode.equals("0")) {
            returnValue += "도착";
        } else if (alarmPurposeCode.equals("1")) {
            returnValue += "사용";
        } else if (alarmPurposeCode.equals("2")) {
            returnValue += "구매";
        } else if (alarmPurposeCode.equals("3")) {
            returnValue += "유효기간 만료";
        } else if (alarmPurposeCode.equals("4")) {
            returnValue += "적립";
        } else if (alarmPurposeCode.equals("5")) {
            returnValue += "차감";
        }

        return returnValue;
    }
}
