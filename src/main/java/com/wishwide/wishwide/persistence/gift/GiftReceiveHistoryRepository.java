package com.wishwide.wishwide.persistence.gift;

import com.wishwide.wishwide.domain.GiftReceiveHistory;
import com.wishwide.wishwide.domain.Product;
import com.wishwide.wishwide.persistence.product.CustomProduct;
import org.springframework.data.repository.CrudRepository;

public interface GiftReceiveHistoryRepository extends CrudRepository<GiftReceiveHistory, Long> {


}
