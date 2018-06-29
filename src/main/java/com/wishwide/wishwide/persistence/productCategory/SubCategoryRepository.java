package com.wishwide.wishwide.persistence.productCategory;

import com.wishwide.wishwide.domain.SubCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubCategoryRepository extends CrudRepository<SubCategory, Long> {
    @Query(value = "select s from SubCategory s where s.subCategoryNo =?1")
    public SubCategory findBySubCategoryNo(Long subCategoryNo);
}
