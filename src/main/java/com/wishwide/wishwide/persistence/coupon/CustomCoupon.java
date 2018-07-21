package com.wishwide.wishwide.persistence.coupon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCoupon {
    //쿠폰 발행 리스트 페이지
    public Page<Object[]> getCouponPage(String type,
                                           String keyword,
                                           String searchUserId,
                                           String roleCode,
                                           String sessionId,
                                           String couponTypeCode,
                                           String couponTargetTypeCode,
                                           String couponPublishTypeCode,
                                           Pageable pageable);

    //쿠폰 상세
    public Object[] getCouponDetail(Long couponNo);
}
