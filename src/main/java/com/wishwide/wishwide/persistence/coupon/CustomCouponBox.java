package com.wishwide.wishwide.persistence.coupon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCouponBox {
    //쿠폰 사용 내역 리스트 페이지
    public Page<Object[]> getCouponBoxPage(String type,
                                           String keyword,
                                           String searchUserId,
                                           String roleCode,
                                           String sessionId,
                                           String couponTypeCode,
                                           String couponTargetTypeCode,
                                           String couponPublishTypeCode,
                                           int couponUseCode,
                                           Pageable pageable);

    //매장 쿠폰 사용 내역 리스트 불러오기
    List<Object[]> getCouponBoxList(String storeId);

    //고객 쿠폰 사용 내역 리스트 불러오기
    List<Object[]> getCustomerCouponBoxList(Long customerNo);

    //쿠폰 발행 내역 리스트 불러오기
    List<Object[]> getCouponHistoryList(Long couponNo);
}
