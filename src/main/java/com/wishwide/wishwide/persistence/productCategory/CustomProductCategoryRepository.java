package com.wishwide.wishwide.persistence.productCategory;

import com.wishwide.wishwide.domain.MajorCategory;
import com.wishwide.wishwide.domain.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomProductCategoryRepository extends CrudRepository<MajorCategory, Long>, CustomProductCategory {

}
