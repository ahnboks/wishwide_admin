package com.wishwide.wishwide.persistence.product;

import com.wishwide.wishwide.domain.GiftProduct;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GiftProductRepository extends CrudRepository<GiftProduct, Long> {
    @Modifying
    @Query(value = "UPDATE GiftProduct gp SET gp.giftProductVisibleCode = ?1 WHERE gp.storeId = ?2")
    public int changeAllGiftProductVisibleCode(int visibleCode, String storeId);

    @Modifying
    @Query(value = "UPDATE GiftProduct gp SET gp.giftProductVisibleCode = ?1 WHERE gp.productNo = ?2")
    public int changeGiftProductVisibleCode(int visibleCode, Long productNo);

    @Modifying
    @Query(value = "DELETE FROM GiftProduct gp WHERE gp.productNo = ?1")
    public void deleteGiftProduct(Long productNo);

    @Query(value = "select count(gp.giftProductNo) from GiftProduct gp where gp.productNo = ?1")
    public int findByGiftProductCnt(Long productNo);
}
