package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.StampHistory;
import com.wishwide.wishwide.domain.StampLog;
import org.springframework.data.repository.CrudRepository;

public interface StampHistoryRepository extends CrudRepository<StampHistory, Long> {
}
