package com.wishwide.wishwide.persistence.alarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomAlarmSet {
    //알림 발송 설정 리스트 페이지
    public Page<Object[]> getAlarmSetPage(String type,
                                          String keyword,
                                          String searchUserId,
                                          String roleCode,
                                          String sessionId,
                                          int alarmJoinCode,
                                          String alarmTypeCode,
                                          String alarmPurposeCode,
                                          String alarmTargetTypeCode,
                                          Pageable pageable);

    //알림 메시지 불러오기
    public List<Object[]> getAlarmMessage();

//    //알림 템플릿 상세 페이지
//    public Object[] getAlarmTemplateDetail(Long alarmTemplateNo);


}
