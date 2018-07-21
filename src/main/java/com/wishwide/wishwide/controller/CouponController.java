package com.wishwide.wishwide.controller;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.wishwide.wishwide.alarm.AlarmManager;
import com.wishwide.wishwide.domain.*;
import com.wishwide.wishwide.persistence.benefit.*;
import com.wishwide.wishwide.persistence.coupon.*;
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
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;

@Log
@Controller
@RequestMapping("/wishwide/coupon/")
public class CouponController {
    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomCouponBoxRepository customCouponBoxRepository;

    @Autowired
    CustomCouponRepository customCouponRepository;

    @Autowired
    CouponBoxHistoryRepository couponBoxHistoryRepository;

    @Autowired
    CouponBoxLogRepository couponBoxLogRepository;

    @Autowired
    CouponPublishHistoryRepository couponPublishHistoryRepository;

    @Autowired
    CouponPublishLogRepository couponPublishLogRepository;

    @Autowired
    CustomCustomerRepository customCustomerRepository;

    @Autowired
    StampRepository stampRepository;

    @Autowired
    StampHistoryRepository stampHistoryRepository;

    @Autowired
    StampLogRepository stampLogRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    PointLogRepository pointLogRepository;

    @Autowired
    private AlarmManager alarmManager;

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
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //리스트
    @GetMapping("/listCoupon")
    public void listCoupon(HttpServletRequest request,
                           @ModelAttribute("pageVO") PageVO pageVO,
                           Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "couponNo");

        Page<Object[]> result = customCouponRepository.getCouponPage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                userId,
                pageVO.getCouponTypeCode(),
                pageVO.getCouponTargetTypeCode(),
                pageVO.getCouponPublishTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(device -> {
            log.info("쿠폰  내역 정보"+ Arrays.toString(device));
        });

        //쿠폰 리스트
        model.addAttribute("couponVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //등록
    @GetMapping("/registerCoupon")
    public void getRegisterCoupon(@ModelAttribute("pageVO") PageVO pageVO,
                                  HttpServletRequest request,
                                  Model model) {
        log.info("등록 페이지");

        //세션
        HttpSession session = request.getSession();

        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();
        String benefitTypeCode = session.getAttribute("benefitTypeCode").toString();

        log.info("세션 : "+sessionId+roleCode);

        if(roleCode.equals("ST")){
            //가맹점아이디
            model.addAttribute("storeId", sessionId);
        }

        //혜택타입
        model.addAttribute("benefitTypeCode", benefitTypeCode);

        //세션 권한
        model.addAttribute("roleCode", roleCode);

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());
    }

