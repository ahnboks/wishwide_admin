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

public class CustomCouponRepositoryImpl extends QuerydslRepositorySupport implements CustomCoupon {
    public CustomCouponRepositoryImpl(){
        super(Coupon.class);
    }

    @Override
    public Page<Object[]> getCouponPage(String type,
                                        String keyword,
                                        String searchUserId,
                                        String roleCode,
                                        String sessionId,
                                        String couponTypeCode,
                                        String couponTargetTypeCode,
                                        String couponPublishTypeCode,
                                        Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QStore store = QStore.store;

        JPQLQuery<Coupon> query = from(coupon);

        //매장명 ^v, 쿠폰유형, 쿠폰명 ^v, 대상자 ^v, 발행방법 ^v, 예약발송시간 ^v, 발행일시 ^v
        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                coupon.couponNo,    //쿠폰번호0
                store.storeId,  //매장아이디1
                store.storeName,    //매장명2
                coupon.couponTitle, //쿠폰명3
                coupon.couponTargetTypeCode,    //대상자코드4
                coupon.couponPublishTypeCode,   //발급경로코드5
                coupon.couponReservationTime,   //쿠폰예약발송시간6
                coupon.couponRegDate,   //발행일시7
                coupon.productTitle, //상품명8
                coupon.couponPublishCode    //쿠폰발행코드9
        );

        tupleJPQLQuery
                .join(store).on(coupon.storeId.eq(store.storeId));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(coupon.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(store.storeId.eq(searchUserId));
        }
        //검색조건 : 쿠폰발급대상코드
        if(!couponTargetTypeCode.equals("ALL")){
            tupleJPQLQuery.where(coupon.couponTargetTypeCode.eq(couponTargetTypeCode));
        }
        //검색조건 : 쿠폰발급경로코드
        if(!couponPublishTypeCode.equals("ALL")){
            tupleJPQLQuery.where(coupon.couponPublishTypeCode.eq(couponPublishTypeCode));
        }

        //검색조건 : 가맹점명, 쿠폰명
        if(type != null) {
            switch (type.trim()) {
                case "ALL" :
                    tupleJPQLQuery.where(store.storeName.like("%" + keyword + "%")
                                    .or(coupon.couponTitle.like("%" + keyword + "%")));
                    break;
                case "storeName" :
                    tupleJPQLQuery.where(store.storeName.like("%" + keyword + "%"));
                    break;
                case "couponTitle" :
                    tupleJPQLQuery.where(store.storeName.like("%" + keyword + "%"));
                    break;
            }
        }

        //정렬;
        tupleJPQLQuery.orderBy(coupon.couponRegDate.desc());

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
    public Object[] getCouponDetail(Long couponNo) {
        QCoupon coupon = QCoupon.coupon;
        QStore store = QStore.store;

        JPQLQuery<Coupon> query = from(coupon);

        //매장명 ^v, 쿠폰유형, 쿠폰명 ^v, 대상자 ^v, 발행방법 ^v, 예약발송시간 ^v, 발행일시 ^v
        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                coupon.couponNo,    //쿠폰번호0
                store.storeId,  //매장아이디1
                store.storeName,    //매장명2
                coupon.couponTitle, //쿠폰명3
                coupon.couponTargetTypeCode,    //대상자코드4
                coupon.couponPublishTypeCode,   //발행타입코드5
                coupon.couponReservationTime,   //쿠폰예약발송시간6
                coupon.couponRegDate,   //발행일시7
                coupon.productTitle, //상품명8
                coupon.couponPublishCode,    //쿠폰발송코드9
                coupon.couponDiscountTypeCode,  //쿠폰할인타입코드10
                coupon.couponDiscountValue, //쿠폰할인값11
                coupon.couponReservationTime,   //쿠폰예약시간12
                coupon.couponFinishdate, //쿠폰만료일13
                store.storeBenefitTypeCode  //매장혜택타입코드14
        );

        tupleJPQLQuery
                .join(store).on(coupon.storeId.eq(store.storeId));

        //조건식
        tupleJPQLQuery.where(coupon.couponNo.eq(couponNo));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();

    }

//    @Override
//    public List<Object[]> getCouponList(String wideManagerId) {
//        QCoupon coupon = QCoupon.coupon;
//
//        JPQLQuery<Coupon> query = from(coupon);
//
//        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
//               coupon.couponNo,
//                coupon.couponTitle,
//                coupon.wideManagerId
//        );
//
//
//        //조건식
//        tupleJPQLQuery.where(coupon.couponNo.gt(0L))
//                .where(coupon.wideManagerId.eq(wideManagerId));
//
//        //정렬
//        tupleJPQLQuery.orderBy(coupon.couponTitle.desc());
//
//        //패치
//        List<Tuple> tuples = tupleJPQLQuery.fetch();
//
//        List<Object[]> resultList = new ArrayList<>();
//
//        tuples.forEach(tuple -> {
//            resultList.add(tuple.toArray());
//        });
//
//        return resultList;
//    }
}
