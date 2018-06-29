package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.CouponBoxHistory;
import com.wishwide.wishwide.domain.CouponBoxLog;
import org.springframework.data.repository.CrudRepository;

public interface CouponBoxHistoryRepository extends CrudRepository<CouponBoxHistory, Long> {
}
