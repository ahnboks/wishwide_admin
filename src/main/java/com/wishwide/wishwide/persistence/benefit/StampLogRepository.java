package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.Stamp;
import com.wishwide.wishwide.domain.StampLog;
import org.springframework.data.repository.CrudRepository;

public interface StampLogRepository extends CrudRepository<StampLog, Long> {
}
