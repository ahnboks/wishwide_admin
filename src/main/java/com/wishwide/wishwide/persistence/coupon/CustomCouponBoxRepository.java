package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.CouponBox;
import org.springframework.data.repository.CrudRepository;

public interface CustomCouponBoxRepository extends CrudRepository<CouponBox, Long>, CustomCouponBox {


}
