package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.Alarm;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface CustomAlarmSetRepository extends CrudRepository<Alarm, Long>, CustomAlarmSet {
    @Modifying
    @Query(value = "UPDATE Alarm a SET a.alarmVisibleCode = ?1 WHERE a.storeId = ?2")
    public void changeAlarmVisibleCode(int visibleCode, String storeId);

    @Query(nativeQuery = true,
            value = "insert into msg_queue (msg_type,dstaddr,callback,stat,text,request_time) " +
                    "values ('3',?1,?2,'0',?3, ?4)")
    public void insertMsgQueue(String receiverPhone, String SenderPhone, String text, LocalDateTime requestTime);
}
