package com.wishwide.wishwide.persistence.alarm;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomAlarmSetRepositoryImpl extends QuerydslRepositorySupport implements CustomAlarmSet {
    public CustomAlarmSetRepositoryImpl(){
        super(AlarmTemplate.class);
    }

    @Override
    public Page<Object[]> getAlarmSetPage(String type,
                                          String keyword,
                                          String searchUserId,
                                          String roleCode,
                                          String sessionId,
                                          String alarmTypeCode,
                                          String alarmPurposeCode,
                                          String alarmTargetTypeCode,
                                          Pageable pageable) {
        QAlarm alarm = QAlarm.alarm;
        QStore store = QStore.store;

        JPQLQuery<Alarm> query = from(alarm);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarm.alarmNo,  //알림번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                alarm.alarmTpNo,  //알림템플릿번호3
                alarm.alarmTypeCode,    //알림유형코드4
                alarm.alarmUpdatedate,    //5
                alarm.alarmPurposeCode, //알림목적코드6
                alarm.alarmPurposeName, //알림목적명7
                alarm.alarmSendPointCode,   //알림발송시점코드8
                alarm.alarmSendPointName,   //알림발송시점명9
                alarm.alarmTargetTypeCode,  //알림대상자코드10
                alarm.alarmMessage,//알림메시지11
                alarm.alarmSendWayCode, //발송수단코드12
                alarm.alarmMessage,     //알림메시지13
                alarm.alarmVisibleCode,  //알림설정여부코드14
                alarm.alarmMessageUpdateCode    //알림메시지업데이트여부코드15
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(store).on(alarm.storeId.eq(store.storeId));

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if (roleCode.equals("ST"))
            tupleJPQLQuery.where(alarm.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(alarm.storeId.eq(searchUserId));
        }
        //검색조건 : 알림유형코드
        if(!alarmTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarm.alarmTypeCode.eq(alarmTypeCode));
        }
        //검색조건 : 알림목적코드
        if(!alarmPurposeCode.equals("ALL")){
            tupleJPQLQuery.where(alarm.alarmPurposeCode.eq(alarmPurposeCode));
        }
        //검색조건 : 대상자코드
        if(!alarmTargetTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarm.alarmTargetTypeCode.eq(alarmTargetTypeCode));
        }

        //검색조건 : 알림목적, 발송시점
        if(keyword != null) {
            tupleJPQLQuery.where(alarm.alarmPurposeName.like("%" + keyword + "%")
                    .or(alarm.alarmSendPointName.like("%" + keyword + "%"))
            );
        }

        //정렬
        tupleJPQLQuery.orderBy(alarm.storeId.desc());

        //페이징
        tupleJPQLQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        long total = tupleJPQLQuery.fetchCount();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public List<Object[]> getAlarmSetList(String storeId) {
        QAlarm alarm = QAlarm.alarm;
        QStore store = QStore.store;

        JPQLQuery<Alarm> query = from(alarm);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarm.alarmNo,  //알림번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                alarm.alarmTpNo,  //알림템플릿번호3
                alarm.alarmTypeCode,    //알림유형코드4
                alarm.alarmUpdatedate,    //5
                alarm.alarmPurposeCode, //알림목적코드6
                alarm.alarmPurposeName, //알림목적명7
                alarm.alarmSendPointCode,   //알림발송시점코드8
                alarm.alarmSendPointName,   //알림발송시점명9
                alarm.alarmTargetTypeCode,  //알림대상자코드10
                alarm.alarmMessage,//알림메시지11
                alarm.alarmSendWayCode, //발송수단코드12
                alarm.alarmMessage,     //알림메시지13
                alarm.alarmVisibleCode,  //알림설정여부코드14
                alarm.alarmMessageUpdateCode    //알림메시지업데이트여부코드15
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(store).on(alarm.storeId.eq(store.storeId));

        //조건문
        tupleJPQLQuery.where(alarm.storeId.eq(storeId));

        //정렬
        tupleJPQLQuery.orderBy(alarm.alarmNo.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });


        return resultList;
    }

    @Override
    public List<Object[]> getAlarmMessage() {
        QAlarmTemplate alarmTemplate = QAlarmTemplate.alarmTemplate;

        JPQLQuery<AlarmTemplate> query = from(alarmTemplate);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmTemplate.alarmTpNo,
                alarmTemplate.alarmTpMessage
        );

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

//    @Override
//    public Object[] getAlarmTemplateDetail(Long alarmTpNo) {
//        QAlarmTemplate alarmTemplate = QAlarmTemplate.alarmTemplate;
//
//        JPQLQuery<AlarmTemplate> query = from(alarmTemplate);
//
//        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
//                alarmTemplate.alarmTpNo,  //알림템플릿번호0
//                alarmTemplate.alarmTypeCode,    //알림유형코드1
//                alarmTemplate.alarmPurposeCode, //알림목적코드3
//                alarmTemplate.alarmPurposeName, //알림목적명4
//                alarmTemplate.alarmSendPointCode,   //알림발송시점코드5
//                alarmTemplate.alarmSendPointName,   //알림발송시점명6
//                alarmTemplate.alarmTargetTypeCode,  //알림대상자코드7
//                alarmTemplate.alarmMessage  //알림메시지8
//        );
//
//        //조건식
//        tupleJPQLQuery.where(alarmTemplate.alarmTpNo.eq(alarmTpNo));
//
//        //한 레코드만 반환
//        Tuple tuple = tupleJPQLQuery.fetchOne();
//
//        return tuple.toArray();
//    }
}
