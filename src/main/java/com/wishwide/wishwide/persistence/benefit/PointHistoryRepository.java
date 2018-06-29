package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.PointHistory;
import com.wishwide.wishwide.domain.StampHistory;
import org.springframework.data.repository.CrudRepository;

public interface PointHistoryRepository extends CrudRepository<PointHistory, Long> {
}
