package com.wishwide.wishwide.persistence.device;

import java.util.List;

public interface CustomDeviceModel {
    //매장 리스트 페이지
//    public Page<Object[]> getStorePage(String keyword,
//                                       String searchUserId,
//                                       String roleCode,
//                                       String sessionId,
//                                       String serviceOperationCode,
//                                       Pageable pageable);

//    //매장 상세 페이지
//    public Object[] getStoreDetail(String storeId);

    //디바이스 모델명 리스트
    public List<Object[]> getDeviceModelList(String deviceTypeCode);
}
