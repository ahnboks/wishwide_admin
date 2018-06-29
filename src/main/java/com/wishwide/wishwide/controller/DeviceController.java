package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.domain.Device;
import com.wishwide.wishwide.persistence.device.DeviceImageRepository;
import com.wishwide.wishwide.persistence.device.CustomDeviceModelRepository;
import com.wishwide.wishwide.persistence.device.CustomDeviceRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import com.wishwide.wishwide.vo.PageMaker;
import com.wishwide.wishwide.vo.PageVO;
import lombok.extern.java.Log;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
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
import static com.wishwide.wishwide.file.FileManager.saveCloudFile;
import static com.wishwide.wishwide.file.FileManager.saveDBDeviceImage;

@Log
@Controller
@RequestMapping("/wishwide/device/")
public class DeviceController {
    @Autowired
    CustomDeviceRepository customDeviceRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomDeviceModelRepository customDeviceModelRepository;

    @Autowired
    DeviceImageRepository deviceImageRepository;

    //리스트
    @GetMapping("/listDevice")
    public void listDevice(HttpServletRequest request,
                            @ModelAttribute("pageVO") PageVO pageVO,
                            Model model) {
        //세션
        HttpSession session = request.getSession();
        //세션 값 세팅
        String userId = session.getAttribute("userId").toString();
        String roleCode = session.getAttribute("userRole").toString();

        log.info("세션 : "+userId+roleCode);

        Pageable pageable = pageVO.makePageable(0, "deviceNo");

        Page<Object[]> result = customDeviceRepository.getDevicePage(
                pageVO.getType(),   //검색조건
                pageVO.getKeyword(),    //키워드
                pageVO.getUserId(),
                pageVO.getDeviceTypeCode(),
                roleCode,
                userId,
                pageVO.getVisibleCode(),
                pageable);

        log.info("결과 값 : " + result);

        log.info("총 페이지 수 : " + result.getTotalPages());

        log.info("총 행 수" + result.getTotalElements());

        result.forEach(device -> {
            log.info("디바이스 정보"+ Arrays.toString(device));
        });

        //디바이스 리스트
        model.addAttribute("deviceVO", new PageMaker<>(result));

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
    }

    //등록
    @GetMapping("/registerDevice")
    public void getRegisterDevice(@ModelAttribute("pageVO") PageVO pageVO,
                                 Model model) {
        log.info("디바이스 등록 페이지");

        //가맹점명 셀렉트 박스
        model.addAttribute("storeNameList", customStoreRepository.getStoreList());

    }

    @PostMapping("/postRegisterDevice")
    public String postRegisterDevice(@ModelAttribute("deviceVO") Device deviceVO,
                                     @RequestParam(value = "deviceImage", required = false) MultipartFile deviceImage,
                                     @RequestParam("deviceMacAddress") List<String> deviceMacAddressList,
                                     RedirectAttributes redirectAttributes,
                                     PageVO pageVO) {
        log.info("등록 데이터 : "+deviceVO + ", 디바이스 맥주소 : "+deviceMacAddressList);

        //디바이스 이미지 파일 등록
        if (deviceImage != null && !(deviceImage.isEmpty())) {
            deviceImageRepository.save(saveDBDeviceImage("DV", deviceVO.getStoreId(), saveCloudFile(deviceImage)));
            log.info("디바이스 이미지 등록 성공");
        }

        //VO에 세션값 세팅

        String deviceMacAddress = "";
        for(String macAddress : deviceMacAddressList){
            deviceMacAddress += macAddress;
        }
        deviceVO.setDeviceMacAddress(deviceMacAddress);

        customDeviceModelRepository.findById(deviceVO.getDeviceModelNo()).ifPresent(deviceModel ->{
            deviceVO.setDeviceModelTitle(deviceModel.getDeviceModelTitle());
        });

        //태블릿일때만 값 세팅
        if(deviceVO.getDeviceTypeCode().equals("TABLET")){
            //가장 마지막에 등록된 디바이스 번호 가져옴
            Long resentDeviceNo = customDeviceRepository.findTop1ByOrderByDeviceNoDesc().getDeviceNo()+1;

            log.info("최신 디바이스 번호 : "+resentDeviceNo);

            String deviceId = "WW_DV"+resentDeviceNo;
            String posId = "WW_POS"+resentDeviceNo;

            String socketId = RandomStringUtils.randomAlphanumeric(8);

            deviceVO.setDeviceId(deviceId);
            deviceVO.setPosId(posId);
            deviceVO.setSocketId(socketId);
        }

        //디바이스 정보 저장
        customDeviceRepository.save(deviceVO);

        redirectAttributes.addFlashAttribute("message", "successRegister");
        pageRedirectProperty(redirectAttributes, pageVO);

        log.info("파트너 등록 성공");

        return "redirect:/wishwide/device/listDevice";
    }

