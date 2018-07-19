package com.wishwide.wishwide.persistence.partner;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomPartnerRepositoryImpl extends QuerydslRepositorySupport implements CustomPartner {
    public CustomPartnerRepositoryImpl(){
        super(Store.class);
    }

    @Override
    public Page<Object[]> getPartnerPage(String keyword,
                                       String searchUserId,
                                       String roleCode,
                                       String sessionId,
                                       Pageable pageable) {
       QPartner partner = QPartner.partner;
       QPartnerStore partnerStore = QPartnerStore.partnerStore;
       QProduct product = QProduct.product;
       QStore store = QStore.store;

        JPQLQuery<Partner> query = from(partner);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                partner.partnerId,  //파트너아이디0
                partner.partnerName,    //파트너명1
                store.storeId.countDistinct(),  //가맹점 개수2
                product.productNo.countDistinct()   //파트너 상품 개수3
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(product).on(partner.partnerId.eq(product.productOwnerId))
                .join(partnerStore).on(partner.partnerId.eq(partnerStore.partnerId))
                .join(store).on(store.storeId.eq(partnerStore.storeId));

        //조건식

        //권한:파트너가 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("PT"))
            tupleJPQLQuery.where(partner.partnerId.eq(sessionId));

        //검색조건 : 파트너명
       if(!searchUserId.equals("ALL")){
           tupleJPQLQuery.where(partner.partnerId.eq(searchUserId));
       }

       if(keyword != null){
           tupleJPQLQuery.where(partner.partnerName.like("%"+keyword+"%"));
       }

        //Group By
        tupleJPQLQuery.groupBy(partner.partnerId);

        //정렬;
        tupleJPQLQuery.orderBy(partner.partnerRegdate.desc());

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
    public List<Object[]> getPartnerNameList() {
        QPartner partner = QPartner.partner;
        QWwRole wwRole = QWwRole.wwRole;

        JPQLQuery<Partner> query = from(partner);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                partner.partnerId,
                partner.partnerName
        );

        //조인문
        tupleJPQLQuery.join(wwRole).on(partner.partnerId.eq(wwRole.userId));

        //조건문
        tupleJPQLQuery.where(wwRole.roleCode.eq("PT"));

       //정렬
        tupleJPQLQuery.orderBy(partner.partnerName.asc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getPartnerStore(String partnerId) {
        QPartner partner = QPartner.partner;
        QPartnerStore partnerStore = QPartnerStore.partnerStore;
        QStore store = QStore.store;
        QStoreImage storeImage = QStoreImage.storeImage;
        QDevice device = QDevice.device;
        QProduct product = QProduct.product;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;
        QCouponBox couponBox = QCouponBox.couponBox;

        JPQLQuery<Store> query = from(store);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                store.storeId,  //매장아이디0
                store.brandName,    //브랜드명1
                store.storeName,    //가맹점명2
                storeImage.storeImageNo,    //로고번호3
                storeImage.storeImageName,  //로고이미지명4
                storeImage.storeImageDbName,    //로고DB명5
                storeImage.storeImageExtension, //로고확장자6
                storeImage.storeImageThumbnailUrl,  //로고썸네일주소7
                storeImage.storeImageUrl,           //로고주소8
                device.deviceNo.countDistinct(),    //디바이스수9
                product.productNo.countDistinct(),  //상품수(판매중인)10
                customer.membershipCustomerNo.countDistinct(),    //고객수11
                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물거래수12
                couponBox.couponBoxNo.countDistinct(),  //쿠폰사용수13
                store.storeServiceOperationCode //서비스운영코드14
        );

        //조인문
        tupleJPQLQuery
                .join(partnerStore).on(store.storeId.eq(partnerStore.storeId))
                .join(partner).on(partner.partnerId.eq(partnerStore.partnerId))
                .leftJoin(storeImage).on(store.storeId.eq(storeImage.storeId).and(storeImage.storeImageTypeCode.eq("LOGO")))
                .leftJoin(device).on(store.storeId.eq(device.storeId))
                .leftJoin(product).on(store.storeId.eq(product.productOwnerId).and(product.productSellStatusCode.eq(1)).and(product.productOwnerRole.eq("ST")))
                .leftJoin(customer).on(store.storeId.eq(customer.storeId).and(customer.customerVisibleCode.eq(1)))
                .leftJoin(giftReceiveHistory).on(store.storeId.eq(giftReceiveHistory.storeId))
                .leftJoin(couponBox).on(store.storeId.eq(couponBox.storeId).and(couponBox.couponUseCode.eq(1)));

        //조건식

        tupleJPQLQuery.where(partner.partnerId.eq(partnerId));

        //정렬
        tupleJPQLQuery.orderBy(partner.partnerName.asc());

        //Group By
        tupleJPQLQuery.groupBy(store.storeId).groupBy(storeImage.storeImageNo);

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }


    @Override
    public List<Object[]> getAllPartnerStore(String partnerId) {
        QPartner partner = QPartner.partner;
        QPartnerStore partnerStore = QPartnerStore.partnerStore;
        QStore store = QStore.store;
        QStoreImage storeImage = QStoreImage.storeImage;
        QDevice device = QDevice.device;
        QProduct product = QProduct.product;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;
        QGiftReceiveHistory giftReceiveHistory = QGiftReceiveHistory.giftReceiveHistory;
        QCouponBox couponBox = QCouponBox.couponBox;

        JPQLQuery<Store> query = from(store);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                store.storeId,  //매장아이디0
                store.brandName,    //브랜드명1
                store.storeName,    //가맹점명2
                storeImage.storeImageNo,    //로고번호3
                storeImage.storeImageName,  //로고이미지명4
                storeImage.storeImageDbName,    //로고DB명5
                storeImage.storeImageExtension, //로고확장자6
                storeImage.storeImageThumbnailUrl,  //로고썸네일주소7
                storeImage.storeImageUrl,           //로고주소8
                device.deviceNo.countDistinct(),    //디바이스수9
                product.productNo.countDistinct(),  //상품수(판매중인)10
                customer.membershipCustomerNo.countDistinct(),    //고객수11
                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물거래수12
                couponBox.couponBoxNo.countDistinct(),  //쿠폰사용수13
                store.storeServiceOperationCode, //서비스운영코드14
                partnerStore.storeId    //매장아이디15  -  매장 아이디 미존재 시 파트너 가맹점이 아님
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(partnerStore).on(store.storeId.eq(partnerStore.storeId).and(partnerStore.partnerId.eq(partnerId)))
                .leftJoin(partner).on(partner.partnerId.eq(partnerStore.partnerId).and(partner.partnerId.eq(partnerId)))
                .leftJoin(storeImage).on(store.storeId.eq(storeImage.storeId).and(storeImage.storeImageTypeCode.eq("LOGO")))
                .leftJoin(device).on(store.storeId.eq(device.storeId))
                .leftJoin(product).on(store.storeId.eq(product.productOwnerId).and(product.productSellStatusCode.eq(1)).and(product.productOwnerRole.eq("ST")))
                .leftJoin(customer).on(store.storeId.eq(customer.storeId).and(customer.customerVisibleCode.eq(1)))
                .leftJoin(giftReceiveHistory).on(store.storeId.eq(giftReceiveHistory.storeId))
                .leftJoin(couponBox).on(store.storeId.eq(couponBox.storeId).and(couponBox.couponUseCode.eq(1)));

        //조건식

        //정렬
        tupleJPQLQuery.orderBy(store.storeName.asc());

        //Group By
        tupleJPQLQuery.groupBy(store.storeId).groupBy(storeImage.storeImageNo);

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public Object[] getPartnerDetail(String partnerId) {
        QPartner partner = QPartner.partner;

        JPQLQuery<Partner> query = from(partner);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                partner.partnerId,  //파트너아이디0
                partner.partnerName,    //파트너명1
                partner.partnerOperatorName,    //담당자명2
                partner.partnerOperatorEmail,   //담당자이메일3
                partner.partnerOperatorPhone    //담당자전화번호4
        );


        //조건식
        tupleJPQLQuery.where(partner.partnerId.eq(partnerId));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }
}
