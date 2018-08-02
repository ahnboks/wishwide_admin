package com.wishwide.wishwide.persistence.coupon;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomCouponBoxRepositoryImpl extends QuerydslRepositorySupport implements CustomCouponBox {
    public CustomCouponBoxRepositoryImpl() {
        super(Device.class);
    }

    @Override
    public Page<Object[]> getCouponBoxPage(String type,
                                           String keyword,
                                           String searchUserId,
                                           String roleCode,
                                           String sessionId,
                                           String couponTypeCode,
                                           String couponTargetTypeCode,
                                           String couponPublishTypeCode,
                                           int couponUseCode,
                                           Pageable pageable) {

        QCouponBox couponBox = QCouponBox.couponBox;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<CouponBox> query = from(couponBox);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                couponBox.couponBoxNo, //쿠폰함 번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                customer.membershipCustomerNo,    //멤버쉽고객번호3
                customer.membershipCustomerPhone, //고객전화번호4
                customer.membershipCustomerName,  //고객명5
                couponBox.cbCouponImageUrl, //쿠폰 url6
                couponBox.cbCouponTitle,  //쿠폰명7
                couponBox.cbCouponTargetTypeCode, //쿠폰발급대상코드8
                couponBox.cbCouponPublishTypeCode,    //쿠폰발급경로코드9
                couponBox.cbCouponUseCode,    //쿠폰사용여부10
                couponBox.cbCouponUsedate,    //쿠폰사용일11
                couponBox.cbCouponBegindate,  //유효시작일12
                couponBox.cbCouponFinishdate, //유효만료일13
                couponBox.couponBoxRegdate,  //쿠폰수신일14
                couponBox.cbCouponPublishCode //쿠폰발급코드15

        );

        //조인문
        tupleJPQLQuery
                .join(customer).on(couponBox.membershipCustomerNo.eq(customer.membershipCustomerNo))
                .join(store).on(couponBox.storeId.eq(store.storeId));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if (roleCode.equals("ST"))
            tupleJPQLQuery.where(couponBox.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if (!searchUserId.equals("ALL")) {
            tupleJPQLQuery.where(store.storeId.eq(searchUserId));
        }
        //검색조건 : 쿠폰발급대상코드
        if (!couponTargetTypeCode.equals("ALL")) {
            tupleJPQLQuery.where(couponBox.cbCouponTargetTypeCode.eq(couponTargetTypeCode));
        }
        //검색조건 : 쿠폰발급경로코드
        if (!couponPublishTypeCode.equals("ALL")) {
            tupleJPQLQuery.where(couponBox.cbCouponPublishTypeCode.eq(couponPublishTypeCode));
        }
        //검색조건 : 쿠폰사용여부코드
        if (couponUseCode != 2) {
            tupleJPQLQuery.where(couponBox.cbCouponUseCode.eq(couponUseCode));
        }

        //검색조건 : 전화번호, 이름, 쿠폰명
        if (type != null) {
            switch (type.trim()) {
                case "ALL":
                    tupleJPQLQuery.where(customer.membershipCustomerPhone.like("%" + keyword + "%")
                            .or(customer.membershipCustomerName.like("%" + keyword + "%"))
                            .or(couponBox.cbCouponTitle.like("%" + keyword + "%"))
                    );
                    break;
                case "customerPhone":
                    tupleJPQLQuery.where(customer.membershipCustomerPhone.like("%" + keyword + "%"));
                    break;
                case "customerName":
                    tupleJPQLQuery.where(customer.membershipCustomerName.like("%" + keyword + "%"));
                    break;
                case "couponTitle":
                    tupleJPQLQuery.where(couponBox.cbCouponTitle.like("%" + keyword + "%"));
                    break;
            }
        }
        //정렬;
        tupleJPQLQuery.orderBy(couponBox.couponBoxRegdate.desc());

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
    public List<Object[]> getCouponBoxList(String storeId) {
        QCouponBox couponBox = QCouponBox.couponBox;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<CouponBox> query = from(couponBox);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                couponBox.couponBoxNo, //쿠폰함 번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                customer.membershipCustomerNo,    //멤버쉽고객번호3
                customer.membershipCustomerPhone, //고객전화번호4
                customer.membershipCustomerName,  //고객명5
                couponBox.cbCouponImageUrl, //쿠폰url
                couponBox.cbCouponTitle,  //쿠폰명7
                couponBox.cbCouponTargetTypeCode, //쿠폰발급대상코드8
                couponBox.cbCouponPublishTypeCode,    //쿠폰발급경로코드9
                couponBox.cbCouponUseCode,    //쿠폰사용여부10
                couponBox.cbCouponUsedate,    //쿠폰사용일11
                couponBox.cbCouponBegindate,  //유효시작일12
                couponBox.cbCouponFinishdate, //유효만료일13
                couponBox.couponBoxRegdate  //쿠폰수신일14
        );

        //조인문
        tupleJPQLQuery
                .join(customer).on(couponBox.membershipCustomerNo.eq(customer.membershipCustomerNo))
                .join(store).on(couponBox.storeId.eq(store.storeId));

        //조건식
        //사용된 쿠폰목록만 가져옴
        tupleJPQLQuery.where(couponBox.storeId.eq(storeId))
                .where(couponBox.cbCouponUseCode.eq(1));

        //정렬
        tupleJPQLQuery.orderBy(couponBox.couponBoxRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getCustomerCouponBoxList(Long customerNo) {
        QCouponBox couponBox = QCouponBox.couponBox;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<CouponBox> query = from(couponBox);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                couponBox.couponBoxNo, //쿠폰함 번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                customer.membershipCustomerNo,    //멤버쉽고객번호3
                customer.membershipCustomerPhone, //고객전화번호4
                customer.membershipCustomerName,  //고객명5
                couponBox.cbCouponImageUrl, //쿠폰url
                couponBox.cbCouponTitle,  //쿠폰명7
                couponBox.cbCouponTargetTypeCode, //쿠폰발급대상코드8
                couponBox.cbCouponPublishTypeCode,    //쿠폰발급경로코드9
                couponBox.cbCouponUseCode,    //쿠폰사용여부10
                couponBox.cbCouponUsedate,    //쿠폰사용일11
                couponBox.cbCouponBegindate,  //유효시작일12
                couponBox.cbCouponFinishdate, //유효만료일13
                couponBox.couponBoxRegdate  //쿠폰수신일14
        );

        //조인문
        tupleJPQLQuery
                .join(customer).on(couponBox.membershipCustomerNo.eq(customer.membershipCustomerNo))
                .join(store).on(couponBox.storeId.eq(store.storeId));

        //조건식
        tupleJPQLQuery.where(customer.membershipCustomerNo.eq(customerNo));

        //정렬
        tupleJPQLQuery.orderBy(couponBox.couponBoxRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getCouponHistoryList(Long couponNo) {
        QCouponBox couponBox = QCouponBox.couponBox;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<CouponBox> query = from(couponBox);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                couponBox.couponBoxNo, //쿠폰함 번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                customer.membershipCustomerNo,    //멤버쉽고객번호3
                customer.membershipCustomerPhone, //고객전화번호4
                customer.membershipCustomerName,  //고객명5
                couponBox.cbCouponImageUrl, //쿠폰url6
                couponBox.cbCouponTitle,  //쿠폰명7
                couponBox.cbCouponTargetTypeCode, //쿠폰발급대상코드8
                couponBox.cbCouponPublishTypeCode,    //쿠폰발급경로코드9
                couponBox.cbCouponUseCode,    //쿠폰사용여부10
                couponBox.cbCouponUsedate,    //쿠폰사용일11
                couponBox.cbCouponBegindate,  //유효시작일12
                couponBox.cbCouponFinishdate, //유효만료일13
                couponBox.couponBoxRegdate  //쿠폰수신일14
        );

        //조인문
        tupleJPQLQuery
                .join(customer).on(couponBox.membershipCustomerNo.eq(customer.membershipCustomerNo))
                .join(store).on(couponBox.storeId.eq(store.storeId));

        //조건식
        tupleJPQLQuery.where(couponBox.couponNo.eq(couponNo));

        //정렬
        tupleJPQLQuery.orderBy(couponBox.couponBoxRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
