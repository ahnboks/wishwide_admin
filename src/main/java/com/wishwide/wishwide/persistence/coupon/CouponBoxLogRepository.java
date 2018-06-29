package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.CouponBoxLog;
import com.wishwide.wishwide.domain.CouponPublishLog;
import org.springframework.data.repository.CrudRepository;

public interface CouponBoxLogRepository extends CrudRepository<CouponBoxLog, Long> {
}
