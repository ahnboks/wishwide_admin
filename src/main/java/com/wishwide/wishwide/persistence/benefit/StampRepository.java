package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.Coupon;
import com.wishwide.wishwide.domain.Stamp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StampRepository extends CrudRepository<Stamp, Long> {
    @Query(value = "SELECT s FROM Stamp s WHERE s.storeId = ?1 and s.membershipCustomerNo = ?2")
    public Stamp findByCustomerStamp(String storeId, Long membershipCustomerNo);
}
