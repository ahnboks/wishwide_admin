package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.Alarm;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomAlarmSetRepository extends CrudRepository<Alarm, Long>, CustomAlarmSet {
    default void onTimeout() {
        System.out.println("레파지토리 호출");
    }

    @Modifying
    @Query(value = "UPDATE Alarm a SET a.alarmVisibleCode = ?1 WHERE a.storeId = ?2")
    public void changeAlarmVisibleCode(int visibleCode, String storeId);

    @Query(value = "select a from Alarm a WHERE a.storeId = ?1")
    public List<Alarm> findByAlarmSetByStoreId(String storeId);

}
