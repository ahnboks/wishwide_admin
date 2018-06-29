package com.wishwide.wishwide.persistence.productCategory;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomProductCategoryRepositoryImpl extends QuerydslRepositorySupport implements CustomProductCategory {
    public CustomProductCategoryRepositoryImpl(){
        super(MajorCategory.class);
    }

//    @Override
//    public Page<Object[]> getStorePage(String keyword,
//                                       String searchUserId,
//                                       String roleCode,
//                                       String sessionId,
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
//                storeImage.storeImageExtension, //로고확장자6
//                storeImage.storeImageThumbnailUrl,  //로고썸네일주소7
//                storeImage.storeImageUrl,           //로고주소8
//                device.deviceNo.countDistinct(),    //디바이스수9
//                product.productNo.countDistinct(),  //상품수(판매중인)10
//                customer.customerNo.countDistinct(),    //고객수11
//                giftReceiveHistory.giftReceiveHistoryNo.countDistinct(),    //선물거래수12
//                couponBox.couponBoxNo.countDistinct(),  //쿠폰사용수13
//                store.storeServiceOperationCode //서비스운영코드14
//        );
//
//        //조인문
//        tupleJPQLQuery
//                .leftJoin(storeImage).on(store.storeId.eq(storeImage.storeId).and(storeImage.storeImageTypeCode.eq("LOGO")))
//                .leftJoin(device).on(store.storeId.eq(device.storeId))
//                .leftJoin(product).on(store.storeId.eq(product.productOwnerId).and(product.productSellStatusCode.eq(1)).and(product.productOwnerRole.eq("ST")))
//                .leftJoin(customer).on(store.storeId.eq(customer.storeId).and(customer.customerVisibleCode.eq(1)))
//                .leftJoin(giftReceiveHistory).on(store.storeId.eq(giftReceiveHistory.storeId))
//                .leftJoin(couponBox).on(store.storeId.eq(couponBox.storeId).and(couponBox.couponUseCode.eq(1)));
//
//        //조건식
//
//        //서비스운영타입 코드에 따른 결과
//        if(!serviceOperationCode.equals("ALL"))
//            tupleJPQLQuery.where(store.storeServiceOperationCode.eq(serviceOperationCode));
//
//        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
//        if(roleCode.equals("ST"))
//            tupleJPQLQuery.where(store.storeId.eq(sessionId));
//
//        //검색조건 : 가맹점명
//       if(!searchUserId.equals("ALL")){
//           tupleJPQLQuery.where(store.storeId.eq(searchUserId));
//       }
//
//       if(keyword != null){
//           tupleJPQLQuery.where(store.storeName.like("%"+keyword+"%"));
//       }
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
//    public Object[] getStoreDetail(String storeId) {
//        QStore store = QStore.store;
//        QStoreImage storeImage = QStoreImage.storeImage;    //로고
//        QStoreFile bziFile = new QStoreFile("bzi"); //사업자등록증
//        QStoreFile contractFile = new QStoreFile("contract");   //계약서
//
//        JPQLQuery<Store> query = from(store);
//
//        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
//                store.storeId,  //매장아이디0
//                store.storeEmail,   //매장이메일1
//                store.storePhone,   //매장전화번호2
//                store.storeZipCode, //우편번호3
//                store.storeAddress, //주소4
//                store.storeAddressDetail,   //상세주소5
//                store.brandName,    //브랜드명6
//                store.storeName,    //가맹점명7
//                store.storeIntroduction,    //매장소개8
//                store.storeBusinessRegistrationNumber, //사업자번호9
//                store.storePresidentName,   //대표자명10
//                store.storeOperatorName,    //운영자명11
//                store.storeOperatorPhone,   //운영자 전화번호12
//                store.storeOperatorEmail,   //운영자 이메일13
//                store.storeOpeningHour, //개점시간14
//                store.storeClosingHour, //폐점시간15
//                store.storeKakaoYellowId,   //옐로우아이디16
//                store.storeContractStatusCode,  //계약상태코드17
//                store.storeContractBegindate,   //계약시작일18
//                store.storeContractFinishdate,  //계약종료일19
//                store.storeBenefitTypeCode, //매장혜택타입코드20
//                store.storeStampGoal,   //쿠폰발급 도장기준21
//                store.storeStampVipGoal,    //단골등업 도장기준22
//                store.storePointUseGoal,    //포인트 사용기준23
//                store.storePointVipGoal,    //단골등업 포인트 기준24
//                store.storeArGameUseCode,   //AR게임 사용여부코드25
//                store.storeArGameTypeCode,      //AR게임 타입코드26
//                store.storeArGameTypeName,  //AR게임 타입명27
//                store.storeArGameWord,  //AR게임 입력단어28
//                store.storeMemo,    //매장 메모29
//                bziFile.storeFileNo,    //사업자등록증 번호30
//                bziFile.storeFileName,  //사업자등록증 파일명31
//                bziFile.storeDbFileName,    //사업자등록증 DB파일명32
//                bziFile.storeFileExtension, //사업자등록증 파일 확장자33
//                bziFile.storeFileUrl,   //사업자등록증 파일 주소34
//                contractFile.storeFileNo,   //계약서 파일 번호35
//                contractFile.storeFileName, //계약서 파일명36
//                contractFile.storeDbFileName,   //계약서 DB 파일명37
//                contractFile.storeFileExtension,    //계약서 확장자38
//                contractFile.storeFileUrl,  //계약서 파일주소39
//                storeImage.storeImageNo,    //로고 파일 번호40
//                storeImage.storeImageName,  //로고 파일명41
//                storeImage.storeImageDbName,    //로고 DB파일명42
//                storeImage.storeImageExtension, //로고 확장자43
//                storeImage.storeImageUrl,   //로고 파일 주소44
//                storeImage.storeImageThumbnailUrl   //로고 썸네일 파일 주소45
//        );
//
//        //조인문
//        tupleJPQLQuery
//                .leftJoin(storeImage).on(store.storeId.eq(storeImage.storeId).and(storeImage.storeImageTypeCode.eq("LOGO")))
//                .leftJoin(bziFile).on(store.storeId.eq(bziFile.storeId).and(bziFile.storeFileTypeCode.eq("BSN")))
//                .leftJoin(contractFile).on(store.storeId.eq(contractFile.storeId).and(contractFile.storeFileTypeCode.eq("CONT")));
//
//        //조건식
//        tupleJPQLQuery.where(store.storeId.eq(storeId));
//
//        //한 레코드만 반환
//        Tuple tuple = tupleJPQLQuery.fetchOne();
//
//        return tuple.toArray();
//    }


    @Override
    public List<Object[]> getMajorCategoryList(String storeId) {
        QMajorCategory majorCategory = QMajorCategory.majorCategory;

        JPQLQuery<MajorCategory> query = from(majorCategory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                majorCategory.majorCategoryNo,  //대분류번호0
                majorCategory.storeId,  //매장아이디1
                majorCategory.majorCategoryTitle,   //대분류명2
                majorCategory.subCategoryUseCode    //중분류여부코드3
        );

        //조건문

        tupleJPQLQuery.where(majorCategory.storeId.eq(storeId).and(majorCategory.majorCategoryVisibleCode.eq(1)));

        //정렬
        tupleJPQLQuery.orderBy(majorCategory.majorCategoryTitle.asc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }

    @Override
    public List<Object[]> getSubCategoryList(Long majorCategoryNo) {
        QSubCategory subCategory = QSubCategory.subCategory;

        JPQLQuery<SubCategory> query = from(subCategory);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                subCategory.subCategoryNo,  //중분류번호0
                subCategory.storeId,  //매장아이디1
                subCategory.subCategoryTitle   //중분류명2
        );

        //조건문

        tupleJPQLQuery.where(subCategory.majorCategoryNo.eq(majorCategoryNo).and(subCategory.subCategoryVisibleCode.eq(1)));

        //정렬
        tupleJPQLQuery.orderBy(subCategory.subCategoryTitle.asc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
