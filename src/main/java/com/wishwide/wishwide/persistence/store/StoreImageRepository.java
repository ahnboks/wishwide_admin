package com.wishwide.wishwide.persistence.store;

import com.wishwide.wishwide.domain.StoreImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StoreImageRepository extends CrudRepository<StoreImage, Long> {
    @Query(value = "select s from StoreImage s where s.storeId =?1 and s.storeImageTypeCode = ?2")
    public StoreImage findByStoreImageAndStoreImageTypeCode(String storeId, String storeImageTypeCode);
}
