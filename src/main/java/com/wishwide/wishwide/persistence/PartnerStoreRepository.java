package com.wishwide.wishwide.persistence;

import com.wishwide.wishwide.domain.PartnerStore;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PartnerStoreRepository  extends CrudRepository<PartnerStore, Long> {
    @Modifying
    @Query(value = "DELETE FROM PartnerStore ps WHERE ps.partnerId = ?1")
    public void deletePartnerStore(String partnerId);
}
