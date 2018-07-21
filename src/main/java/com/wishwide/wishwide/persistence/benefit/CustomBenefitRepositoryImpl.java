package com.wishwide.wishwide.persistence.benefit;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomBenefitRepositoryImpl extends QuerydslRepositorySupport implements CustomBenefit {
    public CustomBenefitRepositoryImpl(){
        super(MembershipCustomer.class);
    }

    @Override
    public Page<Object[]> getBenefitPage(String type,
                                         String keyword,
                                         String searchUserId,
                                         String roleCode,
                                         String sessionId,
                                         Pageable pageable) {
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QStore store = QStore.store;

        JPQLQuery<MembershipCustomer> query = from(customer);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                customer.membershipCustomerNo,    //멤버쉽고객번호0
                store.storeId,  //매장아이디1
                store.storeName,        //매장명2
                customer.customerPhone, //고객전화번호3
                customer.customerName,  //고객명4
                customer.customerBenefitTypeCode,   //고객혜택타입5
                customer.customerBenefitValue,  //고객혜택값6
                customer.membershipCustomerRegdate    //고객 가입일시7
        );

        //조인문
        tupleJPQLQuery.leftJoin(store).on(customer.storeId.eq(store.storeId));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(customer.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            System.out.println("검색아이디"+searchUserId);
            tupleJPQLQuery.where(customer.storeId.eq(searchUserId));
        }

        //검색조건 : 이름, 전화번호
        if(type != null) {
            switch (type.trim()) {
                case "ALL" :
                    tupleJPQLQuery.where(customer.customerPhone.like("%" + keyword + "%")
                            .or(customer.customerName.like("%" + keyword + "%"))
                    );
                    break;
                case "customerPhone" :
                    tupleJPQLQuery.where(customer.customerPhone.like("%" + keyword + "%"));
                    break;
                case "customerName" :
                    tupleJPQLQuery.where(customer.customerName.like("%" + keyword + "%"));
                    break;
            }
        }

        //정렬;
        tupleJPQLQuery.orderBy(customer.membershipCustomerRegdate.desc());

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
    public List<Object[]> getStoreCustomerList(String storeId) {
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QStore store = QStore.store;    //매장
        QCustomerVisitHistory customerVisitHistory = QCustomerVisitHistory.customerVisitHistory;    //고객 방문수
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;    //선물구매수
        QCouponBox useCouponBox = new QCouponBox("use");    //사용한 쿠폰수
        QCouponBox notUsecouponBox = new QCouponBox("notUse");  //미사용한 쿠폰수

        JPQLQuery<MembershipCustomer> query = from(customer);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                customer.membershipCustomerNo,    //멤버쉽고객번호0
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
                customer.membershipCustomerRegdate    //고객 가입일시13
        );

        //조인문
        tupleJPQLQuery.join(store).on(customer.storeId.eq(store.storeId))
                .leftJoin(giftReceiveHistory).on(customer.membershipCustomerNo.eq(giftReceiveHistory.membershipCustomerNo))
                .leftJoin(customerVisitHistory).on(customer.membershipCustomerNo.eq(customerVisitHistory.membershipCustomerNo))
                .leftJoin(useCouponBox).on(customer.membershipCustomerNo.eq(useCouponBox.membershipCustomerNo).and(useCouponBox.couponUseCode.eq(1)))
                .leftJoin(notUsecouponBox).on(customer.membershipCustomerNo.eq(notUsecouponBox.membershipCustomerNo));

        //조건식

        System.out.println("검색아이디" + storeId);
        tupleJPQLQuery.where(customer.storeId.eq(storeId));

        //Group By
        tupleJPQLQuery.groupBy(customer.membershipCustomerNo);

        //정렬;
        tupleJPQLQuery.orderBy(customer.membershipCustomerRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getStampHistoryList(Long membershipCustomerNo, String storeId) {
        QStampHistory stampHistory = QStampHistory.stampHistory;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<StampHistory> query = from(stampHistory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                stampHistory.stampHistoryNo,    //도장내역번호0
                customer.membershipCustomerNo,    //멤버쉽고객번호1
                customer.customerPhone, //고객전화번호2
                customer.customerName,  //고객명3
                stampHistory.stampSavingCnt,    //적립도장4
                stampHistory.stampDeductCnt,    //차감도장5
                stampHistory.stampCnt,  //누적도장6
                stampHistory.stampNowCnt,   //현재도장7
                stampHistory.stampCouponPublishedCnt,   //쿠폰발급횟수8
                stampHistory.stampHistoryRegdate,    //일시9
                stampHistory.stampEarningWay    //도장적립경로10
        );

        //조인문
        tupleJPQLQuery.join(customer).on(stampHistory.membershipCustomerNo.eq(customer.membershipCustomerNo));

        //조건식
        tupleJPQLQuery.where(stampHistory.membershipCustomerNo.eq(membershipCustomerNo).and(stampHistory.storeId.eq(storeId)));

        //정렬;
        tupleJPQLQuery.orderBy(stampHistory.stampHistoryRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getPointHistoryList(Long membershipCustomerNo, String storeId) {
        QPointHistory pointHistory = QPointHistory.pointHistory;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<PointHistory> query = from(pointHistory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                pointHistory.pointHistoryNo,    //포인트내역번호0
                customer.membershipCustomerNo,    //멤버쉽고객번호1
                customer.customerPhone, //고객전화번호2
                customer.customerName,  //고객명3
                pointHistory.pointSavingCnt,    //적립포인트4
                pointHistory.pointDeductCnt,    //차감포인트5
                pointHistory.pointExtinctionCnt,    //소멸포인트6
                pointHistory.pointNowCnt,   //현재포인트7
                pointHistory.pointEarningWay,   //적립경로8
                pointHistory.pointHistoryRegdate,    //일시9
                pointHistory.pointCnt   //누적포인트10
        );

        //조인문
        tupleJPQLQuery.join(customer).on(pointHistory.membershipCustomerNo.eq(customer.membershipCustomerNo));

        //조건식
        tupleJPQLQuery.where(pointHistory.membershipCustomerNo.eq(membershipCustomerNo).and(pointHistory.storeId.eq(storeId)));

        //정렬;
        tupleJPQLQuery.orderBy(pointHistory.pointHistoryRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
