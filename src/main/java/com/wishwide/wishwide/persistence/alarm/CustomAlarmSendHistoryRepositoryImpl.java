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
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<AlarmSendHistory> query = from(alarmSendHistory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmSendHistory.alarmSendHistoryNo,  //알림발송내역번호0
                alarmSendHistory.alarmNo,  //알림번호1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                alarmSendHistory.alarmTpNo,  //알림템플릿번호4
                alarmSendHistory.ashAlarmTypeCode,    //알림유형코드5
                alarmSendHistory.ashAlarmMessageUpdateCode,    //6
                alarmSendHistory.ashAlarmPurposeCode, //알림목적코드7
                alarmSendHistory.ashAlarmPurposeName, //알림목적명8
                alarmSendHistory.ashAlarmSendPointCode,   //알림발송시점코드9
                alarmSendHistory.ashAlarmSendPointName,   //알림발송시점명10
                alarmSendHistory.ashAlarmTargetTypeCode,  //알림대상자코드11
                alarmSendHistory.ashAlarmMessage,//알림메시지12
                alarmSendHistory.ashAlarmSendWayCode, //발송수단코드13
                alarmSendHistory.ashAlarmMessage,     //알림메시지14
                alarmSendHistory.ashAlarmMessageUpdateCode,    //알림메시지업데이트여부코드15
                alarmSendHistory.alarmSendHistoryRegdate,    //알림발송일시 16
                alarmSendHistory.ashAlarmSendTypeCode, //알림발송유형코드17
                customer.membershipCustomerNo,    //멤버쉽고객번호18
                customer.membershipCustomerPhone,  //고객전화번호19
                customer.membershipCustomerName   //고객명20
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(store).on(alarmSendHistory.storeId.eq(store.storeId))
                .leftJoin(customer).on(alarmSendHistory.membershipCustomerNo.eq(customer.membershipCustomerNo));

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if (roleCode.equals("ST"))
            tupleJPQLQuery.where(alarmSendHistory.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.storeId.eq(searchUserId));
        }
        //검색조건 : 알림유형코드
        if(!alarmTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.ashAlarmTypeCode.eq(alarmTypeCode));
        }
        //검색조건 : 알림목적코드
        if(!alarmPurposeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.ashAlarmPurposeCode.eq(alarmPurposeCode));
        }
        //검색조건 : 대상자코드
        if(!alarmTargetTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmSendHistory.ashAlarmTargetTypeCode.eq(alarmTargetTypeCode));
        }

        //검색조건 : 알림목적, 발송시점
        if(keyword != null) {
            tupleJPQLQuery.where(alarmSendHistory.ashAlarmPurposeName.like("%" + keyword + "%")
                    .or(alarmSendHistory.ashAlarmSendPointName.like("%" + keyword + "%"))
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
