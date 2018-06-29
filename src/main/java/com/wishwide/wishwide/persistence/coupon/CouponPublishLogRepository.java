package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.CouponPublishHistory;
import com.wishwide.wishwide.domain.CouponPublishLog;
import org.springframework.data.repository.CrudRepository;

public interface CouponPublishLogRepository extends CrudRepository<CouponPublishLog, Long> {
}
