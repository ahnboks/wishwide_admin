package com.wishwide.wishwide.persistence.gift;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomGiftPayment {
    //선물거래내역 리스트 페이지
    public Page<Object[]> getGiftPaymentPage(String type,
                                             String keyword,
                                             String searchUserId,
                                             String roleCode,
                                             String sessionId,
                                             String giftPaymentStatusCode,
                                             Pageable pageable);

    //선물거래내역 리스트 불러오기
    List<Object[]> getGiftPaymentList(String storeId);

}
