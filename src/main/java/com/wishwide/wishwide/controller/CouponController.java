package com.wishwide.wishwide.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
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
                                        Model model) {
        log.info("등록 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());
    }

    @PostMapping("/postRegisterCoupon")
    public String postRegisterCoupon(@ModelAttribute(value = "couponVO") Coupon couponVO,
                                     @RequestParam(value = "finishDate", required = false) String finishDate,
                                     @RequestParam(value = "customerNo", required = false) List<String> customerList,
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

        //쿠폰 저장 `
        Coupon coupon = customCouponRepository.save(couponVO);

        //전체고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("WHOLE")){
            customCustomerRepository.findCustomerByStoreId(coupon.getStoreId()).forEach(customer -> {

                CouponBox couponBoxVO = setCouponBox(coupon, customer.getCustomerNo());

                //쿠폰타입이 도장이나 포인트면 바로 사용처리
                if(coupon.getCouponTypeNo() != 1){
                    couponBoxVO.setCouponUseCode(1);
                    couponBoxVO.setCouponUsedate(LocalDateTime.now());

                    //도장 or 포인트 적립

                    int benefitValue = customer.getCustomerBenefitValue() + coupon.getCouponBenefitValue();
                    Store store = customStoreRepository.findById(coupon.getStoreId()).get();

                    if(coupon.getCouponTypeNo() == 2){

                        //매장이 기존에 설정한 도장적립 기준을 넘을 경우 0개로 초기화 후 쿠폰 발행
                        if(benefitValue == store.getStoreStampGoal()) {
                            customCustomerRepository.changeCustomerBenefitValue(0, customer.getCustomerNo());

                            Coupon benefitCoupon = customCouponRepository.findByStoreBenefitCoupon(coupon.getStoreId());

                            if(benefitCoupon != null){
                                CouponBox couponBox = setCouponBox(benefitCoupon, customer.getCustomerNo());

                                customCouponBoxRepository.save(couponBox);
                            }
                        }
                        else {
                            customCustomerRepository.changeCustomerBenefitValue(benefitValue, customer.getCustomerNo());
                        }
                    }
                    else{
                        customCustomerRepository.changeCustomerBenefitValue(benefitValue, customer.getCustomerNo());
                    }
                }
                customCouponBoxRepository.save(couponBoxVO);

                log.info("쿠폰함 저장 완료");

                /**알림 전송**/
                sendAlarm(coupon, customer);
            });
        }
        //단골고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("VIP")){
            customCustomerRepository.findVIPCustomerByStoreId(coupon.getStoreId()).forEach(customer -> {
                CouponBox couponBoxVO = setCouponBox(coupon, customer.getCustomerNo());

                //쿠폰타입이 도장이나 포인트면 바로 사용처리
                if(coupon.getCouponTypeNo() != 1){
                    couponBoxVO.setCouponUseCode(1);
                    couponBoxVO.setCouponUsedate(LocalDateTime.now());

                    //도장 or 포인트 적립
                }
                customCouponBoxRepository.save(couponBoxVO);


                log.info("쿠폰함 저장 완료");

                /**알림 전송**/
                sendAlarm(coupon, customer);
            });
        }
        //특정고객 발송 시
        if(couponVO.getCouponTargetTypeCode().equals("SELECT")) {
            if (customerList != null) {
                customerList.forEach(customerNo -> {
                    log.info("고객번호 : "+customerNo);
                    Customer customer = customCustomerRepository.findById(Long.parseLong(customerNo)).get();
                    CouponBox couponBoxVO = setCouponBox(coupon, customer.getCustomerNo());

                    //쿠폰타입이 도장이나 포인트면 바로 사용처리
                    if(coupon.getCouponTypeNo() != 1){
                        couponBoxVO.setCouponUseCode(1);
                        couponBoxVO.setCouponUsedate(LocalDateTime.now());
                    }
                    customCouponBoxRepository.save(couponBoxVO);

                    log.info("쿠폰함 저장 완료");



                    /**알림 전송**/
                    sendAlarm(coupon, customer);
                });
            }
        }

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        return "redirect:/wishwide/coupon/listCoupon";
    }

    //상세
    @GetMapping("/detailCoupon/{productNo}")
    public String detailCoupon(@PathVariable("productNo") Long productNo,
                               @ModelAttribute("pageVO") PageVO pageVO,
                               Model model) {
        log.info("데이터 : "+productNo);

        //매장 정보
//        model.addAttribute("couponVO", customProductRepository.getCouponDetail(productNo));
//
//        //페이징 정보
//        model.addAttribute("pageVO", pageVO);
//
//        //서브상품 개수
//        model.addAttribute("subProductCnt", subProductRepository.findBySubProductCnt(productNo));

        return "wishwide/coupon/detailCoupon";
    }

    public CouponBox setCouponBox(Coupon coupon, Long customerNo){
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
        couponBox.setCustomerNo(customerNo);
        couponBox.setProductTitle(coupon.getProductTitle());
        couponBox.setStoreId(coupon.getStoreId());

        return couponBox;
    }

    public void sendAlarm(Coupon coupon, Customer customer){
        //할인쿠폰
        if(coupon.getCouponTypeNo() == 1) {
            alarmManager.sendCouponAlarmMessage(coupon, customer, "C0");
        }
        //도장적립쿠폰
        else if(coupon.getCouponTypeNo() == 2){
            alarmManager.sendCouponAlarmMessage(coupon, customer, "S4");
        }
        //포인트적립쿠폰
        else{
            alarmManager.sendCouponAlarmMessage(coupon, customer, "P4");
        }
    }
}
