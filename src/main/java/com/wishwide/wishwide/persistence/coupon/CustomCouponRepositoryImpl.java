package com.wishwide.wishwide.persistence.coupon;

import com.wishwide.wishwide.domain.Coupon;
import com.wishwide.wishwide.domain.Customer;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class CustomCouponRepositoryImpl extends QuerydslRepositorySupport implements CustomCoupon {
    public CustomCouponRepositoryImpl(){
        super(Coupon.class);
    }
//
//    @Override
//    public Page<Object[]> getStorePage(String type,
//                                       String keyword,
//                                       String roleCode,
//                                       String storeId,
//                                       String serviceOperationCode,
//                                       Pageable pageable) {
//        QStore store = QStore.store;
//        QStoreImage storeImage = QStoreImage.storeImage;
//        QDevice device = QDevice.device;
//        QProduct product = QProduct.product;
//        QCustomer customer = QCustomer.customer;
//        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;
//        QCouponBox couponBox = QCouponBox.couponBox;
//
//        JPQLQuery<Store> query = from(store);
//
//        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
//                store.storeId,  //매장아이디0
//                store.brandName,    //브랜드명1
//                store.storeName,    //가맹점명2
//                storeImage.storeImageNo,    //로고번호3
//                storeImage.storeImageName,  //로고이미지명4
//                storeImage.storeImageDbName,    //로고DB명5
//                storeImage.storeImageExtension, //로고확장자
//                storeImage.storeImageThumbnailUrl,  //로고썸네일주소
//                storeImage.storeImageUrl,           //로고주소
//                device.deviceNo.countDistinct(),    //디바이스수
//                product.productNo.countDistinct(),  //상품수(판매중인)
//                customer.customerNo.countDistinct(),    //고객수
//                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물거래수
//                couponBox.couponBoxNo.countDistinct(),  //쿠폰사용수
//                store.storeServiceOperationCode //서비스운영코드
//        );
//
//        tupleJPQLQuery
//                .leftJoin(storeImage).on(store.storeId.eq(storeImage.storeId).and(storeImage.storeImageTypeCode.eq("LOGO")))
//                .leftJoin(device).on(store.storeId.eq(device.storeId))
//                .leftJoin(product).on(store.storeId.eq(product.productOwnerId).and(product.productSellStatusCode.eq(1)))
//                .leftJoin(customer).on(store.storeId.eq(customer.storeId).and(customer.customerVisibleCode.eq(1)))
//                .leftJoin(giftReceiveHistory).on(store.storeId.eq(giftReceiveHistory.storeId))
//                .leftJoin(couponBox).on(store.storeId.eq(couponBox.storeId).and(couponBox.couponUseCode.eq(1)));
//
//        //조건식
//        tupleJPQLQuery.where(store.storeServiceOperationCode.eq(serviceOperationCode));
//
//        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
//        if(roleCode.equals("ST"))
//            tupleJPQLQuery.where(store.storeId.eq(storeId));
//
//        //검색조건 : 가맹점명
//        if(type != null) {
//            switch (type.trim()) {
//                case "storeName" :
//                    tupleJPQLQuery.where(store.storeName.like("%" + keyword + "%"));
//                    break;
//            }
//        }
//
//        //Group By
//        tupleJPQLQuery.groupBy(store.storeId).groupBy(storeImage.storeImageNo);
//
//        //정렬;
//        tupleJPQLQuery.orderBy(store.storeRegdate.desc());
//
//        //전체 row count 값
//        List<Tuple> countTuple = tupleJPQLQuery.fetch();
//
//        //페이징
//        tupleJPQLQuery
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize());
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
////        long total = tupleJPQLQuery.fetchCount();
//        long total = countTuple.size();
//
//        return new PageImpl<>(resultList, pageable, total);
//    }



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