    @Transactional
    @PostMapping("/postRegisterCoupon")
    public String postRegisterCoupon(@ModelAttribute(value = "couponVO") Coupon couponVO,
                                     @RequestParam(value = "finishDate", required = false) String finishDate,
                                     @RequestParam(value = "membershipCustomerNo", required = false) List<String> customerList,
                                     @ModelAttribute("pageVO") PageVO pageVO,
                                     RedirectAttributes redirectAttributes) {
        log.info("데이터 : " + couponVO);
        log.info("데이터 : " + customerList);

        LocalDate couponFinishTime = LocalDate.parse(finishDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        //VO 세팅
        if(couponVO.getCouponTargetTypeCode().equals("BIRTH")||
                couponVO.getCouponTargetTypeCode().equals("STAMP")||
                    couponVO.getCouponTargetTypeCode().equals("NEW")||
                        couponVO.getCouponTargetTypeCode().equals("UPGRADE")){
            couponVO.setCouponPublishCode("AUTO");
        }
        else{
            couponVO.setCouponPublishCode("IMME");
        }

        couponVO.setCouponFinishdate(couponFinishTime);

        //쿠폰 저장
        Coupon coupon = customCouponRepository.save(couponVO);

        //쿠폰발행내역 & 로그 저장
        saveCouponPublishHistoryLog(coupon);

        //전체고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("WHOLE")){
            customCustomerRepository.findCustomerByStoreId(coupon.getStoreId()).forEach(customer -> {

                CouponBox couponBoxVO = setCouponBox(coupon, customer.getMembershipCustomerNo());

                if(coupon.getCouponTypeNo() != 1){
                    //도장 & 포인트 적립
                    saveBenefit(couponBoxVO, coupon, customer);
                }

                CouponBox couponBox = customCouponBoxRepository.save(couponBoxVO);

                //쿠폰함 내역 & 로그 저장
                saveCouponBoxHistoryLog(couponBox, customer);

                log.info("쿠폰함 저장 완료");

                /**알림 전송**/
                sendAlarm(coupon, customer);
            });
        }
        //단골고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("VIP")){
            customCustomerRepository.findVIPCustomerByStoreId(coupon.getStoreId()).forEach(customer -> {
                CouponBox couponBoxVO = setCouponBox(coupon, customer.getMembershipCustomerNo());

                if(coupon.getCouponTypeNo() != 1){
                    //도장 & 포인트 적립
                    saveBenefit(couponBoxVO, coupon, customer);
                }

                CouponBox couponBox = customCouponBoxRepository.save(couponBoxVO);

                //쿠폰함 내역 & 로그 저장
                saveCouponBoxHistoryLog(couponBox, customer);

                log.info("쿠폰함 저장 완료");

                /**알림 전송**/
                sendAlarm(coupon, customer);
            });
        }
        //특정고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("SELECT")) {
            if (customerList != null) {
                customerList.forEach(membershipCustomerNo -> {
                    log.info("멤버쉽고객번호 : "+membershipCustomerNo);
                    MembershipCustomer membershipCustomer = customCustomerRepository.findById(Long.parseLong(membershipCustomerNo)).get();
                    CouponBox couponBoxVO = setCouponBox(coupon, membershipCustomer.getMembershipCustomerNo());

                    if(coupon.getCouponTypeNo() != 1){
                        //도장 & 포인트 적립
                        saveBenefit(couponBoxVO, coupon, membershipCustomer);
                    }

                    CouponBox couponBox = customCouponBoxRepository.save(couponBoxVO);

                    //쿠폰함 내역 & 로그 저장
                    saveCouponBoxHistoryLog(couponBox, membershipCustomer);

                    log.info("쿠폰함 저장 완료");

                    /**알림 전송**/
                    sendAlarm(coupon, membershipCustomer);
                });
            }
        }

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        return "redirect:/wishwide/coupon/listCoupon";
    }



    //상세
    @GetMapping("/detailCoupon/{couponNo}")
    public String detailCoupon(@PathVariable("couponNo") Long couponNo,
                               @ModelAttribute("pageVO") PageVO pageVO,
                               Model model) {
        log.info("데이터 : "+couponNo);

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        //쿠폰 정보
        model.addAttribute("couponVO", customCouponRepository.getCouponDetail(couponNo));

        return "wishwide/coupon/detailCoupon";
    }

    //자동발송쿠폰 수정
    @PostMapping("/update")
    public String updateCoupon(@ModelAttribute(value = "couponVO") Coupon couponVO,
                                     @ModelAttribute("pageVO") PageVO pageVO,
                                     RedirectAttributes redirectAttributes) {
        log.info("데이터 : " + couponVO);

        //VO 세팅
        customCouponRepository.findById(couponVO.getCouponNo()).ifPresent(coupon ->{
            coupon.setCouponBenefitValue(couponVO.getCouponBenefitValue());
            coupon.setCouponDiscountTypeCode(couponVO.getCouponDiscountTypeCode());
            coupon.setCouponDiscountValue(couponVO.getCouponDiscountValue());
            coupon.setCouponTitle(couponVO.getCouponTitle());
            coupon.setProductTitle(couponVO.getProductTitle());
            coupon.setCouponTypeNo(couponVO.getCouponTypeNo());
            coupon.setCouponFinishdate(couponVO.getCouponFinishdate());

            customCouponRepository.save(coupon);;
        });

        redirectAttributes.addFlashAttribute("message", "successUpdate");
        pageRedirectProperty(redirectAttributes, pageVO);

        return "redirect:/wishwide/coupon/listCoupon";
    }

    //쿠폰발행내역로그 저장
    private void saveCouponPublishHistoryLog(Coupon coupon) {
        CouponPublishHistory couponPublishHistory = new CouponPublishHistory();
        couponPublishHistory.setCouponBenefitValue(coupon.getCouponBenefitValue());
        couponPublishHistory.setCouponDiscountTypeCode(coupon.getCouponDiscountTypeCode());
        couponPublishHistory.setCouponDiscountValue(coupon.getCouponDiscountValue());
        couponPublishHistory.setCouponNo(coupon.getCouponNo());
        couponPublishHistory.setCouponPublishTypeCode(coupon.getCouponPublishTypeCode());
        couponPublishHistory.setCouponReservationTime(coupon.getCouponReservationTime());
        couponPublishHistory.setCouponTargetTypeCode(coupon.getCouponTargetTypeCode());
        couponPublishHistory.setCouponTitle(coupon.getCouponTitle());
        couponPublishHistory.setCouponTypeNo(coupon.getCouponTypeNo());
        couponPublishHistory.setProductTitle(coupon.getProductTitle());
        couponPublishHistory.setStoreId(coupon.getStoreId());

        couponPublishHistoryRepository.save(couponPublishHistory);

        CouponPublishLog couponPublishLog = new CouponPublishLog();
        couponPublishLog.setCouponBenefitValue(coupon.getCouponBenefitValue());
        couponPublishLog.setCouponDiscountTypeCode(coupon.getCouponDiscountTypeCode());
        couponPublishLog.setCouponDiscountValue(coupon.getCouponDiscountValue());
        couponPublishLog.setCouponNo(coupon.getCouponNo());
        couponPublishLog.setCouponPublishTypeCode(coupon.getCouponPublishTypeCode());
        couponPublishLog.setCouponReservationTime(coupon.getCouponReservationTime());
        couponPublishLog.setCouponTargetTypeCode(coupon.getCouponTargetTypeCode());
        couponPublishLog.setCouponTitle(coupon.getCouponTitle());
        couponPublishLog.setCouponTypeNo(coupon.getCouponTypeNo());
        couponPublishLog.setProductTitle(coupon.getProductTitle());
        couponPublishLog.setStoreId(coupon.getStoreId());

        couponPublishLogRepository.save(couponPublishLog);
    }

    //VO 셋팅
    public CouponBox setCouponBox(Coupon coupon, Long membershipCustomerNo){
        CouponBox couponBox = new CouponBox();
        couponBox.setCouponNo(coupon.getCouponNo());
        couponBox.setCouponBegindate(LocalDate.now());
        couponBox.setCouponFinishdate(coupon.getCouponFinishdate());
        couponBox.setCouponDiscountTypeCode(coupon.getCouponDiscountTypeCode());
        couponBox.setCouponDiscountValue(coupon.getCouponDiscountValue());
        couponBox.setCouponImageUrl("http://restapi.fs.ncloud.com/elin-cloud/contents/%EC%BF%A0%ED%8F%B0%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg");
        couponBox.setCouponPublishTypeCode(coupon.getCouponPublishTypeCode());
        couponBox.setCouponPublishCode(coupon.getCouponPublishCode());
        couponBox.setCouponReservationTime(coupon.getCouponReservationTime());
        couponBox.setCouponTargetTypeCode(coupon.getCouponTargetTypeCode());
        couponBox.setCouponTitle(coupon.getCouponTitle());
        couponBox.setCouponTypeNo(coupon.getCouponTypeNo());
        couponBox.setMembershipCustomerNo(membershipCustomerNo);
        couponBox.setProductTitle(coupon.getProductTitle());
        couponBox.setStoreId(coupon.getStoreId());

        return couponBox;
    }

    //쿠폰함내역로그 저장
    private void saveCouponBoxHistoryLog(CouponBox coupon, MembershipCustomer customer) {
        CouponBoxHistory couponBoxHistory = new CouponBoxHistory();
        couponBoxHistory.setCouponNo(coupon.getCouponNo());
        couponBoxHistory.setCouponBegindate(LocalDate.now());
        couponBoxHistory.setCouponFinishdate(coupon.getCouponFinishdate());
        couponBoxHistory.setCouponDiscountTypeCode(coupon.getCouponDiscountTypeCode());
        couponBoxHistory.setCouponDiscountValue(coupon.getCouponDiscountValue());
        couponBoxHistory.setCouponImageUrl("http://restapi.fs.ncloud.com/elin-cloud/contents/%EC%BF%A0%ED%8F%B0%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg");
        couponBoxHistory.setCouponPublishTypeCode(coupon.getCouponPublishTypeCode());
        couponBoxHistory.setCouponReservationTime(coupon.getCouponReservationTime());
        couponBoxHistory.setCouponTargetTypeCode(coupon.getCouponTargetTypeCode());
        couponBoxHistory.setCouponTitle(coupon.getCouponTitle());
        couponBoxHistory.setCouponTypeNo(coupon.getCouponTypeNo());
        couponBoxHistory.setMembershipCustomerNo(customer.getMembershipCustomerNo());
        couponBoxHistory.setProductTitle(coupon.getProductTitle());
        couponBoxHistory.setStoreId(coupon.getStoreId());

        couponBoxHistoryRepository.save(couponBoxHistory);

        CouponBoxLog couponBoxLog = new CouponBoxLog();
        couponBoxLog.setCouponNo(coupon.getCouponNo());
        couponBoxLog.setCouponBegindate(LocalDate.now());
        couponBoxLog.setCouponFinishdate(coupon.getCouponFinishdate());
        couponBoxLog.setCouponDiscountTypeCode(coupon.getCouponDiscountTypeCode());
        couponBoxLog.setCouponDiscountValue(coupon.getCouponDiscountValue());
        couponBoxLog.setCouponImageUrl("http://restapi.fs.ncloud.com/elin-cloud/contents/%EC%BF%A0%ED%8F%B0%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg");
        couponBoxLog.setCouponPublishTypeCode(coupon.getCouponPublishTypeCode());
        couponBoxLog.setCouponReservationTime(coupon.getCouponReservationTime());
        couponBoxLog.setCouponTargetTypeCode(coupon.getCouponTargetTypeCode());
        couponBoxLog.setCouponTitle(coupon.getCouponTitle());
        couponBoxLog.setCouponTypeNo(coupon.getCouponTypeNo());
        couponBoxLog.setMembershipCustomerNo(customer.getMembershipCustomerNo());
        couponBoxLog.setProductTitle(coupon.getProductTitle());
        couponBoxLog.setStoreId(coupon.getStoreId());

        couponBoxLogRepository.save(couponBoxLog);
    }

    //알림 발송 저장
    public void sendAlarm(Coupon coupon, MembershipCustomer membershipCustomer){
        //할인쿠폰
        if(coupon.getCouponTypeNo() == 1) {
            alarmManager.sendCouponAlarmMessage(coupon, membershipCustomer, "C0");
        }
        //도장적립쿠폰
        else if(coupon.getCouponTypeNo() == 2){
            alarmManager.sendBenefitAlarmMessage(coupon, membershipCustomer, "S4");
        }
        //포인트적립쿠폰
        else{
            alarmManager.sendBenefitAlarmMessage(coupon, membershipCustomer, "P4");
        }
    }

    //혜택 저장
    private void saveBenefit(CouponBox couponBoxVO, Coupon coupon, MembershipCustomer customer) {
        //쿠폰타입이 도장이나 포인트면 바로 사용처리
        if(coupon.getCouponTypeNo() != 1){
            couponBoxVO.setCouponUseCode(1);
            couponBoxVO.setCouponUsedate(LocalDateTime.now());

            //도장 or 포인트 적립

            int benefitValue = customer.getCustomerBenefitValue() + coupon.getCouponBenefitValue();
            Store store = customStoreRepository.findById(coupon.getStoreId()).get();

            if(coupon.getCouponTypeNo() == 2){
                Stamp stamp = stampRepository.findByCustomerStamp(coupon.getStoreId(), customer.getMembershipCustomerNo());

                if(stamp != null){
                    stampRepository.findById(stamp.getStampNo()).ifPresent(stamp1 -> {
                        //매장이 기존에 설정한 도장적립 기준을 넘을 경우 0개로 초기화 후 쿠폰 발행
                        if(benefitValue == store.getStoreStampGoal()) {
                            customCustomerRepository.changeCustomerBenefitValue(0, customer.getMembershipCustomerNo());

                            Coupon benefitCoupon = customCouponRepository.findByStoreBenefitCoupon(coupon.getStoreId());

                            if(benefitCoupon != null){
                                CouponBox couponBox = setCouponBox(benefitCoupon, customer.getMembershipCustomerNo());

                                customCouponBoxRepository.save(couponBox);
                            }

                            stamp1.setStampNowCnt(0);
                            stamp1.setStampCouponPublishedCnt(stamp.getStampCouponPublishedCnt()+1);
                        }
                        else {
                            customCustomerRepository.changeCustomerBenefitValue(benefitValue, customer.getMembershipCustomerNo());
                            stamp1.setStampNowCnt(coupon.getCouponBenefitValue());
                        }

                        //도장 업데이트
                        stamp1.setStampCnt(stamp.getStampCnt()+coupon.getCouponBenefitValue());
                        stamp1.setStampSavingCnt(coupon.getCouponBenefitValue());

                        stampRepository.save(stamp1);
                    });

                    //도장 내역 & 로그 쌓기
                    saveStampHistoryLog(stamp);
                }
            }
            else{
                customCustomerRepository.changeCustomerBenefitValue(benefitValue, customer.getMembershipCustomerNo());

                Point point = pointRepository.findByCustomerPoint(coupon.getStoreId(), customer.getMembershipCustomerNo());

                if(point != null){
                    pointRepository.findById(point.getPointNo()).ifPresent(point1 -> {
                        point1.setPointCnt(point1.getPointCnt()+benefitValue);
                        point1.setPointNowCnt(point1.getPointNowCnt()+benefitValue);
                        point1.setPointSavingCnt(benefitValue);

                        pointRepository.save(point1);
                    });

                    //포인트 내역 & 로그 쌓기
                    savePointHistoryLog(point);
                }
            }
        }
    }

    //도장내역로그 저장
    private void saveStampHistoryLog(Stamp stamp){
        StampHistory stampHistory = new StampHistory();
        stampHistory.setStampNo(stamp.getStampNo());
        stampHistory.setMembershipCustomerNo(stamp.getMembershipCustomerNo());
        stampHistory.setStampCnt(stamp.getStampCnt());
        stampHistory.setStampCouponPublishedCnt(stamp.getStampCouponPublishedCnt());
        stampHistory.setStampDeductCnt(stamp.getStampDeductCnt());
        stampHistory.setStampNowCnt(stamp.getStampNowCnt());
        stampHistory.setStoreId(stamp.getStoreId());
        stampHistory.setStampEarningWay("C");

        stampHistoryRepository.save(stampHistory);

        StampLog stampLog = new StampLog();
        stampLog.setStampNo(stamp.getStampNo());
        stampLog.setMembershipCustomerNo(stamp.getMembershipCustomerNo());
        stampLog.setStampCnt(stamp.getStampCnt());
        stampLog.setStampCouponPublishedCnt(stamp.getStampCouponPublishedCnt());
        stampLog.setStampDeductCnt(stamp.getStampDeductCnt());
        stampLog.setStampNowCnt(stamp.getStampNowCnt());
        stampLog.setStoreId(stamp.getStoreId());
        stampLog.setStampEarningWay("C");

        stampLogRepository.save(stampLog);
    }

    //포인트내역로그 저장
    private void savePointHistoryLog(Point point) {
        PointHistory pointHistory = new PointHistory();

        pointHistory.setMembershipCustomerNo(point.getMembershipCustomerNo());
        pointHistory.setPointBegindate(LocalDate.now());
        pointHistory.setPointCnt(point.getPointCnt());
        pointHistory.setPointDeductCnt(point.getPointDeductCnt());
        pointHistory.setPointEarningWay("C");
        pointHistory.setPointExtinctionCnt(point.getPointExtinctionCnt());
        LocalDate date = LocalDate.now();
        pointHistory.setPointFinishdate(date.plusYears(2));
        pointHistory.setPointNo(point.getPointNo());
        pointHistory.setPointNowCnt(point.getPointNowCnt());
        pointHistory.setPointSavingCnt(point.getPointSavingCnt());
        pointHistory.setStoreId(point.getStoreId());

        pointHistoryRepository.save(pointHistory);

        PointLog pointLog = new PointLog();

        pointLog.setMembershipCustomerNo(point.getMembershipCustomerNo());
        pointLog.setPointBegindate(LocalDate.now());
        pointLog.setPointCnt(point.getPointCnt());
        pointLog.setPointDeductCnt(point.getPointDeductCnt());
        pointLog.setPointEarningWay("C");
        pointLog.setPointExtinctionCnt(point.getPointExtinctionCnt());
        pointLog.setPointFinishdate(date.plusYears(2));
        pointLog.setPointNo(point.getPointNo());
        pointLog.setPointNowCnt(point.getPointNowCnt());
        pointLog.setPointSavingCnt(point.getPointSavingCnt());
        pointLog.setStoreId(point.getStoreId());

        pointLogRepository.save(pointLog);
    }

    //쿠폰 등록여부 가져오기
    @GetMapping("/selectCouponRegisterCode/{storeId}/{couponTargetTypeCode}")
    public ResponseEntity<Integer> selectStoreCouponBox(@PathVariable("storeId") String storeId,
                                                               @PathVariable("couponTargetTypeCode") String couponTargetTypeCode) {
        log.info("코드 : " + storeId+couponTargetTypeCode);
        return new ResponseEntity<>(customCouponRepository.findByStoreRegisterCoupon(storeId, couponTargetTypeCode), HttpStatus.CREATED);
    }

    //쿠폰 발송내역 가져오기
    @GetMapping("/selectCouponHistory/{couponNo}")
    public ResponseEntity<List<Object[]>> selectStoreCouponBox(@PathVariable("couponNo") Long couponNo) {
        log.info("코드 : " +couponNo);
        return new ResponseEntity<>(customCouponBoxRepository.getCouponHistoryList(couponNo), HttpStatus.CREATED);
    }

}
