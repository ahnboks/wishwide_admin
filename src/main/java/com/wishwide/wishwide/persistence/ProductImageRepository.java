package com.wishwide.wishwide.persistence;

import com.wishwide.wishwide.domain.ProductImage;
import org.springframework.data.repository.CrudRepository;

public interface ProductImageRepository extends CrudRepository<ProductImage, Long> {
}
