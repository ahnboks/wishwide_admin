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

public class CustomAlarmTemplateRepositoryImpl extends QuerydslRepositorySupport implements CustomAlarmTemplate {
    public CustomAlarmTemplateRepositoryImpl(){
        super(AlarmTemplate.class);
    }

    @Override
    public Page<Object[]> getAlarmTemplatePage(String type,
                                               String keyword,
                                               String alarmTypeCode,
                                               String alarmPurposeCode,
                                               String alarmTargetTypeCode,
                                               Pageable pageable) {
        QAlarmTemplate alarmTemplate = QAlarmTemplate.alarmTemplate;

        JPQLQuery<AlarmTemplate> query = from(alarmTemplate);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmTemplate.alarmTemplateNo,  //알림템플릿번호0
                alarmTemplate.alarmTypeCode,    //알림유형코드1
                alarmTemplate.alarmTemplateUpdatedate,    //2
                alarmTemplate.alarmPurposeCode, //알림목적코드3
                alarmTemplate.alarmPurposeName, //알림목적명4
                alarmTemplate.alarmSendPointCode,   //알림발송시점코드5
                alarmTemplate.alarmSendPointName,   //알림발송시점명6
                alarmTemplate.alarmTargetTypeCode,  //알림대상자코드7
                alarmTemplate.alarmTemplateRegdate,  //알림템플릿등록일8
                alarmTemplate.alarmMessage  //알림메시지9
        );

        //검색조건 : 알림유형코드
        if(!alarmTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmTemplate.alarmTypeCode.eq(alarmTypeCode));
        }
        //검색조건 : 알림목적코드
        if(!alarmPurposeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmTemplate.alarmPurposeCode.eq(alarmPurposeCode));
        }
        //검색조건 : 알림대상자코드
        if(!alarmTargetTypeCode.equals("ALL")){
            tupleJPQLQuery.where(alarmTemplate.alarmTargetTypeCode.eq(alarmTargetTypeCode));
        }

        //검색조건 : 알림목적, 발송시점, 대상자
        if(keyword != null) {
            tupleJPQLQuery.where(alarmTemplate.alarmPurposeName.like("%" + keyword + "%")
                    .or(alarmTemplate.alarmSendPointName.like("%" + keyword + "%"))
            );
        }

        //정렬
        tupleJPQLQuery.orderBy(alarmTemplate.alarmTemplateRegdate.desc());

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
    public List<Object[]> getAlarmVariable() {
        QAlarmVariable alarmVariable = QAlarmVariable.alarmVariable;

        JPQLQuery<AlarmVariable> query = from(alarmVariable);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmVariable.alarmVariableNo,
                alarmVariable.alarmVariableCode,
                alarmVariable.alarmVariableName
        );

        //정렬
        tupleJPQLQuery.orderBy(alarmVariable.alarmVariableName.asc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public Object[] getAlarmTemplateDetail(Long alarmTemplateNo) {
        QAlarmTemplate alarmTemplate = QAlarmTemplate.alarmTemplate;

        JPQLQuery<AlarmTemplate> query = from(alarmTemplate);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                alarmTemplate.alarmTemplateNo,  //알림템플릿번호0
                alarmTemplate.alarmTypeCode,    //알림유형코드1
                alarmTemplate.alarmTemplateRegdate,    //2
                alarmTemplate.alarmPurposeCode, //알림목적코드3
                alarmTemplate.alarmPurposeName, //알림목적명4
                alarmTemplate.alarmSendPointCode,   //알림발송시점코드5
                alarmTemplate.alarmSendPointName,   //알림발송시점명6
                alarmTemplate.alarmTargetTypeCode,  //알림대상자코드7
                alarmTemplate.alarmMessage  //알림메시지8
        );

        //조건식
        tupleJPQLQuery.where(alarmTemplate.alarmTemplateNo.eq(alarmTemplateNo));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }

}
