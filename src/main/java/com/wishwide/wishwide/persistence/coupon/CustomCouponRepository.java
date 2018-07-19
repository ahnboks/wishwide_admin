package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.Coupon;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomCouponRepository extends CrudRepository<Coupon, Long>, CustomCoupon {
    @Modifying
    @Query(value = "UPDATE Coupon c SET c.couponVisibleCode = ?1 WHERE c.storeId = ?2")
    public void changeCouponVisibleCode(int visibleCode, String storeId);

    @Query(value = "SELECT c FROM Coupon c WHERE c.storeId = ?1 and c.couponTargetTypeCode = 'STAMP'")
    public Coupon findByStoreBenefitCoupon(String storeId);

    @Query(value = "SELECT count(c) FROM Coupon c WHERE c.storeId = ?1 and c.couponTargetTypeCode = ?2")
    public int findByStoreRegisterCoupon(String storeId, String couponTargetTypeCode);
}