    //상세
    @GetMapping("/detailDevice/{deviceNo}")
    public String detailStore(@PathVariable("deviceNo") Long deviceNo,
                              @ModelAttribute("pageVO") PageVO pageVO,
                              Model model){
        //디바이스 정보
        model.addAttribute("deviceVO", customDeviceRepository.getDeviceDetail(deviceNo));

        //페이징 정보
        model.addAttribute("pageVO", pageVO);

        return "wishwide/device/detailDevice";
    }

    //수정
    @PostMapping("/update")
    public String updateDevice(@ModelAttribute("deviceVO") Device deviceVO,
                               @RequestParam(value = "deviceImage", required = false) MultipartFile deviceImage,
                               @RequestParam("deviceMacAddress") List<String> deviceMacAddressList,
                               RedirectAttributes redirectAttributes,
                               PageVO pageVO){
        log.info("수정 데이터 : "+deviceVO + ", 맥주소 : "+deviceMacAddressList);

        //매장 정보 조회
        customDeviceRepository.findById(deviceVO.getDeviceNo()).ifPresent(device -> {
            //디바이스 이미지 파일 등록
            if (deviceImage != null && !(deviceImage.isEmpty())) {
                deviceImageRepository.save(saveDBDeviceImage("DV", deviceVO.getStoreId(), saveCloudFile(deviceImage)));
                log.info("디바이스 이미지 등록 성공");
            }

            String deviceMacAddress = "";
            for(String macAddress : deviceMacAddressList){
                deviceMacAddress += macAddress;
            }

            device.setDeviceMacAddress(deviceMacAddress);

            customDeviceModelRepository.findById(deviceVO.getDeviceModelNo()).ifPresent(deviceModel ->{
                device.setDeviceModelTitle(deviceModel.getDeviceModelTitle());
            });

            //수정 값 세팅
            device.setDeviceDescription(deviceVO.getDeviceDescription());
            device.setDeviceModelNo(deviceVO.getDeviceModelNo());
            device.setDeviceTitle(deviceVO.getDeviceTitle());
            device.setDeviceTypeCode(deviceVO.getDeviceTypeCode());

            customDeviceRepository.save(device);

            redirectAttributes.addFlashAttribute("message", "successUpdate");

            pageRedirectProperty(redirectAttributes, pageVO);

            log.info("디바이스 수정 성공");
        });


        return "redirect:/wishwide/device/listDevice";

    }



    //디바이스 사용여부 코드 변경
    @GetMapping("/updateDeviceVisibleCode/{deviceNo}/{deviceVisibleCode}")
    public ResponseEntity<String> updateServiceOperationCode(@PathVariable("deviceNo") Long deviceNo,
                                                             @PathVariable("deviceVisibleCode") int deviceVisibleCode) {
        log.info("코드 : " + deviceVisibleCode);

        String resultCode = "";

        //코드 변경
        customDeviceRepository.findById(deviceNo).ifPresent(device -> {
            if(deviceVisibleCode == 1)
                device.setDeviceVisibleCode(0);
            else
                device.setDeviceVisibleCode(1);

            customDeviceRepository.save(device);
        });

        if(deviceVisibleCode == 1)
            resultCode = "0";
        else
            resultCode = "1";

        return new ResponseEntity<>(resultCode, HttpStatus.CREATED);
    }

    //디바이스 모델명 가져오기
    @GetMapping("/selectDeviceModel/{deviceTypeCode}")
    public ResponseEntity<List<Object[]>> selectDeviceModel(@PathVariable("deviceTypeCode")String deviceTypeCode) {
        log.info("코드 : " + deviceTypeCode);

        return new ResponseEntity<>(customDeviceModelRepository.getDeviceModelList(deviceTypeCode), HttpStatus.CREATED);
    }
}
