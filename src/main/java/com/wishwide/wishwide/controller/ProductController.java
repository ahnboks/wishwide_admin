package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.domain.*;
import com.wishwide.wishwide.persistence.partner.CustomPartnerRepository;
import com.wishwide.wishwide.persistence.product.CustomProductRepository;
import com.wishwide.wishwide.persistence.product.GiftProductRepository;
import com.wishwide.wishwide.persistence.product.ProductImageRepository;
import com.wishwide.wishwide.persistence.product.SubProductRepository;
import com.wishwide.wishwide.persistence.productCategory.CustomProductCategoryRepository;
import com.wishwide.wishwide.persistence.productCategory.MajorCategoryRepository;
import com.wishwide.wishwide.persistence.productCategory.SubCategoryRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import com.wishwide.wishwide.vo.PageMaker;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;
import static com.wishwide.wishwide.file.FileManager.saveCloudFile;
import static com.wishwide.wishwide.file.FileManager.saveDBProductImage;

@Log
@Controller
@RequestMapping("/wishwide/product/")
public class ProductController {
    @Autowired
    CustomProductRepository customProductRepository;

    @Autowired
    GiftProductRepository giftProductRepository;

    @Autowired
    CustomPartnerRepository customPartnerRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    MajorCategoryRepository majorCategoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    SubProductRepository subProductRepository;

    @Autowired
    CustomProductCategoryRepository customProductCategoryRepository;

    private Long productNo;

    /*매장*/

