package com.wishwide.wishwide.persistence.product;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomProductRepositoryImpl extends QuerydslRepositorySupport implements CustomProduct {
    public CustomProductRepositoryImpl(){
        super(Product.class);
    }

    @Override
    public Page<Object[]> getStoreProductPage(String type,
                                              String keyword,
                                              String searchUserId,
                                              String roleCode,
                                              String sessionId,
                                              int productSellStatusCode,
                                              int giftProductRegisterCode,
                                              Pageable pageable) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QGiftProduct giftProduct = QGiftProduct.giftProduct;
        QStore store = QStore.store;

        JPQLQuery<Product> query = from(product);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                product.productNo,  //상품번호0
                product.giftProductRegisterCode,    //선물등록여부코드1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                product.majorCategoryTitle, //대분류명4
                product.subCategoryTitle,   //중분류명5
                product.productTitle,   //상품명6
                product.productPrice,   //판매가격7
                giftProduct.giftProductDiscountPrice,   //할인가격8
                product.productSellStatusCode,  //판매상태코드9
                product.productRegdate, //등록일10
                productImage.productImageNo,    //상품이미지번호11
                productImage.productImageName,  //상품이미지명12
                productImage.productImageDbName,    //상품이미지db명13
                productImage.productImageExtension, //상품이미지확장자14
                productImage.productImageThumbnailUrl,  //상품이미지썸네일주소15
                productImage.productImageUrl    //상품이미지주소16
        );

        //조인문
        tupleJPQLQuery
                .join(store).on(product.productOwnerId.eq(store.storeId))
                .leftJoin(giftProduct).on(product.productNo.eq(giftProduct.productNo))
                .leftJoin(productImage).on(product.productNo.eq(productImage.productNo));

        //조건식

        //매장 권한으로 등록한 상품만 가져옴
       tupleJPQLQuery.where(product.productOwnerRole.eq("ST"));

        //권한:매장이나 파트너가 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(product.productOwnerId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(product.productOwnerId.eq(searchUserId));
        }
        //검색조건 : 판매코드
        if(productSellStatusCode != 2){
            tupleJPQLQuery.where(product.productSellStatusCode.eq(productSellStatusCode));
        }
        //검색조건 : 선물등록여부
        if(giftProductRegisterCode != 2){
            tupleJPQLQuery.where(product.giftProductRegisterCode.eq(giftProductRegisterCode));
        }

        //검색조건 : 대분류명, 중분류명, 상품명, 상품가격, 할인가격
        if(type != null) {
            switch (type.trim()) {
                case "ALL" :
                    tupleJPQLQuery.where(product.majorCategoryTitle.like("%" + keyword + "%")
                            .or(product.subCategoryTitle.like("%" + keyword + "%"))
                            .or(product.productTitle.like("%" + keyword + "%"))
                            .or(product.productPrice.like("%" + keyword + "%"))
                            .or(giftProduct.giftProductDiscountPrice.like("%" + keyword + "%"))
                    );
                    break;
                case "majorCategoryTitle" :
                    tupleJPQLQuery.where(product.majorCategoryTitle.like("%" + keyword + "%"));
                    break;
                case "subCategoryTitle" :
                    tupleJPQLQuery.where(product.subCategoryTitle.like("%" + keyword + "%"));
                    break;
                case "productTitle" :
                    tupleJPQLQuery.where(product.productTitle.like("%" + keyword + "%"));
                    break;
                case "productPrice" :
                    tupleJPQLQuery.where(product.productPrice.like("%" + keyword + "%"));
                    break;
                case "giftProductDiscountPrice" :
                    tupleJPQLQuery.where(giftProduct.giftProductDiscountPrice.like("%" + keyword + "%"));
                    break;
            }
        }

        //정렬;
        tupleJPQLQuery.orderBy(product.productRegdate.desc());

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
    public Page<Object[]> getPartnerProductPage(String type,
                                                String keyword,
                                                String searchUserId,
                                                String roleCode,
                                                String sessionId,
                                                int productSellStatusCode,
                                                int giftProductRegisterCode,
                                                Pageable pageable) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QPartner partner = QPartner.partner;

        JPQLQuery<Product> query = from(product);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                product.productNo,  //상품번호0
                partner.partnerId,  //파트너아이디1
                partner.partnerName,    //파트너명2
                product.majorCategoryTitle, //대분류명3
                product.subCategoryTitle,   //중분류명4
                product.productTitle,   //상품명5
                product.productPrice,   //판매가격6
                product.productSellStatusCode,  //판매상태코드7
                product.productRegdate, //등록일8
                productImage.productImageNo,    //상품이미지번호9
                productImage.productImageName,  //상품이미지명10
                productImage.productImageDbName,    //상품이미지db명11
                productImage.productImageExtension, //상품이미지확장자12
                productImage.productImageThumbnailUrl,  //상품이미지썸네일주소13
                productImage.productImageUrl    //상품이미지주소14
        );

        //조인문
        tupleJPQLQuery
                .join(partner).on(product.productOwnerId.eq(partner.partnerId))
                .leftJoin(productImage).on(product.productNo.eq(productImage.productNo));

        //조건식

        //매장 권한으로 등록한 상품만 가져옴
        tupleJPQLQuery.where(product.productOwnerRole.eq("PT"));

        //권한:매장이나 파트너가 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("PT"))
            tupleJPQLQuery.where(product.productOwnerId.eq(sessionId));

        //검색조건 : 파트너명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(product.productOwnerId.eq(searchUserId));
        }
        //검색조건 : 판매코드
        if(productSellStatusCode != 2){
            tupleJPQLQuery.where(product.productSellStatusCode.eq(productSellStatusCode));
        }
        //검색조건 : 선물등록여부
        if(giftProductRegisterCode != 2){
            tupleJPQLQuery.where(product.giftProductRegisterCode.eq(giftProductRegisterCode));
        }

        //검색조건 : 대분류명, 중분류명, 상품명, 상품가격
        if(type != null) {
            switch (type.trim()) {
                case "ALL" :
                    tupleJPQLQuery.where(product.majorCategoryTitle.like("%" + keyword + "%")
                                            .or(product.subCategoryTitle.like("%" + keyword + "%"))
                                            .or(product.productTitle.like("%" + keyword + "%"))
                                            .or(product.productPrice.like("%" + keyword + "%"))
                    );
                    break;
                case "majorCategoryTitle" :
                    tupleJPQLQuery.where(product.majorCategoryTitle.like("%" + keyword + "%"));
                    break;
                case "subCategoryTitle" :
                    tupleJPQLQuery.where(product.subCategoryTitle.like("%" + keyword + "%"));
                    break;
                case "productTitle" :
                    tupleJPQLQuery.where(product.productTitle.like("%" + keyword + "%"));
                    break;
                case "productPrice" :
                    tupleJPQLQuery.where(product.productPrice.like("%" + keyword + "%"));
                    break;
            }
        }

        //정렬;
        tupleJPQLQuery.orderBy(product.productRegdate.desc());

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
    public List<Object[]> getStoreProductList(String storeId) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QGiftProduct giftProduct = QGiftProduct.giftProduct;
        QStore store = QStore.store;

        JPQLQuery<Product> query = from(product);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                product.productNo,  //상품번호0
                product.giftProductRegisterCode,    //선물등록여부코드1
                store.storeId,  //매장아이디2
                store.storeName,    //가맹점명3
                product.majorCategoryTitle, //대분류명4
                product.subCategoryTitle,   //중분류명5
                product.productTitle,   //상품명6
                product.productPrice,   //판매가격7
                giftProduct.giftProductDiscountPrice,   //할인가격8
                product.productSellStatusCode,  //판매상태코드9
                product.productRegdate, //등록일10
                productImage.productImageNo,    //상품이미지번호11
                productImage.productImageName,  //상품이미지명12
                productImage.productImageDbName,    //상품이미지db명13
                productImage.productImageExtension, //상품이미지확장자14
                productImage.productImageThumbnailUrl,  //상품이미지썸네일주소15
                productImage.productImageUrl    //상품이미지주소16
        );

        //조인문
        tupleJPQLQuery
                .join(store).on(product.productOwnerId.eq(store.storeId))
                .leftJoin(giftProduct).on(product.productNo.eq(giftProduct.productNo))
                .leftJoin(productImage).on(product.productNo.eq(productImage.productNo));

        //조건식

        tupleJPQLQuery.where(product.productOwnerId.eq(storeId));

        //정렬;
        tupleJPQLQuery.orderBy(product.productRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
