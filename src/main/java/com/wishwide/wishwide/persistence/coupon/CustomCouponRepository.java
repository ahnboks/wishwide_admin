package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.Coupon;
import com.wishwide.wishwide.domain.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomCouponRepository extends CrudRepository<Coupon, Long>, CustomCoupon {
    @Modifying
    @Query(value = "UPDATE Coupon c SET c.couponVisibleCode = ?1 WHERE c.storeId = ?2")
    public void changeCouponVisibleCode(int visibleCode, String storeId);
}
