package com.wishwide.wishwide.persistence.store;

import com.wishwide.wishwide.domain.StoreFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StoreFileRepository extends CrudRepository<StoreFile, Long> {
    @Query(value = "select s from StoreFile s where s.storeId =?1 and s.storeFileTypeCode = ?2")
    public StoreFile findByStoreIdAndStoreFileTypeCode(String storeId, String storeFileTypeCode);
}
