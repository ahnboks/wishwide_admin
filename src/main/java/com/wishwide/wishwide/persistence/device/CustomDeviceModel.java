package com.wishwide.wishwide.persistence.device;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomDeviceModel {
    //디바이스 모델 리스트 페이지
    public Page<Object[]> getDeviceModelPage(
                                       String type,
                                       String keyword,
                                       String deviceTypeCode,
                                       Pageable pageable);

    //디바이스 모델 상세 페이지
    public Object[] getDeviceModelDetail(Long deviceModelNo);

    //디바이스 모델명 리스트
    public List<Object[]> getDeviceModelList(String deviceTypeCode);
}
