package com.wishwide.wishwide.persistence.benefit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBenefit {
    //혜택 조회 리스트 페이지
    Page<Object[]> getBenefitPage(String type,
                                  String keyword,
                                  String searchUserId,
                                  String roleCode,
                                  String sessionId,
                                  Pageable pageable);

    //매장 고객 리스트 불러오기
    List<Object[]> getStoreCustomerList(String storeId);

    //도장 내역 리스트 불러오기
    List<Object[]> getStampHistoryList(Long membershipCustomerNo, String storeId);

    //포인트 리스 불러오기
    List<Object[]> getPointHistoryList(Long membershipCustomerNo, String storeId);
}
