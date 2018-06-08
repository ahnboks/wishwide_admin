package com.wishwide.wishwide.persistence;

import com.wishwide.wishwide.domain.WwBrand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WwBrandRepository extends CrudRepository<WwBrand, Long> {
    @Query(value = "select count(wb.brandNo) from WwBrand wb where wb.brandName = ?1")
    public int checkBrandName(String brandName);
}
