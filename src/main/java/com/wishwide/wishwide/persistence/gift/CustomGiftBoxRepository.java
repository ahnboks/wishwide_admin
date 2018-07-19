package com.wishwide.wishwide.persistence.gift;

import com.wishwide.wishwide.domain.GiftBox;
import com.wishwide.wishwide.domain.MembershipCustomer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomGiftBoxRepository extends CrudRepository<GiftBox, Long>, CustomGiftBox {
}
