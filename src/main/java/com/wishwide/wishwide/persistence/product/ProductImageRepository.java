package com.wishwide.wishwide.persistence.product;

import com.wishwide.wishwide.domain.GiftProduct;
import com.wishwide.wishwide.domain.ProductImage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
    @Query(value = "select p from ProductImage p where p.productNo =?1")
    public ProductImage findByProductNo(Long productNo);
}
