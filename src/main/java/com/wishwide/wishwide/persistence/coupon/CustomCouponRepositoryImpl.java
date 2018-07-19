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
        QCouponType couponType = QCouponType.couponType;


        JPQLQuery<Coupon> query = from(coupon);

        //매장명 ^v, 쿠폰유형, 쿠폰명 ^v, 대상자 ^v, 발행방법 ^v, 예약발송시간 ^v, 발행일시 ^v
        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                coupon.couponNo,    //쿠폰번호0
                store.storeId,  //매장아이디1
                store.storeName,    //매장명2
                coupon.couponTypeNo,    //쿠폰유형번호3
                couponType.couponTypeName ,  //쿠폰유형4
                coupon.couponTitle, //쿠폰명5
                coupon.couponTargetTypeCode,    //대상자코드6
                coupon.couponPublishTypeCode,   //발행유형코드7
                coupon.couponReservationTime,   //쿠폰예약발송시간8
                coupon.couponRegDate,   //발행일시9
                coupon.productTitle, //상품명10
                coupon.couponPublishCode    //쿠폰발행코드11
        );

        tupleJPQLQuery
                .join(store).on(coupon.storeId.eq(store.storeId))
                .join(couponType).on(coupon.couponTypeNo.eq(couponType.couponTypeNo));

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(coupon.storeId.eq(sessionId));

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
