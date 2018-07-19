package com.wishwide.wishwide.persistence.customer;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomCustomerRepositoryImpl extends QuerydslRepositorySupport implements CustomCustomer {
    public CustomCustomerRepositoryImpl(){
        super(MembershipCustomer.class);
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
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QStore store = QStore.store;    //매장
        QCustomerVisitHistory customerVisitHistory = QCustomerVisitHistory.customerVisitHistory;    //고객 방문수
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;    //선물구매수
        QGiftBox useGiftBox = new QGiftBox("useGift");  //사용한선물
        QGiftBox notUseGiftBox = new QGiftBox("notUseGift");    //받은선물
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
                customer.membershipCustomerRegdate,    //고객 가입일시13
                useGiftBox.giftBoxNo.countDistinct(),  //사용한선물수14
                notUseGiftBox.giftBoxNo.countDistinct()    //사용안한선물수15
        );

        //조인문
        tupleJPQLQuery.leftJoin(store).on(customer.storeId.eq(store.storeId))
                .leftJoin(giftReceiveHistory).on(customer.membershipCustomerNo.eq(giftReceiveHistory.membershipCustomerNo))
                .leftJoin(customerVisitHistory).on(customer.membershipCustomerNo.eq(customerVisitHistory.membershipCustomerNo))
                .leftJoin(useGiftBox).on(customer.customerPhone.eq(useGiftBox.giftReceiverPhone).and(useGiftBox.giftUseCode.eq(1)).and(useGiftBox.storeId.eq(customer.storeId)))
                .leftJoin(notUseGiftBox).on(customer.customerPhone.eq(notUseGiftBox.giftReceiverPhone).and(useGiftBox.storeId.eq(customer.storeId)))
                .leftJoin(useCouponBox).on(customer.membershipCustomerNo.eq(useCouponBox.membershipCustomerNo).and(useCouponBox.couponUseCode.eq(1)))
                .leftJoin(notUsecouponBox).on(customer.membershipCustomerNo.eq(notUsecouponBox.membershipCustomerNo));

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
        tupleJPQLQuery.groupBy(customer.membershipCustomerNo);

        //정렬;
        tupleJPQLQuery.orderBy(customer.membershipCustomerRegdate.desc());

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
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QStore store = QStore.store;    //매장
        QCustomerVisitHistory customerVisitHistory = QCustomerVisitHistory.customerVisitHistory;    //고객 방문수
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;    //선물구매수
        QGiftBox useGiftBox = new QGiftBox("useGift");  //사용한선물
        QGiftBox notUseGiftBox = new QGiftBox("notUseGift");    //받은선물
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
                customer.membershipCustomerRegdate,    //고객 가입일시13
                useGiftBox.giftBoxNo.countDistinct(),  //사용한선물수14
                notUseGiftBox.giftBoxNo.countDistinct()    //사용안한선물수15
        );

        //조인문
        tupleJPQLQuery.join(store).on(customer.storeId.eq(store.storeId))
                .leftJoin(giftReceiveHistory).on(customer.membershipCustomerNo.eq(giftReceiveHistory.membershipCustomerNo))
                .leftJoin(customerVisitHistory).on(customer.membershipCustomerNo.eq(customerVisitHistory.membershipCustomerNo))
                .leftJoin(useGiftBox).on(customer.customerPhone.eq(useGiftBox.giftReceiverPhone).and(useGiftBox.giftUseCode.eq(1)))
                .leftJoin(notUseGiftBox).on(customer.customerPhone.eq(notUseGiftBox.giftReceiverPhone))
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
    public Object[] getCustomerDetail(Long customerNo) {
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<MembershipCustomer> query = from(customer);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                customer.membershipCustomerNo,    //멤버쉽고객번호0
                customer.customerPhone, //고객전화번호1
                customer.customerEmail, //고객이메일2
                customer.customerName,  //고객명3
                customer.customerGenderTypeCode,    //고객성별4
                customer.customerBirth, //고객생일5
                customer.customerGradeTypeCode, //고객등급6
                customer.customerReceiveGiftCnt,    //받은선물수7
                customer.customerBenefitTypeCode,   //고객혜택타입8
                customer.customerBenefitValue,  //고객혜택값9
                customer.membershipCustomerRegdate,    //고객 가입일시10
                customer.customerVisibleCode    //고객활성화여부11
        );

        //조건식
        tupleJPQLQuery.where(customer.membershipCustomerNo.eq(customerNo));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }
}
