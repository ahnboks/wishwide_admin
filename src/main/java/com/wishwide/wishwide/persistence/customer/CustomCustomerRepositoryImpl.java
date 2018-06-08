package com.wishwide.wishwide.persistence.customer;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.List;

public class CustomCustomerRepositoryImpl extends QuerydslRepositorySupport implements CustomCustomer {
    public CustomCustomerRepositoryImpl(){
        super(Customer.class);
    }

    @Override
    public Page<Object[]> getCustomerPage(String type,
                                          String keyword,
                                          String searchUserId,
                                          String roleCode,
                                          String sessionId,
                                          String customerBenefitTypeCode,
                                          String customerGradeTypeCode,
                                          Pageable pageable) {
        QCustomer customer = QCustomer.customer;
        QStore store = QStore.store;    //매장
        QCustomerVisitHistory customerVisitHistory = QCustomerVisitHistory.customerVisitHistory;    //고객 방문수
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;    //선물구매수
        QCouponBox useCouponBox = new QCouponBox("use");    //사용한 쿠폰수
        QCouponBox notUsecouponBox = new QCouponBox("notUse");  //미사용한 쿠폰수

        JPQLQuery<Customer> query = from(customer);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                customer.customerNo,    //고객번호0
                customer.customerPhone, //고객전화번호1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                customer.customerGradeTypeCode, //고객등급4
                customerVisitHistory.customerVisitHistoryNo.countDistinct(),    //방문횟수5
                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물구매수6
                customer.customerReceiveGiftCnt,    //받은선물수7
                useCouponBox.couponBoxNo.countDistinct(),   //사용한쿠폰수8
                notUsecouponBox.couponBoxNo.countDistinct(),    //사용안햔쿠폰수9
                customer.customerBenefitTypeCode,   //고객혜택타입10
                customer.customerBenefitValue,  //고객혜택값11
                customer.customerBirth, //고객생일12
                customer.customerRegdate    //고객 가입일시13
        );

        //조인문
        tupleJPQLQuery.leftJoin(store).on(customer.storeId.eq(store.storeId))
                .leftJoin(giftReceiveHistory).on(customer.customerNo.eq(giftReceiveHistory.customerNo))
                .leftJoin(customerVisitHistory).on(customer.customerNo.eq(customerVisitHistory.customerNo))
                .leftJoin(useCouponBox).on(customer.customerNo.eq(useCouponBox.customerNo).and(useCouponBox.couponUseCode.eq(1)))
                .leftJoin(notUsecouponBox).on(customer.customerNo.eq(notUsecouponBox.customerNo));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(customer.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            System.out.println("검색아이디"+searchUserId);
            tupleJPQLQuery.where(customer.storeId.eq(searchUserId));
        }
        //검색조건 : 등급
        if(!customerBenefitTypeCode.equals("ALL")){
            tupleJPQLQuery.where(customer.customerBenefitTypeCode.eq(customerBenefitTypeCode));
        }
        //검색조건 : 혜택
        if(!customerGradeTypeCode.equals("ALL")){
            tupleJPQLQuery.where(customer.customerGradeTypeCode.eq(customerGradeTypeCode));
        }

        //Group By
        tupleJPQLQuery.groupBy(customer.customerNo);

        //정렬;
        tupleJPQLQuery.orderBy(customer.customerRegdate.desc());

        //전체 row count 값
        List<Tuple> countTuple = tupleJPQLQuery.fetch();

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

        //        long total = tupleJPQLQuery.fetchCount();
        long total = countTuple.size();

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public List<Object[]> getStoreCustomerList(String storeId) {
        QCustomer customer = QCustomer.customer;
        QStore store = QStore.store;    //매장
        QCustomerVisitHistory customerVisitHistory = QCustomerVisitHistory.customerVisitHistory;    //고객 방문수
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;    //선물구매수
        QCouponBox useCouponBox = new QCouponBox("use");    //사용한 쿠폰수
        QCouponBox notUsecouponBox = new QCouponBox("notUse");  //미사용한 쿠폰수

        JPQLQuery<Customer> query = from(customer);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                customer.customerNo,    //고객번호0
                customer.customerPhone, //고객전화번호1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                customer.customerGradeTypeCode, //고객등급4
                customerVisitHistory.customerVisitHistoryNo.countDistinct(),    //방문횟수5
                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물구매수6
                customer.customerReceiveGiftCnt,    //받은선물수7
                useCouponBox.couponBoxNo.countDistinct(),   //사용한쿠폰수8
                notUsecouponBox.couponBoxNo.countDistinct(),    //사용안햔쿠폰수9
                customer.customerBenefitTypeCode,   //고객혜택타입10
                customer.customerBenefitValue,  //고객혜택값11
                customer.customerBirth, //고객생일12
                customer.customerRegdate    //고객 가입일시13
        );

        //조인문
        tupleJPQLQuery.join(store).on(customer.storeId.eq(store.storeId))
                .leftJoin(giftReceiveHistory).on(customer.customerNo.eq(giftReceiveHistory.customerNo))
                .leftJoin(customerVisitHistory).on(customer.customerNo.eq(customerVisitHistory.customerNo))
                .leftJoin(useCouponBox).on(customer.customerNo.eq(useCouponBox.customerNo).and(useCouponBox.couponUseCode.eq(1)))
                .leftJoin(notUsecouponBox).on(customer.customerNo.eq(notUsecouponBox.customerNo));

        //조건식

        System.out.println("검색아이디" + storeId);
        tupleJPQLQuery.where(customer.storeId.eq(storeId));

        //Group By
        tupleJPQLQuery.groupBy(customer.customerNo);

        //정렬;
        tupleJPQLQuery.orderBy(customer.customerRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