    //매장 상품 리스트
    @GetMapping("/listStoreProduct")
    public void listStoreProduct(HttpServletRequest request,
                                 @ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : " + userId + roleCode);

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
            log.info("매장 상품 정보" + Arrays.toString(product));
        });

        //상품 리스트
        model.addAttribute("productVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //등록
    @GetMapping("/registerStoreProduct")
    public void getRegisterStoreProduct(@ModelAttribute("pageVO") PageVO pageVO,
                                        HttpServletRequest request,
                                        Model model) {
        log.info("상품 등록 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());


        //세션
        HttpSession session = request.getSession();

        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        if(roleCode.equals("ST")){
            //가맹점아이디
            model.addAttribute("storeId", sessionId);

            //상품 카테고리
            model.addAttribute("categoryList", customProductCategoryRepository.getMajorCategoryList(sessionId));
        }
    }

    @PostMapping("/postRegisterStoreProduct")
    public String postRegisterStoreProduct(@ModelAttribute(value = "productVO") Product productVO,
                                           @ModelAttribute(value = "subProductList") ListSubProduct subProductList,
                                           @ModelAttribute(value = "giftProductVO") GiftProduct giftProductVO,
                                           @RequestParam(value = "productImage") MultipartFile productImage,
                                           @ModelAttribute("pageVO") PageVO pageVO,
                                           RedirectAttributes redirectAttributes) {
        log.info("데이터 : " + productVO.toString());
        log.info("데이터 : " + giftProductVO.toString());
        log.info("데이터 : " + productImage);
        subProductList.getSubProductList().forEach(a ->{
            log.info(""+a.toString());
        });

        //대분류명, 중분류명
        String majorCategoryTitle = "";
        String subCategoryTitle = "";

        majorCategoryTitle = majorCategoryRepository.findByMajorCategoryNo(productVO.getMajorCategoryNo()).getMajorCategoryTitle();

        if((!(productVO.getSubCategoryNo() == 0)) && (!(productVO.getSubCategoryNo() == null))){
            subCategoryTitle = subCategoryRepository.findBySubCategoryNo(productVO.getSubCategoryNo()).getSubCategoryTitle();
        }

        //VO 세팅
        productVO.setProductOwnerRole("ST");
        productVO.setMajorCategoryTitle(majorCategoryTitle);

        if(!subCategoryTitle.equals(""))
            productVO.setSubCategoryTitle(subCategoryTitle);

        //상품 이미지 파일 값 저장
        ProductImage pm = saveDBProductImage(productVO.getProductOwnerId(), saveCloudFile(productImage));

        productVO.setProductImageUrl(pm.getProductImageUrl());

        //서브상품 존재 시 서브상품 등록
        subProductList.getSubProductList().forEach(subProduct -> {
            if(!subProduct.getSubProductName().equals("none")) {
                productVO.setProductSubProductCode(1);

                //상품 저장
                productNo = customProductRepository.save(productVO).getProductNo();

                System.out.println("상품번호 : "+productNo);

                SubProduct sub = new SubProduct();
                sub.setProductNo(productNo);
                sub.setSubProductName(subProduct.getSubProductName());
                sub.setSubProductPrice(subProduct.getSubProductPrice());
                sub.setSubProductDiscountValue(subProduct.getSubProductDiscountValue());

                subProductRepository.save(sub);
            }
            else{
                productVO.setProductSubProductCode(0);

                //상품 저장
                productNo =  customProductRepository.save(productVO).getProductNo();
            }
        });

        //상품이미지 VO에 상품번호 set
        pm.setProductNo(productNo);

        //상품 이미지 등록
        productImageRepository.save(pm);

        //선물등록여부코드 1일시 선물등록
        if(productVO.getGiftProductRegisterCode() == 1) {
            //서브상품이 존재할 경우 각각의 서브상품명, 서브상품가격으로 선물 등록
            if(productVO.getProductSubProductCode() == 1) {
                subProductList.getSubProductList().forEach(subProduct -> {
                    GiftProduct giftProduct = new GiftProduct();
                    giftProduct.setSubCategoryNo(productVO.getSubCategoryNo());
                    giftProduct.setSubCategoryTitle(productVO.getSubCategoryTitle());
                    giftProduct.setStoreId(productVO.getProductOwnerId());
                    giftProduct.setProductTitle(productVO.getProductTitle() +" "+subProduct.getSubProductName());
                    giftProduct.setProductPrice(subProduct.getSubProductPrice());
                    giftProduct.setProductImageUrl(productVO.getProductImageUrl());
                    giftProduct.setProductDescription(productVO.getProductDescription());
                    giftProduct.setMajorCategoryNo(productVO.getMajorCategoryNo());
                    giftProduct.setMajorCategoryTitle(productVO.getMajorCategoryTitle());
                    giftProduct.setGiftProductDiscountValue(subProduct.getSubProductDiscountValue());
                    giftProduct.setProductNo(productNo);

                    if(giftProductVO.getGiftBundleDiscountCode() == 1){
                        giftProduct.setGiftBundleDiscountCode(giftProductVO.getGiftBundleDiscountCode());
                        giftProduct.setGiftBundleDiscountTypeCode(giftProductVO.getGiftBundleDiscountTypeCode());
                        giftProduct.setGiftBundleDiscountInitQuantity(giftProductVO.getGiftBundleDiscountInitQuantity());
                        giftProduct.setGiftBundleDiscountValue(giftProductVO.getGiftBundleDiscountValue());
                    }

                    giftProductRepository.save(giftProduct);
                });
            }
            else{
                GiftProduct giftProduct = new GiftProduct();
                giftProduct.setSubCategoryNo(productVO.getSubCategoryNo());
                giftProduct.setSubCategoryTitle(productVO.getSubCategoryTitle());
                giftProduct.setStoreId(productVO.getProductOwnerId());
                giftProduct.setProductTitle(productVO.getProductTitle());
                giftProduct.setProductPrice(productVO.getProductPrice());
                giftProduct.setProductImageUrl(productVO.getProductImageUrl());
                giftProduct.setProductDescription(productVO.getProductDescription());
                giftProduct.setMajorCategoryNo(productVO.getMajorCategoryNo());
                giftProduct.setMajorCategoryTitle(productVO.getMajorCategoryTitle());
                giftProduct.setGiftProductDiscountValue(productVO.getProductDiscountValue());
                giftProduct.setProductNo(productNo);

                //묶음할인코드가 1일경우에만 등록
                if(giftProductVO.getGiftBundleDiscountCode() == 1){
                    giftProduct.setGiftBundleDiscountCode(giftProductVO.getGiftBundleDiscountCode());
                    giftProduct.setGiftBundleDiscountTypeCode(giftProductVO.getGiftBundleDiscountTypeCode());
                    giftProduct.setGiftBundleDiscountInitQuantity(giftProductVO.getGiftBundleDiscountInitQuantity());
                    giftProduct.setGiftBundleDiscountValue(giftProductVO.getGiftBundleDiscountValue());
                }
                giftProductRepository.save(giftProduct);
            }
            log.info("선물 등록 완료");
        }

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("상품 등록 성공");

        return "redirect:/wishwide/product/listStoreProduct";
    }

    //상세
    @GetMapping("/detailStoreProduct/{productNo}")
    public String detailStoreProduct(@PathVariable("productNo") Long productNo,
                                     @ModelAttribute("pageVO") PageVO pageVO,
                                     Model model) {
        log.info("데이터 : "+productNo);

        //매장 정보
        model.addAttribute("productVO", customProductRepository.getStoreProductDetail(productNo));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        //서브상품 개수
        model.addAttribute("subProductCnt", subProductRepository.findBySubProductCnt(productNo));

        return "wishwide/product/detailStoreProduct";
    }

    //수정
    @Transactional
    @PostMapping("/updateStore")
    public String updateStoreProduct(@ModelAttribute(value = "productVO") Product productVO,
                                     @ModelAttribute(value = "subProductList") ListSubProduct subProductList,
                                     @ModelAttribute(value = "giftProductVO") GiftProduct giftProductVO,
                                     @RequestParam(value = "productImage",required = false) MultipartFile productImage,
                                     @ModelAttribute("pageVO") PageVO pageVO,
                                     RedirectAttributes redirectAttributes) {

        log.info("데이터 : " + productVO.toString());
        log.info("데이터 : " + giftProductVO.toString());
        log.info("데이터 : " + productImage);

        subProductList.getSubProductList().forEach(a ->{
            log.info(""+a.toString());
        });

        //상품 정보 조회
        customProductRepository.findById(productVO.getProductNo()).ifPresent(product -> {
            //수정 값 세팅
            product.setProductTitle(productVO.getProductTitle());
            product.setGiftProductRegisterCode(productVO.getGiftProductRegisterCode());
            product.setProductDescription(productVO.getProductDescription());
            product.setProductPrice(productVO.getProductPrice());

            if (productImage != null && !(productImage.isEmpty())) {
                //상품 이미지 파일 값 저장
                ProductImage pm = saveDBProductImage(productVO.getProductOwnerId(), saveCloudFile(productImage));

                //상품이미지 VO에 상품번호 set
                pm.setProductNo(product.getProductNo());

                //상품 이미지 등록
                productImageRepository.save(pm);

                product.setProductImageUrl(pm.getProductImageUrl());
            }

            //기존에 등록한 서브상품 존재 시 서브상품 삭제
            if(subProductRepository.findBySubProductCnt(product.getProductNo()) > 0)
                subProductRepository.deleteSubProduct(product.getProductNo());

            //서브상품 존재 시 서브상품 등록
            subProductList.getSubProductList().forEach(subProduct -> {
                if(!subProduct.getSubProductName().equals("none")) {
                    product.setProductSubProductCode(1);

                    SubProduct sub = new SubProduct();
                    sub.setProductNo(product.getProductNo());
                    sub.setSubProductName(subProduct.getSubProductName());
                    sub.setSubProductPrice(subProduct.getSubProductPrice());
                    sub.setSubProductDiscountValue(subProduct.getSubProductDiscountValue());

                    subProductRepository.save(sub);
                }
                else{
                    product.setProductSubProductCode(0);
                }
            });

            //선물등록여부코드 1일시 선물등록
            if(productVO.getGiftProductRegisterCode() == 1) {
                //기존에 등록한 선물 존재 시 선물 삭제
                if (giftProductRepository.findByGiftProductCnt(product.getProductNo()) != 0) {
                    giftProductRepository.deleteGiftProduct(product.getProductNo());
                }

                //서브상품이 존재할 경우 각각의 서브상품명, 서브상품가격으로 선물 등록
                if(productVO.getProductSubProductCode() == 1) {
                    subProductList.getSubProductList().forEach(subProduct -> {
                        GiftProduct giftProduct = new GiftProduct();
                        giftProduct.setSubCategoryNo(productVO.getSubCategoryNo());
                        giftProduct.setSubCategoryTitle(productVO.getSubCategoryTitle());
                        giftProduct.setStoreId(productVO.getProductOwnerId());
                        giftProduct.setProductTitle(productVO.getProductTitle() +" "+subProduct.getSubProductName());
                        giftProduct.setProductPrice(subProduct.getSubProductPrice());
                        giftProduct.setProductImageUrl(product.getProductImageUrl());
                        giftProduct.setProductDescription(productVO.getProductDescription());
                        giftProduct.setMajorCategoryNo(productVO.getMajorCategoryNo());
                        giftProduct.setMajorCategoryTitle(productVO.getMajorCategoryTitle());
                        giftProduct.setProductNo(product.getProductNo());
                        giftProduct.setGiftProductDiscountValue(subProduct.getSubProductDiscountValue());

                        if(giftProductVO.getGiftBundleDiscountCode() == 1){
                            giftProduct.setGiftBundleDiscountCode(giftProductVO.getGiftBundleDiscountCode());
                            giftProduct.setGiftBundleDiscountTypeCode(giftProductVO.getGiftBundleDiscountTypeCode());
                            giftProduct.setGiftBundleDiscountInitQuantity(giftProductVO.getGiftBundleDiscountInitQuantity());
                            giftProduct.setGiftBundleDiscountValue(giftProductVO.getGiftBundleDiscountValue());
                        }

                        giftProductRepository.save(giftProduct);
                    });
                }
                else{
                    GiftProduct giftProduct = new GiftProduct();
                    giftProduct.setSubCategoryNo(productVO.getSubCategoryNo());
                    giftProduct.setSubCategoryTitle(productVO.getSubCategoryTitle());
                    giftProduct.setStoreId(productVO.getProductOwnerId());
                    giftProduct.setProductTitle(productVO.getProductTitle());
                    giftProduct.setProductPrice(productVO.getProductPrice());
                    giftProduct.setProductImageUrl(product.getProductImageUrl());
                    giftProduct.setProductDescription(productVO.getProductDescription());
                    giftProduct.setMajorCategoryNo(productVO.getMajorCategoryNo());
                    giftProduct.setMajorCategoryTitle(productVO.getMajorCategoryTitle());
                    giftProduct.setProductNo(product.getProductNo());
                    giftProduct.setGiftProductDiscountValue(productVO.getProductDiscountValue());

                    //묶음할인코드가 1일경우에만 등록
                    if(giftProductVO.getGiftBundleDiscountCode() == 1){
                        giftProduct.setGiftBundleDiscountCode(giftProductVO.getGiftBundleDiscountCode());
                        giftProduct.setGiftBundleDiscountTypeCode(giftProductVO.getGiftBundleDiscountTypeCode());
                        giftProduct.setGiftBundleDiscountInitQuantity(giftProductVO.getGiftBundleDiscountInitQuantity());
                        giftProduct.setGiftBundleDiscountValue(giftProductVO.getGiftBundleDiscountValue());
                    }
                    giftProductRepository.save(giftProduct);
                }
                log.info("선물 등록 완료");

            }

            //상품 수정
            customProductRepository.save(product);

            redirectAttributes.addFlashAttribute("message", "successUpdate");

            pageRedirectProperty(redirectAttributes, pageVO);
        });


        return "redirect:/wishwide/product/listStoreProduct";

    }


    /*파트너*/

    //파트너 상품 리스트
    @GetMapping("/listPartnerProduct")
    public void listPartnerProduct(HttpServletRequest request,
                                   @ModelAttribute("pageVO") PageVO pageVO,
                                   Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : " + userId + roleCode);

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
            log.info("파트너 상품 정보" + Arrays.toString(product));
        });

        //상품 리스트
        model.addAttribute("productVO", new PageMaker<>(result));

        //파트너명 셀렉트 박스
        model.addAttribute("partnerNameList", customPartnerRepository.getPartnerNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }


    //등록
    @GetMapping("/registerPartnerProduct")
    public void getRegisterPartnerProduct(@ModelAttribute("pageVO") PageVO pageVO,
                                          HttpServletRequest request,
                                          Model model) {
        log.info("상품 등록 페이지");

        //파트너명 셀렉트 박스
        model.addAttribute("partnerNameList", customPartnerRepository.getPartnerNameList());

        //세션
        HttpSession session = request.getSession();

        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        if(roleCode.equals("PT")){
            //파트너아이디
            model.addAttribute("partnerId", sessionId);
        }
    }

    @PostMapping("/postRegisterPartnerProduct")
    public String postRegisterPartnerProduct(@ModelAttribute(value = "productVO") Product productVO,
                                             @ModelAttribute(value = "subProductList") ListSubProduct subProductList,
                                             @RequestParam(value = "productImage") MultipartFile productImage,
                                             @ModelAttribute("pageVO") PageVO pageVO,
                                             RedirectAttributes redirectAttributes) {
        log.info("데이터 : " + productVO.toString());
        log.info("데이터 : " + productImage);
        subProductList.getSubProductList().forEach(a ->{
            log.info(""+a.toString());
        });

        //VO 세팅
        productVO.setProductOwnerRole("PT");

        //상품 이미지 파일 값 저장
        ProductImage pm = saveDBProductImage(productVO.getProductOwnerId(), saveCloudFile(productImage));

        productVO.setProductImageUrl(pm.getProductImageUrl());

        //서브상품 존재 시 서브상품 등록
        subProductList.getSubProductList().forEach(subProduct -> {
            if(!subProduct.getSubProductName().equals("none")) {
                productVO.setProductSubProductCode(1);

                //상품 저장
                productNo = customProductRepository.save(productVO).getProductNo();

                System.out.println("상품번호 : "+productNo);

                SubProduct sub = new SubProduct();
                sub.setProductNo(productNo);
                sub.setSubProductName(subProduct.getSubProductName());
                sub.setSubProductPrice(subProduct.getSubProductPrice());
                sub.setSubProductDiscountValue(subProduct.getSubProductDiscountValue());

                subProductRepository.save(sub);
            }
            else{
                productVO.setProductSubProductCode(0);

                //상품 저장
                productNo =  customProductRepository.save(productVO).getProductNo();
            }
        });

        //상품이미지 VO에 상품번호 set
        pm.setProductNo(productNo);

        //상품 이미지 등록
        productImageRepository.save(pm);

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("상품 등록 성공");

        return "redirect:/wishwide/product/listPartnerProduct";
    }

    //상세
    @GetMapping("/detailPartnerProduct/{productNo}")
    public String detailPartnerProduct(@PathVariable("productNo") Long productNo,
                                       @ModelAttribute("pageVO") PageVO pageVO,
                                       Model model) {
        log.info("데이터 : "+productNo);

        //매장 정보
        model.addAttribute("productVO", customProductRepository.getPartnerProductDetail(productNo));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        //서브상품 개수
        model.addAttribute("subProductCnt", subProductRepository.findBySubProductCnt(productNo));

        return "wishwide/product/detailPartnerProduct";
    }

    //수정
    @Transactional
    @PostMapping("/updatePartner")
    public String updatePartnerProduct(@ModelAttribute(value = "productVO") Product productVO,
                                       @ModelAttribute(value = "subProductList") ListSubProduct subProductList,
                                       @RequestParam(value = "productImage", required = false) MultipartFile productImage,
                                       @ModelAttribute("pageVO") PageVO pageVO,
                                       RedirectAttributes redirectAttributes) {

        log.info("데이터 : " + productVO.toString());
        log.info("데이터 : " + productImage);

        subProductList.getSubProductList().forEach(a ->{
            log.info(""+a.toString());
        });

        //상품 정보 조회
        customProductRepository.findById(productVO.getProductNo()).ifPresent(product -> {
            //수정 값 세팅
            product.setProductTitle(productVO.getProductTitle());
            product.setGiftProductRegisterCode(productVO.getGiftProductRegisterCode());
            product.setProductDescription(productVO.getProductDescription());
            product.setProductPrice(productVO.getProductPrice());

            if (productImage != null && !(productImage.isEmpty())) {
                //상품 이미지 파일 값 저장
                ProductImage pm = saveDBProductImage(productVO.getProductOwnerId(), saveCloudFile(productImage));

                //상품이미지 VO에 상품번호 set
                pm.setProductNo(product.getProductNo());

                //상품 이미지 등록
                productImageRepository.save(pm);

                product.setProductImageUrl(pm.getProductImageUrl());
            }

            //기존에 등록한 서브상품 존재 시 서브상품 삭제
            if(subProductRepository.findBySubProductCnt(product.getProductNo()) > 0)
                subProductRepository.deleteSubProduct(product.getProductNo());

            //서브상품 존재 시 서브상품 등록
            subProductList.getSubProductList().forEach(subProduct -> {
                if(!subProduct.getSubProductName().equals("none")) {
                    product.setProductSubProductCode(1);

                    SubProduct sub = new SubProduct();
                    sub.setProductNo(product.getProductNo());
                    sub.setSubProductName(subProduct.getSubProductName());
                    sub.setSubProductPrice(subProduct.getSubProductPrice());
                    sub.setSubProductDiscountValue(subProduct.getSubProductDiscountValue());

                    subProductRepository.save(sub);
                }
                else{
                    product.setProductSubProductCode(0);
                }
            });

            //상품 수정
            customProductRepository.save(product);

            redirectAttributes.addFlashAttribute("message", "successUpdate");

            pageRedirectProperty(redirectAttributes, pageVO);
        });


        return "redirect:/wishwide/product/listPartnerProduct";

    }

    //상품 판매 코드 변경
    @Transactional
    @GetMapping("/updateProductSellStatusCode/{productNo}/{productSellStatusCode}")
    public ResponseEntity<String> updateServiceOperationCode(@PathVariable("productNo") Long productNo,
                                                             @PathVariable("productSellStatusCode") int productSellStatusCode) {
        log.info("판매 코드 : " + productSellStatusCode);

        String resultCode = "";

        //판매 코드 변경
        customProductRepository.findById(productNo).ifPresent(product -> {
            if (productSellStatusCode == 1) {
                product.setProductSellStatusCode(0);

                if(product.getGiftProductRegisterCode() == 1) {
                    //선물 연쇄 비활성화
                    giftProductRepository.changeGiftProductVisibleCode(0, productNo);
                }
            } else {
                product.setProductSellStatusCode(1);

                if(product.getGiftProductRegisterCode() == 1) {
                    //선물 연쇄 활성화
                    giftProductRepository.changeGiftProductVisibleCode(1, productNo);
                }
            }

            customProductRepository.save(product);
        });

        if (productSellStatusCode == 1)
            resultCode = "0";
        else
            resultCode = "1";

        return new ResponseEntity<>(resultCode, HttpStatus.CREATED);
    }

    //대분류명 가져오기
    @GetMapping("/selectMajorCategory/{storeId}")
    public ResponseEntity<List<Object[]>> selectMajorCategory(@PathVariable("storeId") String storeId) {
        log.info("코드 : " + storeId);
        return new ResponseEntity<>(customProductCategoryRepository.getMajorCategoryList(storeId), HttpStatus.CREATED);
    }

    //중분류명 가져오기
    @GetMapping("/selectSubCategory/{majorCategoryNo}")
    public ResponseEntity<List<Object[]>> selectSubCategory(@PathVariable("majorCategoryNo") Long majorCategoryNo) {
        log.info("코드 : " + majorCategoryNo);
        return new ResponseEntity<>(customProductCategoryRepository.getSubCategoryList(majorCategoryNo), HttpStatus.CREATED);
    }

    //서브상품목록 가져오기
    @GetMapping("/selectSubProduct/{productNo}")
    public ResponseEntity<List<Object[]>> selectSubProduct(@PathVariable("productNo") Long productNo) {
        log.info("코드 : " + productNo);
        return new ResponseEntity<>(customProductRepository.getSubProductList(productNo), HttpStatus.CREATED);
    }

    //선물상품 가져오기
    @GetMapping("/selectGiftProduct/{productNo}")
    public ResponseEntity<List<Object[]>> selectGiftProduct(@PathVariable("productNo") Long productNo) {
        log.info("코드 : " + productNo);
        return new ResponseEntity<>(customProductRepository.getGiftProduct(productNo), HttpStatus.CREATED);
    }

}
