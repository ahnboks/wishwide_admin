package com.wishwide.wishwide.persistence.productCategory;

import com.wishwide.wishwide.domain.MajorCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MajorCategoryRepository extends CrudRepository<MajorCategory, Long> {
    @Query(value = "select m from MajorCategory m where m.majorCategoryNo =?1")
    public MajorCategory findByMajorCategoryNo(Long majorCategoryNo);
}
