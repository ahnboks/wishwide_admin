package com.wishwide.wishwide.persistence.gift;

import com.wishwide.wishwide.domain.GiftPayment;
import com.wishwide.wishwide.domain.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomGiftPaymentRepository extends CrudRepository<GiftPayment, Long>, CustomGiftPayment {
}
