package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.AlarmMessageVariable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlarmMessageVariableRepository extends CrudRepository<AlarmMessageVariable, Long> {
    @Query(value = "select amv from AlarmMessageVariable amv where amv.alarmNo =?1")
    public List<AlarmMessageVariable> findAlarmMessageVariableByAlarmNo(Long alarmNo);
}
