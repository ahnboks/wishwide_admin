package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.domain.LoginInfo;
import com.wishwide.wishwide.domain.Store;
import com.wishwide.wishwide.domain.WwBrand;
import com.wishwide.wishwide.domain.WwRole;
import com.wishwide.wishwide.persistence.*;
import com.wishwide.wishwide.persistence.alarm.CustomAlarmSetRepository;
import com.wishwide.wishwide.persistence.ar.MarkerRepository;
import com.wishwide.wishwide.persistence.coupon.CustomCouponBoxRepository;
import com.wishwide.wishwide.persistence.coupon.CustomCouponRepository;
import com.wishwide.wishwide.persistence.customer.CustomCustomerRepository;
import com.wishwide.wishwide.persistence.device.CustomDeviceRepository;
import com.wishwide.wishwide.persistence.gift.CustomGiftPaymentRepository;
import com.wishwide.wishwide.persistence.product.CustomProductRepository;
import com.wishwide.wishwide.persistence.product.GiftProductRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import com.wishwide.wishwide.persistence.store.StoreFileRepository;
import com.wishwide.wishwide.persistence.store.StoreImageRepository;
import com.wishwide.wishwide.vo.PageMaker;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.util.List;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;
import static com.wishwide.wishwide.file.FileManager.saveCloudFile;
import static com.wishwide.wishwide.file.FileManager.saveDBStoreFile;
import static com.wishwide.wishwide.file.FileManager.saveDBStoreImage;

