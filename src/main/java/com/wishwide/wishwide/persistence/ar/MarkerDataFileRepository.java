package com.wishwide.wishwide.persistence.ar;

import com.wishwide.wishwide.domain.MarkerDataFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkerDataFileRepository extends CrudRepository<MarkerDataFile, Long> {
    @Query(value = "select md from MarkerDataFile md where md.storeId =?1")
    public MarkerDataFile findMarkerDataFileByStoreId(String storeId);
}
