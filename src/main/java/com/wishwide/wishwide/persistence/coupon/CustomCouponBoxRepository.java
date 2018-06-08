package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.CouponBox;
import com.wishwide.wishwide.domain.Customer;
import com.wishwide.wishwide.persistence.customer.CustomCustomer;
import org.springframework.data.repository.CrudRepository;

public interface CustomCouponBoxRepository extends CrudRepository<CouponBox, Long>, CustomCouponBox {


}
