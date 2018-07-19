package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.Coupon;
import com.wishwide.wishwide.domain.Point;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PointRepository extends CrudRepository<Point, Long> {
    @Query(value = "SELECT p FROM Point p WHERE p.storeId = ?1 and p.membershipCustomerNo = ?2")
    public Point findByCustomerPoint(String storeId, Long membershipCustomerNo);
}
