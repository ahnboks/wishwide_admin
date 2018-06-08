package com.wishwide.wishwide.persistence.product;

import com.wishwide.wishwide.domain.GiftProduct;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GiftProductRepository extends CrudRepository<GiftProduct, Long> {
    @Modifying
    @Query(value = "UPDATE GiftProduct gp SET gp.giftProductVisibleCode = ?1 WHERE gp.storeId = ?2")
    public int changeGiftProductVisibleCode(int visibleCode, String storeId);
}
