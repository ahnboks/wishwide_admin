package com.wishwide.wishwide.controller;


import com.wishwide.wishwide.domain.LoginInfo;
import com.wishwide.wishwide.domain.Partner;
import com.wishwide.wishwide.domain.PartnerStore;
import com.wishwide.wishwide.domain.WwRole;
import com.wishwide.wishwide.persistence.LoginInfoRepository;
import com.wishwide.wishwide.persistence.PartnerStoreRepository;
import com.wishwide.wishwide.persistence.WwRoleRepository;
import com.wishwide.wishwide.persistence.partner.CustomPartnerRepository;
import com.wishwide.wishwide.persistence.product.CustomProductRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;

@Log
@Controller
@RequestMapping("/wishwide/partner/")
public class PartnerController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomPartnerRepository customPartnerRepository;

    @Autowired
    CustomProductRepository customProductRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    LoginInfoRepository loginInfoRepository;

    @Autowired
    WwRoleRepository wwRoleRepository;

    @Autowired
    PartnerStoreRepository partnerStoreRepository;

    //리스트
    @GetMapping("/listPartner")
    public String listPartner(@ModelAttribute("pageVO") PageVO pageVO,
                             HttpServletRequest request,
                             Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        String redirectUrl = "";
        if(roleCode.equals("MA")){
            Pageable pageable = pageVO.makePageable(0, "partnerId");

            Page<Object[]> result = customPartnerRepository.getPartnerPage(
                    pageVO.getKeyword(),    //키워드
                    pageVO.getUserId(),
                    roleCode,
                    sessionId,
                    pageable);

            log.info("결과 값 : " + result);

            log.info("총 페이지 수 : " + result.getTotalPages());

            log.info("총 행 수" + result.getTotalElements());

            result.forEach(alarmSetVO -> {
                log.info("파트너 정보"+ Arrays.toString(alarmSetVO));
            });

            //파트너 리스트
            model.addAttribute("partnerVO", new PageMaker<>(result));

            //파트너명 셀렉트 박스
            model.addAttribute("partnerNameList", customPartnerRepository.getPartnerNameList());

            //총 페이지 수
            model.addAttribute("totalPages", result.getTotalElements());


            redirectUrl = "wishwide/partner/listPartner";
        }
        else if(roleCode.equals("PT")){
            redirectUrl = "redirect:/wishwide/partner/detailPartner/"+sessionId;
        }

        return redirectUrl;
    }

    //등록
    @GetMapping("/registerPartner")
    public void getRegisterPartner(@ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        log.info("파트너 등록 페이지");

    }


    @PostMapping("/postRegisterPartner")
    public String postRegisterPartner(@ModelAttribute("partnerVO") Partner partnerVO,
                                      @RequestParam("userPw") String userPw,
                                      @RequestParam("storeId") List<String> storeIdList,
                                      @ModelAttribute("pageVO") PageVO pageVO,
                                      HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        log.info("등록 데이터 : "+partnerVO + " , 비밀번호 :"+userPw + " 가맹점 리스트 : "+storeIdList);

        //세션 값 get
        HttpSession session = request.getSession();

        //LoginInfo 테이블에 아이디, 비밀번호 저장
        LoginInfo loginInfo = new LoginInfo();

        loginInfo.setUserId(partnerVO.getPartnerId());
        loginInfo.setUserPw(passwordEncoder.encode(userPw));

        loginInfoRepository.save(loginInfo);

        //wwRole 테이블에 권한 등록
        WwRole wwRole = new WwRole();

        wwRole.setUserId(partnerVO.getPartnerId());
        wwRole.setRoleCode("PT");
        wwRole.setRoleName("파트너");

        wwRoleRepository.save(wwRole);

        //ParterStore 테이블에 파트너 - 가맹점 간 연결관계 등록
        for(String storeId : storeIdList){
            PartnerStore partnerStore = new PartnerStore();
            partnerStore.setPartnerId(partnerVO.getPartnerId());
            partnerStore.setStoreId(storeId);

            partnerStoreRepository.save(partnerStore);
        }

        //VO에 세션값 세팅
        partnerVO.setPartnerRegId(session.getAttribute("userId").toString());
        partnerVO.setPartnerUpdateId(session.getAttribute("userId").toString());

        //파트너 정보 저장
        customPartnerRepository.save(partnerVO);

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("파트너 등록 성공");

        return "redirect:/wishwide/partner/listPartner";
    }


    //수정
    @Transactional
    @PostMapping("/update")
    public String updatePartner(@ModelAttribute("partnerVO") Partner partnerVO,
                                @RequestParam("storeId") List<String> storeIdList,
                                @ModelAttribute("pageVO") PageVO pageVO,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        log.info("수정 데이터 : "+partnerVO + ", 가맹점 리스트 : "+storeIdList);

        //세션 값 get
        HttpSession session = request.getSession();

        customPartnerRepository.findById(partnerVO.getPartnerId()).ifPresent(partner -> {
            //기존 partnerStore 테이블 데이터 삭제
            partnerStoreRepository.deletePartnerStore(partnerVO.getPartnerId());

            log.info("파트너 가맹점 삭제 완료");

            //ParterStore 테이블에 파트너 - 가맹점 간 연결관계 등록
            for(String storeId : storeIdList){
                PartnerStore partnerStore = new PartnerStore();
                partnerStore.setPartnerId(partnerVO.getPartnerId());
                partnerStore.setStoreId(storeId);

                partnerStoreRepository.save(partnerStore);
            }

            //VO에 세션값 세팅
            partner.setPartnerOperatorName(partnerVO.getPartnerOperatorName());
            partner.setPartnerOperatorEmail(partnerVO.getPartnerOperatorEmail());
            partner.setPartnerOperatorPhone(partnerVO.getPartnerOperatorPhone());
            partner.setPartnerUpdateId(session.getAttribute("userId").toString());

            //파트너 정보 저장
            customPartnerRepository.save(partner);

            redirectAttributes.addFlashAttribute("message", "successUpdate");
            pageRedirectProperty(redirectAttributes, pageVO);

            log.info("파트너 수정 성공");

        });

        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();
        String redirectUrl = "";

        if(roleCode.equals("MA")){
            redirectUrl = "redirect:/wishwide/partner/listPartner";
        }
        else if(roleCode.equals("PT")){
            redirectUrl = "redirect:/wishwide/partner/detailPartner/"+sessionId;
        }

        return redirectUrl;
    }

    //상세
    @GetMapping("/detailPartner/{partnerId}")
    public String detailStore(@PathVariable("partnerId") String partnerId,
                              @ModelAttribute("pageVO") PageVO pageVO,
                              Model model){
        //매장 정보
        model.addAttribute("partnerVO", customPartnerRepository.getPartnerDetail(partnerId));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        return "wishwide/partner/detailPartner";
    }


    //비밀번호 변경
    @PostMapping("/detailPartner/updatePassword/{partnerId}")
    public ResponseEntity<String> updatePassword(@PathVariable("partnerId") String partnerId,
                                                 @RequestParam("userPw") String userPw,
                                                 RedirectAttributes redirectAttributes,
                                                 PageVO pageVO) {
        log.info("비밀번호 변경"+userPw);
        loginInfoRepository.findById(partnerId).ifPresent(loginInfo -> {
            loginInfo.setUserPw(passwordEncoder.encode(userPw));
            loginInfoRepository.save(loginInfo);
        });

        pageRedirectProperty(redirectAttributes, pageVO);

        return new ResponseEntity<>("1",HttpStatus.CREATED);
    }

    //상품 리스트 가져오기
    @GetMapping("/selectPartnerProduct/{partnerId}")
    public ResponseEntity<List<Object[]>> selectPartnerProduct(@PathVariable("partnerId") String partnerId) {
        log.info("코드 : " + partnerId);
        return new ResponseEntity<>(customProductRepository.getPartnerProductList(partnerId), HttpStatus.CREATED);
    }

    //파트너 가맹점 리스트 가져오기
    @GetMapping("/selectPartnerStore/{partnerId}")
    public ResponseEntity<List<Object[]>> selectPartnerStore(@PathVariable("partnerId") String partnerId) {
        log.info("코드 : " + partnerId);
        return new ResponseEntity<>(customPartnerRepository.getPartnerStore(partnerId), HttpStatus.CREATED);
    }


    //모든 가맹점 리스트 가져오기
    @GetMapping("/selectAllPartnerStore/{partnerId}")
    public ResponseEntity<List<Object[]>> selectAllPartnerStore(@PathVariable("partnerId") String partnerId) {
        return new ResponseEntity<>(customPartnerRepository.getAllPartnerStore(partnerId), HttpStatus.CREATED);
    }
}
