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

public class CustomAlarmSendHistoryRepositoryImpl extends QuerydslRepositorySupport implements CustomAlarmSendHistory {
    public CustomAlarmSendHistoryRepositoryImpl(){
        super(AlarmSendHistory.class);
    }

    @Override
    public Page<Object[]> getAlarmSendHistoryPage(String type,
                                                  String keyword,
                                                  String searchUserId,
                                                  String roleCode,
                                                  String sessionId,
                                                  String alarmTypeCode,
                                                  String alarmPurposeCode,
                                                  String alarmTargetTypeCode,
                                                  Pageable pageable) {
        QAlarmSendHistory alarmSendHistory = QAlarmSendHistory.alarmSendHistory;
        QStore store = QStore.store;
        QCustomer customer = QCustomer.customer;

        JPQLQuery<AlarmSendHistory> query = from(alarmSendHistory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmSendHistory.alarmSendHistoryNo,  //알림발송내역번호0
                alarmSendHistory.alarmNo,  //알림번호1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                alarmSendHistory.alarmTemplateNo,  //알림템플릿번호4
                alarmSendHistory.alarmTypeCode,    //알림유형코드5
                alarmSendHistory.alarmMessageUpdateCode,    //6
                alarmSendHistory.alarmPurposeCode, //알림목적코드7
                alarmSendHistory.alarmPurposeName, //알림목적명8
                alarmSendHistory.alarmSendPointCode,   //알림발송시점코드9
                alarmSendHistory.alarmSendPointName,   //알림발송시점명10
                alarmSendHistory.alarmTargetTypeCode,  //알림대상자코드11
                alarmSendHistory.alarmMessage,//알림메시지12
                alarmSendHistory.alarmSendWayCode, //발송수단코드13
                alarmSendHistory.alarmMessage,     //알림메시지14
                alarmSendHistory.alarmMessageUpdateCode,    //알림메시지업데이트여부코드15
                alarmSendHistory.alarmSendHistoryRegdate,    //알림발송일시 16
                alarmSendHistory.alarmSendTypeCode, //알림발송유형코드17
                customer.customerNo,    //고객번호18
                customer.customerPhone,  //고객전화번호19
                customer.customerName   //고객명20
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(store).on(alarmSendHistory.storeId.eq(store.storeId))
                .leftJoin(customer).on(alarmSendHistory.customerNo.eq(customer.customerNo));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.storeId.eq(searchUserId));
        }
        //검색조건 : 알림유형코드
        if(!alarmTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.alarmTypeCode.eq(alarmTypeCode));
        }
        //검색조건 : 알림목적코드
        if(!alarmPurposeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.alarmPurposeCode.eq(alarmPurposeCode));
        }
        //검색조건 : 대상자코드
        if(!alarmTargetTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.alarmTargetTypeCode.eq(alarmTargetTypeCode));
        }

        //검색조건 : 알림목적, 발송시점
        if(keyword != null) {
            tupleJPQLQuery.where(alarmSendHistory.alarmPurposeName.like("%" + keyword + "%")
                    .or(alarmSendHistory.alarmSendPointName.like("%" + keyword + "%"))
            );
        }

        //정렬
        tupleJPQLQuery.orderBy(alarmSendHistory.alarmSendHistoryRegdate.desc());

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

}
