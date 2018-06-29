package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.GiftProduct;
import com.wishwide.wishwide.domain.MsgQueue;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MsgQueueRepository extends CrudRepository<MsgQueue, Integer> {
}
