package com.wishwide.wishwide.persistence;

import com.wishwide.wishwide.domain.Marker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MarkerRepository extends CrudRepository<Marker, Long> {
    @Modifying
    @Query(value = "UPDATE Marker m SET m.markerVisibleCode = ?1 WHERE m.storeId = ?2")
    public void changeMarkerVisibleCode(int visibleCode, String storeId);
}
