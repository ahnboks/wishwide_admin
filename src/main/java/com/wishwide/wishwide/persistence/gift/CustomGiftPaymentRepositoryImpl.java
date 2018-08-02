package com.wishwide.wishwide.persistence.gift;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomGiftPaymentRepositoryImpl extends QuerydslRepositorySupport implements CustomGiftPayment {
    public CustomGiftPaymentRepositoryImpl() {
        super(GiftPayment.class);
    }

    @Override
    public Page<Object[]> getGiftPaymentPage(String type,
                                             String keyword,
                                             String searchUserId,
                                             String roleCode,
                                             String sessionId,
                                             String giftPaymentStatusCode,
                                             Pageable pageable) {
        QGiftPayment giftPayment = QGiftPayment.giftPayment;
        QStore store = QStore.store;

        JPQLQuery<GiftPayment> query = from(giftPayment);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                giftPayment.giftPaymentNo,  //선물결제번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                giftPayment.gpGiftSenderPhone,    //구매자 전화번호3
                giftPayment.gpGiftSenderName, //구매자명4
                giftPayment.gpGiftReceiverPhone,  //수신자 전화번호5
                giftPayment.gpGiftReceiverName,   //수신자명6
                giftPayment.gpProductTitle,   //상품명7
                giftPayment.giftQuantity,   //선물수량8
                giftPayment.giftPaymentPrice,   //선물결제가격9
                giftPayment.giftPaymentStatusCode,  //선물결제상태10
                giftPayment.giftPaymentRegdate  //선물결제일11
        );

        //조인문
        tupleJPQLQuery.join(store).on(giftPayment.storeId.eq(store.storeId));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if (roleCode.equals("ST"))
            tupleJPQLQuery.where(giftPayment.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if (!searchUserId.equals("ALL")) {
            System.out.println("검색아이디" + searchUserId);
            tupleJPQLQuery.where(giftPayment.storeId.eq(searchUserId));
        }

        //검색조건 : 선물결제상태코드
        if (!giftPaymentStatusCode.equals("ALL")) {
            tupleJPQLQuery.where(giftPayment.giftPaymentStatusCode.eq(giftPaymentStatusCode));
        }

        //검색조건 : 구매자번호, 구매자명, 수신자번호, 수신자명, 상품명, 수량, 결제금액
        if (type != null) {
            switch (type.trim()) {
                case "ALL":
                    tupleJPQLQuery.where(giftPayment.gpGiftSenderPhone.like("%" + keyword + "%")
                            .or(giftPayment.gpGiftSenderName.like("%" + keyword + "%"))
                            .or(giftPayment.gpGiftReceiverPhone.like("%" + keyword + "%"))
                            .or(giftPayment.gpGiftReceiverName.like("%" + keyword + "%"))
                            .or(giftPayment.gpProductTitle.like("%" + keyword + "%"))
                            .or(giftPayment.giftQuantity.like("%" + keyword + "%"))
                            .or(giftPayment.giftPaymentPrice.like("%" + keyword + "%"))
                    );
                    break;
                case "gpGiftSenderPhone":
                    tupleJPQLQuery.where(giftPayment.gpGiftSenderPhone.like("%" + keyword + "%"));
                    break;
                case "gpGiftSenderName":
                    tupleJPQLQuery.where(giftPayment.gpGiftSenderName.like("%" + keyword + "%"));
                    break;
                case "gpGiftReceiverPhone":
                    tupleJPQLQuery.where(giftPayment.gpGiftReceiverPhone.like("%" + keyword + "%"));
                    break;
                case "gpGiftReceiverName":
                    tupleJPQLQuery.where(giftPayment.gpGiftReceiverName.like("%" + keyword + "%"));
                    break;
                case "gpProductTitle":
                    tupleJPQLQuery.where(giftPayment.gpProductTitle.like("%" + keyword + "%"));
                    break;
                case "giftQuantity":
                    tupleJPQLQuery.where(giftPayment.giftQuantity.like("%" + keyword + "%"));
                    break;
                case "giftPaymentPrice":
                    tupleJPQLQuery.where(giftPayment.giftPaymentPrice.like("%" + keyword + "%"));
                    break;
            }
        }

        //정렬;
        tupleJPQLQuery.orderBy(giftPayment.giftPaymentRegdate.desc());

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
    public List<Object[]> getGiftPaymentList(String storeId) {
        QGiftPayment giftPayment = QGiftPayment.giftPayment;
        QStore store = QStore.store;

        JPQLQuery<GiftPayment> query = from(giftPayment);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                giftPayment.giftPaymentNo,  //선물결제번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                giftPayment.gpGiftSenderPhone,    //구매자 전화번호3
                giftPayment.gpGiftSenderName, //구매자명4
                giftPayment.gpGiftReceiverPhone,  //수신자 전화번호5
                giftPayment.gpGiftReceiverName,   //수신자명6
                giftPayment.gpProductTitle,   //상품명7
                giftPayment.giftQuantity,   //선물수량8
                giftPayment.giftPaymentPrice,   //선물결제가격9
                giftPayment.giftPaymentStatusCode,  //선물결제상태10
                giftPayment.giftPaymentRegdate  //선물결제일11
        );

        //조인문
        tupleJPQLQuery.join(store).on(giftPayment.storeId.eq(store.storeId));

        //조건식
        System.out.println("검색아이디" + storeId);
        tupleJPQLQuery.where(giftPayment.storeId.eq(storeId));

        //정렬;
        tupleJPQLQuery.orderBy(giftPayment.giftPaymentRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getCustomerGiftPaymentList(Long customerNo) {
        QGiftPayment giftPayment = QGiftPayment.giftPayment;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<GiftPayment> query = from(giftPayment);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                giftPayment.giftPaymentNo,  //선물결제번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                giftPayment.gpGiftSenderPhone,    //구매자 전화번호3
                giftPayment.gpGiftSenderName, //구매자명4
                giftPayment.gpGiftReceiverPhone,  //수신자 전화번호5
                giftPayment.gpGiftReceiverName,   //수신자명6
                giftPayment.gpProductTitle,   //상품명7
                giftPayment.giftQuantity,   //선물수량8
                giftPayment.giftPaymentPrice,   //선물결제가격9
                giftPayment.giftPaymentStatusCode,  //선물결제상태10
                giftPayment.giftPaymentRegdate  //선물결제일11
        );

        //조인문
        tupleJPQLQuery.join(store).on(giftPayment.storeId.eq(store.storeId))
                .join(customer).on(giftPayment.membershipCustomerNo.eq(customer.membershipCustomerNo));

        //조건식
        tupleJPQLQuery.where(customer.membershipCustomerNo.eq(customerNo)
                .and(customer.storeId.eq(giftPayment.storeId)));

        //정렬;
        tupleJPQLQuery.orderBy(giftPayment.giftPaymentRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
