package com.wishwide.wishwide.persistence.alarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomAlarmSendHistory {
    //알림 발송 내역 리스트 페이지
    Page<Object[]> getAlarmSendHistoryPage(String type,
                                           String keyword,
                                           String searchUserId,
                                           String roleCode,
                                           String sessionId,
                                           int alarmJoinCode,
                                           String alarmTypeCode,
                                           String alarmPurposeCode,
                                           String alarmTargetTypeCode,
                                           Pageable pageable);

}
