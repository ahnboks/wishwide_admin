package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.domain.Marker;
import com.wishwide.wishwide.domain.MarkerDataFile;
import com.wishwide.wishwide.domain.MarkerImageFile;
import com.wishwide.wishwide.persistence.ar.CustomMarkerRepository;
import com.wishwide.wishwide.persistence.ar.MarkerDataFileRepository;
import com.wishwide.wishwide.persistence.ar.MarkerImageRepository;
import com.wishwide.wishwide.persistence.ar.MarkerRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static com.wishwide.wishwide.controller.DefaultController.pageRedirectProperty;
import static com.wishwide.wishwide.file.FileManager.*;

@Log
@Controller
@RequestMapping("/wishwide/ar/")
public class ArController {
    @Autowired
    CustomMarkerRepository customMarkerRepository;

    @Autowired
    MarkerDataFileRepository markerDataFileRepository;

    @Autowired
    MarkerImageRepository markerImageRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    MarkerRepository markerRepository;

    private int storeArGameTypeCode;
    private String storeArGameTypeName = "";

    //리스트
    @GetMapping("/listMarker")
    public void listMarker(@ModelAttribute("pageVO") PageVO pageVO,
                             HttpServletRequest request,
                             Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String sessionId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+sessionId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "storeId");

        Page<Object[]> result = customMarkerRepository.getMarkerPage(
                pageVO.getUserId(),
                roleCode,
                sessionId,
                pageVO.getStoreArGameTypeCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(markerVO -> {
            log.info("마커 정보"+ Arrays.toString(markerVO));
        });

        //마커 리스트
        model.addAttribute("markerVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //마커등록(GET)
    @GetMapping("/registerMarker")
    public void getRegisterMarker(Model model,
                                  HttpServletRequest request,
                                  @ModelAttribute("pageVO") PageVO pageVO) {
        log.info("마커등록");

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

    //마커등록(POST)
    @PostMapping("/registerMarker")
    public String postRegisterMarker(@RequestParam("storeId") String storeId,
                                     @RequestParam(value = "datFile") MultipartFile datFile,
                                     @RequestParam(value = "xmlFile") MultipartFile xmlFile,
                                     @RequestParam(value = "imageFiles") MultipartFile[] imageFiles,
                                     PageVO pageVO,
                                     RedirectAttributes redirectAttributes) {
        log.info("매장 아이디 "+ storeId);

        //파일
        if ((datFile != null && !(datFile.isEmpty()))&&(xmlFile != null && !(xmlFile.isEmpty()))) {
            //클라우드에 파일 업로드
            log.info("dat,xml 파일 입력 시도!");
            MarkerDataFile markerDataFileVO = saveDBMarkerDataFile(saveCloudFile(datFile), saveCloudFile(xmlFile));

            //DB에 파일 업로드
            markerDataFileVO.setStoreId(storeId);

            markerDataFileRepository.save(markerDataFileVO);

            log.info("dat, xml 파일 업로드 성공");

            //QR 이미지 파일 업로드
            if (imageFiles != null && imageFiles.length >0) {
                String fileName = "";
                String msg = "";
                for(int i =0 ;i< imageFiles.length; i++){
                    try {
                        if (true == imageFiles[i].isEmpty()) {
                            continue;
                        }
                        fileName = imageFiles[i].getOriginalFilename().substring(0, imageFiles[i].getOriginalFilename().indexOf("."));

                        log.info("파일 이름"+fileName);

                        //마커정보 저장
                        customStoreRepository.findById(storeId).ifPresent(store -> {
                            storeArGameTypeCode = store.getStoreArGameTypeCode();
                            storeArGameTypeName = store.getStoreArGameTypeName();
                        });
                        Marker marker = new Marker();
                        marker.setMarkerTouchEventCode("R");
                        marker.setStoreArGameTypeCode(storeArGameTypeCode);
                        marker.setStoreArGameTypeName(storeArGameTypeName);
                        marker.setStoreId(storeId);

                        Long markerNo = markerRepository.save(marker).getMarkerNo();

                        //마커파일 저장
                        markerImageRepository.save(saveDBMarkerImage(
                                markerNo,
                                storeId,
                                saveCloudFile(imageFiles[i])));

                        msg += "업로드 성공 " + fileName +"<br/>";

                        redirectAttributes.addFlashAttribute("message", "registerSuccess");

                        pageRedirectProperty(redirectAttributes,pageVO);
                    } catch (Exception e) {
                        return "업로드 실패 " + fileName + ": " + e.getMessage() +"<br/>";
                    }
                }
                log.info("업로드한 파일 목록 : "+msg);
            } else {
                return "파일 비었음";
            }
        }

        return "redirect:/wishwide/ar/listMarker";
    }

    //마커상세
    @GetMapping("/detailMarker/{storeId}")
    public String detailMarker(@PathVariable("storeId") String storeId,
                               @ModelAttribute("pageVO") PageVO pageVO,
                               Model model) {


        //마커정보
        model.addAttribute("markerVO", customMarkerRepository.getMarkerDetail(storeId));

        model.addAttribute("pageVO", pageVO);

        return "wishwide/ar/detailMarker";
    }

    //마커수정
    @PostMapping("/update")
    public String updateMarker(@RequestParam("storeId") String storeId,
                               @RequestParam(value = "datFile") MultipartFile datFile,
                               @RequestParam(value = "xmlFile") MultipartFile xmlFile,
                               @RequestParam(value = "imageFiles") MultipartFile[] imageFiles,
                               PageVO pageVO,
                               RedirectAttributes redirectAttributes) {
        //파일
        if ((datFile != null && !(datFile.isEmpty()))&&(xmlFile != null && !(xmlFile.isEmpty()))) {
            //클라우드에 파일 업로드
            log.info("dat,xml 파일 입력 시도!");
            MarkerDataFile markerDataFileVO = saveDBMarkerDataFile(saveCloudFile(datFile), saveCloudFile(xmlFile));

            //DB에 파일 업로드
            markerDataFileVO.setStoreId(storeId);

            markerDataFileRepository.save(markerDataFileVO);

            log.info("dat, xml 파일 업로드 성공");

            //QR 이미지 파일 업로드
            if (imageFiles != null && imageFiles.length >0) {
                String fileName = "";
                String msg = "";
                for(int i =0 ;i< imageFiles.length; i++){
                    try {
                        if (true == imageFiles[i].isEmpty()) {
                            continue;
                        }
                        fileName = imageFiles[i].getOriginalFilename().substring(0, imageFiles[i].getOriginalFilename().indexOf("."));

                        log.info("파일 이름"+fileName);

                        //마커정보 저장
                        customStoreRepository.findById(storeId).ifPresent(store -> {
                            storeArGameTypeCode = store.getStoreArGameTypeCode();
                            storeArGameTypeName = store.getStoreArGameTypeName();
                        });
                        Marker marker = new Marker();
                        marker.setMarkerTouchEventCode("R");
                        marker.setStoreArGameTypeCode(storeArGameTypeCode);
                        marker.setStoreArGameTypeName(storeArGameTypeName);
                        marker.setStoreId(storeId);

                        Long markerNo = markerRepository.save(marker).getMarkerNo();

                        //마커파일 저장
                        markerImageRepository.save(saveDBMarkerImage(
                                markerNo,
                                storeId,
                                saveCloudFile(imageFiles[i])));

                        msg += "업로드 성공 " + fileName +"<br/>";

                        redirectAttributes.addFlashAttribute("message", "registerSuccess");

                        pageRedirectProperty(redirectAttributes,pageVO);
                    } catch (Exception e) {
                        return "업로드 실패 " + fileName + ": " + e.getMessage() +"<br/>";
                    }
                }
                log.info("업로드한 파일 목록 : "+msg);
            } else {
                return "파일 비었음";
            }
        }

        redirectAttributes.addFlashAttribute("message", "updateSuccess");


        return "redirect:/wishwide/ar/listMarker";
    }

    //마커 이미지파일
    @GetMapping("/getMarkerImage/{storeId}")
    public ResponseEntity<List<MarkerImageFile>> getMarkerImageFileInfo(@PathVariable("storeId")String storeId){
        return new ResponseEntity<>(
                markerImageRepository.findMarkerImageByStoreId(storeId),
                HttpStatus.OK);
    }

    /*AR게임내역*/

    //리스트
//    @GetMapping("/listMarker")
//    public void listMarker(@ModelAttribute("pageVO") PageVO pageVO,
//                           HttpServletRequest request,
//                           Model model) {
//        //세션
//        HttpSession session = request.getSession();
//        //세션 값 세팅
//        String sessionId = session.getAttribute("userId").toString();
//        String roleCode = session.getAttribute("userRole").toString();
//
//        log.info("세션 : "+sessionId+roleCode);
//
//        Pageable pageable = pageVO.makePageable(0, "storeId");
//
//        Page<Object[]> result = customMarkerRepository.getMarkerPage(
//                pageVO.getUserId(),
//                roleCode,
//                sessionId,
//                pageVO.getStoreArGameTypeCode(),
//                pageable);
//
//        log.info("결과 값 : " + result);
//
//        log.info("총 페이지 수 : " + result.getTotalPages());
//
//        log.info("총 행 수" + result.getTotalElements());
//
//        result.forEach(markerVO -> {
//            log.info("마커 정보"+ Arrays.toString(markerVO));
//        });
//
//        //마커 리스트
//        model.addAttribute("markerVO", new PageMaker<>(result));
//
//        //가맹점명 셀렉트 박스
//        model.addAttribute("storeNameList", customStoreRepository.getStoreList());
//
//        //총 페이지 수
//        model.addAttribute("totalPages", result.getTotalElements());
//    }

}
