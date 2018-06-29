package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.AlarmSendLog;
import com.wishwide.wishwide.domain.MsgQueue;
import org.springframework.data.repository.CrudRepository;

public interface AlarmSendLogRepository extends CrudRepository<AlarmSendLog, Long> {
}
