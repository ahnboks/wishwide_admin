package com.wishwide.wishwide.persistence.product;

import com.wishwide.wishwide.domain.ProductImage;
import com.wishwide.wishwide.domain.SubProduct;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubProductRepository extends CrudRepository<SubProduct, Long> {
    @Modifying
    @Query(value = "DELETE FROM SubProduct sp WHERE sp.productNo = ?1")
    public void deleteSubProduct(Long productNo);

    @Query(value = "select count(sp.subProductNo) from SubProduct sp where sp.productNo = ?1")
    public int findBySubProductCnt(Long productNo);
}
