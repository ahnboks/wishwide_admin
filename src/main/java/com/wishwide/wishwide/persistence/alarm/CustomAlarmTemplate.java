package com.wishwide.wishwide.persistence.alarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomAlarmTemplate {
    //알림 템플릿 리스트 페이지
    public Page<Object[]> getAlarmTemplatePage(String type,
                                               String keyword,
                                               String alarmTypeCode,
                                               String alarmPurposeCode,
                                               String alarmTargetTypeCode,
                                               Pageable pageable);

    //알림 템플릿 상세 페이지
    public Object[] getAlarmTemplateDetail(Long alarmTemplateNo);

    //알림 변수 리스트
    public List<Object[]> getAlarmVariable();

}
