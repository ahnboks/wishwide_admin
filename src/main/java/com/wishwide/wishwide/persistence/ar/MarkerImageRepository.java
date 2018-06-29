package com.wishwide.wishwide.persistence.ar;

import com.wishwide.wishwide.domain.MarkerImageFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkerImageRepository extends CrudRepository<MarkerImageFile, Long> {
    @Query(value = "select mi from MarkerImageFile mi where mi.storeId =?1")
    public List<MarkerImageFile> findMarkerImageByStoreId(String storeId);
}
