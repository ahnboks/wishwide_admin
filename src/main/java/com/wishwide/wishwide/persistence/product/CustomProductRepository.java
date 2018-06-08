package com.wishwide.wishwide.persistence.product;

import com.wishwide.wishwide.domain.Device;
import com.wishwide.wishwide.domain.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomProductRepository extends CrudRepository<Product, Long>, CustomProduct {
    @Modifying
    @Query(value = "UPDATE Product p SET p.productSellStatusCode = ?1 WHERE p.productOwnerId = ?2")
    public void changeProductVisibleCode(int productSellStatusCode, String storeId);
}
