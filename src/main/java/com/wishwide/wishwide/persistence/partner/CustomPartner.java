package com.wishwide.wishwide.persistence.partner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPartner {
//    //매장 리스트 페이지
//    public Page<Object[]> getStorePage(String keyword,
//                                       String searchUserId,
//                                       String roleCode,
//                                       String sessionId,
//                                       String serviceOperationCode,
//                                       Pageable pageable);
//    //매장 상세 페이지
//    public Object[] getStoreDetail(String storeId);

    //파트너명명 리스트
    public List<Object[]> getPartnerNameList();

}