@Log
@Controller
@RequestMapping("/wishwide/store/")
public class StoreController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    LoginInfoRepository loginInfoRepository;

    @Autowired
    StoreImageRepository storeImageRepository;

    @Autowired
    StoreFileRepository storeFileRepository;

    @Autowired
    WwRoleRepository wwRoleRepository;

    @Autowired
    WwBrandRepository wwBrandRepository;

    @Autowired
    CustomCouponRepository customCouponRepository;

    @Autowired
    CustomProductRepository customProductRepository;

    @Autowired
    GiftProductRepository giftProductRepository;

    @Autowired
    CustomDeviceRepository customDeviceRepository;

    @Autowired
    MarkerRepository markerRepository;

    @Autowired
    CustomAlarmSetRepository customAlarmSetRepository;

    @Autowired
    CustomCustomerRepository customCustomerRepository;

    @Autowired
    CustomGiftPaymentRepository customGiftPaymentRepository;

    @Autowired
    CustomCouponBoxRepository customCouponBoxRepository;


    //리스트
    @GetMapping("/listStore")
    public void listStore(HttpServletRequest request,
                          @ModelAttribute("pageVO") PageVO pageVO,
                          Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "storeId");

        Page<Object[]> result = customStoreRepository.getStorePage(
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getServiceOperationCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(store -> {
            log.info("매장 정보"+ java.util.Arrays.toString(store));
        });

        //매장 리스트
        model.addAttribute("storeVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //등록
    @GetMapping("/registerStore")
    public void getRegisterStore(@ModelAttribute("pageVO") PageVO pageVO,
                                   Model model) {
        log.info("매장 등록 페이지");
    }

    @PostMapping("/postRegisterStore")
    public String postRegisterStore(@ModelAttribute("storeVO") Store storeVO,
                                    @RequestParam("userPw") String userPw,
                                    @RequestParam("storeBusinessRegistrationNumber") List<String> storeBusinessRegistrationNumberList,
                                    @RequestParam(value = "bziFile", required = false) MultipartFile bziFile,
                                    @RequestParam(value = "contractFile", required = false) MultipartFile contractFile,
                                    @RequestParam(value = "logoFile") MultipartFile logoFile,
                                    @ModelAttribute("pageVO") PageVO pageVO,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        log.info("등록 데이터 : " + storeVO + " , 비밀번호 :" + userPw);

//        //세션 값 get
        HttpSession session = request.getSession();

        //LoginInfo 테이블에 아이디, 비밀번호 저장
        LoginInfo loginInfo = new LoginInfo();

        loginInfo.setUserId(storeVO.getStoreId());
        loginInfo.setUserPw(passwordEncoder.encode(userPw));

        loginInfoRepository.save(loginInfo);

        //wwRole 테이블에 권한 등록
        WwRole wwRole = new WwRole();

        wwRole.setUserId(storeVO.getStoreId());
        wwRole.setRoleCode("ST");
        wwRole.setRoleName("매장");

        wwRoleRepository.save(wwRole);

        //사업자등록증 파일 등록
        if (bziFile != null && !(bziFile.isEmpty())) {
            storeFileRepository.save(saveDBStoreFile("BSN", storeVO.getStoreId(), saveCloudFile(bziFile)));
            log.info("사업자 등록증 등록 성공");
        }

        //계약서 파일 업로드
        if (contractFile != null && !(contractFile.isEmpty())) {
            storeFileRepository.save(saveDBStoreFile("CONT", storeVO.getStoreId(), saveCloudFile(contractFile)));
            log.info("계약서 등록 성공");
        }

        //로고 파일 업로드
        String logoFileUrl = "";
        if (logoFile != null && !(logoFile.isEmpty())) {
            logoFileUrl = storeImageRepository.save(saveDBStoreImage("LOGO", storeVO.getStoreId(), saveCloudFile(logoFile))).getStoreImageUrl();

            //VO에 URL값 세팅
            storeVO.setStoreLogoUrl(logoFileUrl);
            log.info("로고 등록 성공");
        }

        //wwBrand 테이블에 브랜드 정보 등록
        int checkBrandName = wwBrandRepository.checkBrandName(storeVO.getBrandName());

        //매장의 브랜드명이 기존에 등록되어있지 않을 경우
        if (checkBrandName == 0) {
            //새로운 브랜드 정보 등록
            WwBrand wwBrand = new WwBrand();
            wwBrand.setBrandLogoUrl(logoFileUrl);
            wwBrand.setBrandName(storeVO.getBrandName());

            wwBrandRepository.save(wwBrand);
        }

        //VO에 세션값 세팅
        storeVO.setStoreUpdateId(session.getAttribute("userId").toString());
        storeVO.setStoreRegId(session.getAttribute("userId").toString());

        //게임 타입에 따라 게임명 입력
        if (storeVO.getStoreArGameTypeCode() == 1)
            storeVO.setStoreArGameTypeName("캐릭터잡기");
        else
            storeVO.setStoreArGameTypeName("글자맞추기");

        //사업자 번호 set
        String storeBusinessRegistrationNumber = "";
        for(String brn : storeBusinessRegistrationNumberList){
            storeBusinessRegistrationNumber += brn;
        }
        log.info("사업자 번호 : "+storeBusinessRegistrationNumber);
        storeVO.setStoreBusinessRegistrationNumber(Integer.parseInt(storeBusinessRegistrationNumber));

        //매장 정보 저장
        customStoreRepository.save(storeVO);

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("매장 등록 성공");

        return "redirect:/wishwide/store/listStore";
    }

    //상세
    @GetMapping("/detailStore/{storeId}")
    public String detailStore(@PathVariable("storeId") String storeId,
                                   @ModelAttribute("pageVO") PageVO pageVO,
                                   Model model){
        //매장 정보
        model.addAttribute("storeVO", customStoreRepository.getStoreDetail(storeId));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        return "wishwide/store/detailStore";
    }

    //수정
    @Transactional
    @PostMapping("/update")
    public String updateStore(@ModelAttribute("storeVO") Store storeVO,
                              @RequestParam(value = "bziFile", required = false) MultipartFile bziFile,
                              @RequestParam(value = "contractFile", required = false) MultipartFile contractFile,
                              @RequestParam(value = "logoFile") MultipartFile logoFile,
                              @RequestParam("storeBusinessRegistrationNumber") List<String> storeBusinessRegistrationNumberList,
                              @ModelAttribute("pageVO") PageVO pageVO,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes){
        log.info("수정 데이터 : "+storeVO);

        //세션 값 get
        HttpSession session = request.getSession();

        //매장 정보 조회
        customStoreRepository.findById(storeVO.getStoreId()).ifPresent(store -> {
            //사업자등록증 파일 등록
            if (bziFile != null && !(bziFile.isEmpty())) {
                storeFileRepository.save(saveDBStoreFile("BSN",storeVO.getStoreId(), saveCloudFile(bziFile)));
                log.info("사업자 등록증 등록 성공");
            }

            //계약서 파일 업로드
            if (contractFile != null && !(contractFile.isEmpty())) {
                storeFileRepository.save(saveDBStoreFile("CONT", storeVO.getStoreId(), saveCloudFile(contractFile)));
                log.info("계약서 등록 성공");
            }

            //로고 파일 업로드
            String logoFileUrl = "";
            if (logoFile != null && !(logoFile.isEmpty())) {
                logoFileUrl = storeImageRepository.save(saveDBStoreImage("LOGO", storeVO.getStoreId(), saveCloudFile(logoFile))).getStoreImageUrl();

                //VO에 URL값 세팅
                storeVO.setStoreLogoUrl(logoFileUrl);
                log.info("로고 등록 성공");
            }

            //수정 값 세팅
                store.setStorePhone(storeVO.getStorePhone());
                store.setStoreEmail(storeVO.getStoreEmail());
                store.setStoreZipCode(storeVO.getStoreZipCode());
                store.setStoreAddress(storeVO.getStoreAddress());
                store.setStoreAddressDetail(storeVO.getStoreAddressDetail());
                store.setStoreIntroduction(storeVO.getStoreIntroduction());
                store.setStorePresidentName(storeVO.getStorePresidentName());
                store.setStoreOpeningHour(storeVO.getStoreOpeningHour());
                store.setStoreClosingHour(storeVO.getStoreClosingHour());
                store.setStoreKakaoYellowId(storeVO.getStoreKakaoYellowId());
                store.setStoreBenefitTypeCode(storeVO.getStoreBenefitTypeCode());
                store.setStoreStampGoal(storeVO.getStoreStampGoal());
                store.setStoreStampVipGoal(storeVO.getStoreStampVipGoal());
                store.setStorePointUseGoal(storeVO.getStorePointUseGoal());
                store.setStorePointVipGoal(storeVO.getStorePointVipGoal());
                store.setStoreArGameUseCode(storeVO.getStoreArGameUseCode());
                store.setStoreArGameTypeCode(storeVO.getStoreArGameTypeCode());
                store.setStoreArGameWord(storeVO.getStoreArGameWord());
                store.setStoreMemo(storeVO.getStoreMemo());
                store.setStoreBusinessRegistrationNumber(storeVO.getStoreBusinessRegistrationNumber());
                store.setStoreContractBegindate(storeVO.getStoreContractBegindate());
                store.setStoreContractFinishdate(storeVO.getStoreContractFinishdate());
                store.setStoreOperatorEmail(storeVO.getStoreOperatorEmail());
                store.setStoreOperatorName(storeVO.getStoreOperatorName());
                store.setStoreOperatorPhone(storeVO.getStoreOperatorPhone());
                store.setStorePresidentName(storeVO.getStorePresidentName());
                store.setStoreUpdateId(session.getAttribute("userId").toString());

                //게임 타입에 따라 게임명 입력
                if(storeVO.getStoreArGameTypeCode() == 1)
                    store.setStoreArGameTypeName("캐릭터잡기");
                else
                    store.setStoreArGameTypeName("글자맞추기");

                //사업자 번호 set
                String storeBusinessRegistrationNumber = "";
                for (String brn : storeBusinessRegistrationNumberList) {
                    storeBusinessRegistrationNumber += brn;
                }
                store.setStoreBusinessRegistrationNumber(Integer.parseInt(storeBusinessRegistrationNumber));

                //계약상태 코드가 계약종료 & 계약취소 & 계약대기일 시
                if(storeVO.getStoreContractStatusCode().equals("CC") ||
                        storeVO.getStoreContractStatusCode().equals("CE") ||
                            storeVO.getStoreContractStatusCode().equals("CW")){
                    // 서비스 운영코드 값 변경
                    store.setStoreServiceOperationCode("TERMINATE");

                    //상품 비활성화 처리
                    customProductRepository.changeProductVisibleCode(0, storeVO.getStoreId());
                    //선물상품 비활성화 처리
                    giftProductRepository.changeAllGiftProductVisibleCode(0, storeVO.getStoreId());
                    //마커 비활성화 처리
                    markerRepository.changeMarkerVisibleCode(0, storeVO.getStoreId());
                    //알림 비활성화 처리
                    customAlarmSetRepository.changeAlarmVisibleCode(0, storeVO.getStoreId());
                    //쿠폰 비활성화 처리
                    customCouponRepository.changeCouponVisibleCode(0, storeVO.getStoreId());
                    //디바이스 비활성화 처리
                    customDeviceRepository.changeDeviceVisibleCode(0, storeVO.getStoreId());

                    log.info("비활성화 처리");
                }
                //계약취소, 계약종료, 계약대기 상태에서 계약중으로 코드가 바뀔 시
                if(storeVO.getStoreContractStatusCode().equals("CY")){
                    if(store.getStoreContractStatusCode().equals("CC") ||
                            store.getStoreContractStatusCode().equals("CE") ||
                                store.getStoreContractStatusCode().equals("CW")){
                        // 서비스 운영코드 값 변경
                        store.setStoreServiceOperationCode("PREACTIVE");

                        //상품 활성화 처리
                        customProductRepository.changeProductVisibleCode(1, storeVO.getStoreId());
                        //선물상품 활성화 처리
                        giftProductRepository.changeAllGiftProductVisibleCode(1, storeVO.getStoreId());
                        //마커 활성화 처리
                        markerRepository.changeMarkerVisibleCode(1, storeVO.getStoreId());
                        //알림 활성화 처리
                        customAlarmSetRepository.changeAlarmVisibleCode(1, storeVO.getStoreId());
                        //쿠폰 활성화 처리
                        customCouponRepository.changeCouponVisibleCode(1, storeVO.getStoreId());
                        //디바이스 활성화 처리
                        customDeviceRepository.changeDeviceVisibleCode(1, storeVO.getStoreId());

                        log.info("활성화 처리");
                    }
                }

                //VO 셋팅
                store.setStoreContractStatusCode(storeVO.getStoreContractStatusCode());

                customStoreRepository.save(store);

                redirectAttributes.addFlashAttribute("message", "successUpdate");

                pageRedirectProperty(redirectAttributes, pageVO);
        });


        return "redirect:/wishwide/store/listStore";

    }

    private String resultCode = "";
    //서비스운영타입코드 변경
    @GetMapping("/updateServiceOperationCode/{storeId}/{storeServiceOperationCode}")
    public ResponseEntity<String> updateServiceOperationCode(@PathVariable("storeId") String storeId,
                                 @PathVariable("storeServiceOperationCode") String storeServiceOperationCode) {
        log.info("서비스 타입 코드 : "+storeServiceOperationCode);



        //서비스 타입 코드 변경
        customStoreRepository.findById(storeId).ifPresent(store -> {
            if(store.getStoreIntroduction().equals("") || store.getStoreIntroduction() == null){
                resultCode = "not-introduction";
            }
            else if(store.getStoreOpeningHour() == null || store.getStoreClosingHour() == null){
                resultCode = "not-storeHour";
            }
            else if(store.getStoreAddress().equals("") || store.getStoreAddress() == null){
                resultCode = "not-storeAddress";
            }
            else if(store.getStoreBenefitTypeCode().equals("N") || store.getStoreBenefitTypeCode() == null){
                resultCode = "not-benefitCode";
            }
            else{
                if(storeServiceOperationCode.equals("ACTIVE")) {
                    store.setStoreServiceOperationCode("PREACTIVE");
                    resultCode = "PREACTIVE";
                }
                else {
                    store.setStoreServiceOperationCode("ACTIVE");
                    resultCode = "ACTIVE";
                }
            }

            customStoreRepository.save(store);
        });

        return new ResponseEntity<>(resultCode,HttpStatus.CREATED);
    }

    //비밀번호 변경
    @PostMapping("/detailStore/updatePassword/{storeId}")
    public ResponseEntity<String> updatePassword(@PathVariable("storeId") String storeId,
                                                 @RequestParam("userPw") String userPw,
                                                 RedirectAttributes redirectAttributes,
                                                 PageVO pageVO) {
        log.info("비밀번호 변경"+userPw);
        loginInfoRepository.findById(storeId).ifPresent(loginInfo -> {
            loginInfo.setUserPw(passwordEncoder.encode(userPw));
            loginInfoRepository.save(loginInfo);
        });

        pageRedirectProperty(redirectAttributes, pageVO);

        return new ResponseEntity<>("1",HttpStatus.CREATED);
    }

    //가맹점 리스트 가져오기
    @GetMapping("/selectStore")
    public ResponseEntity<List<Object[]>> selectStore() {
        return new ResponseEntity<>(customStoreRepository.getStoreList(), HttpStatus.CREATED);
    }

    //고객 리스트 가져오기
    @GetMapping("/selectStoreCustomer/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreCustomer(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customCustomerRepository.getStoreCustomerList(storeId), HttpStatus.CREATED);
    }

    //디바이스 리스트 가져오기
    @GetMapping("/selectStoreDevice/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreDevice(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customDeviceRepository.getStoreDeviceList(storeId), HttpStatus.CREATED);
    }

    //상품 리스트 가져오기
    @GetMapping("/selectStoreProduct/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreProduct(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customProductRepository.getStoreProductList(storeId), HttpStatus.CREATED);
    }

    //선물거래내역 리스트 가져오기
    @GetMapping("/selectStoreGiftPayment/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreGiftPayment(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customGiftPaymentRepository.getGiftPaymentList(storeId), HttpStatus.CREATED);
    }

    //쿠폰사용내역 리스트 가져오기
    @GetMapping("/selectStoreCouponBox/{storeId}")
    public ResponseEntity<List<Object[]>> selectStoreCouponBox(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customCouponBoxRepository.getCouponBoxList(storeId), HttpStatus.CREATED);
    }

    //하나의 매장 정보 가져오기
    @GetMapping("/selectOneStore/{storeId}")
    public ResponseEntity<Object[]> selectOneStore(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customStoreRepository.getStoreDetail(storeId), HttpStatus.CREATED);
    }

    /*계약조회*/
    //리스트
    @GetMapping("/listStoreContract")
    public void listStoreContract(HttpServletRequest request,
                          @ModelAttribute("pageVO") PageVO pageVO,
                          Model model) {
        Pageable pageable = pageVO.makePageable(0, "storeId");

        Page<Object[]> result = customStoreRepository.getStoreContractPage(
                pageVO.getType(),
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                pageVO.getServiceOperationCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(store -> {
            log.info("계약 정보"+ java.util.Arrays.toString(store));
        });

        //매장 리스트
        model.addAttribute("storeContractVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }
}
