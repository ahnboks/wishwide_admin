package com.wishwide.wishwide.controller;

import com.wishwide.wishwide.persistence.device.CustomDeviceRepository;
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

@Log
@Controller
@RequestMapping("/wishwide/device/")
public class DeviceController {
    @Autowired
    CustomDeviceRepository customDeviceRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

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
        model.addAttribute("storeNameList", customStoreRepository.getStoreNameList());

        //총 페이지 수
        model.addAttribute("totalPages", result.getTotalElements());
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

}
