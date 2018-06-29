package com.wishwide.wishwide.persistence.device;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomDevice {
    //디바이스 리스트 페이지
    public Page<Object[]> getDevicePage(String type,
                                        String keyword,
                                        String searchUserId,
                                        String deviceTypeCode,
                                        String roleCode,
                                        String sessionId,
                                        int visibleCode,
                                        Pageable pageable);

    //디바이스 상세 페이지
    public Object[] getDeviceDetail(Long deviceNo);

    //매장 디바이스 리스트 불러오기
    List<Object[]> getStoreDeviceList(String storeId);

}
