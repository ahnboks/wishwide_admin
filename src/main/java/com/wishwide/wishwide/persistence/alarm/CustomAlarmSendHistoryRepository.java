package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.AlarmSendHistory;
import org.springframework.data.repository.CrudRepository;

public interface CustomAlarmSendHistoryRepository extends CrudRepository<AlarmSendHistory, Long>, CustomAlarmSendHistory {


}
