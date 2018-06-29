package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.PointHistory;
import com.wishwide.wishwide.domain.PointLog;
import org.springframework.data.repository.CrudRepository;

public interface PointLogRepository extends CrudRepository<PointLog, Long> {
}
