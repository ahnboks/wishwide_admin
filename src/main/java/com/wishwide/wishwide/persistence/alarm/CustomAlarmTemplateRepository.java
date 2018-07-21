package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.Alarm;
import com.wishwide.wishwide.domain.AlarmTemplate;
import com.wishwide.wishwide.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomAlarmTemplateRepository extends CrudRepository<AlarmTemplate, Long>, CustomAlarmTemplate {
    @Query(value = "select at.alarmMessage from AlarmTemplate at WHERE at.alarmTemplateNo = ?1")
    public String findByAlarmMessageByAlarmTemplateNo(Long alarmTemplateNo);

    //알림 템플릿 리스트
    @Query(value = "select at from AlarmTemplate at")
    public List<AlarmTemplate> getAlarmTemplateList();
}
