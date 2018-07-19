package com.wishwide.wishwide.persistence.device;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomDeviceModelRepositoryImpl extends QuerydslRepositorySupport implements CustomDeviceModel {
    public CustomDeviceModelRepositoryImpl(){
        super(DeviceModel.class);
    }

    @Override
    public Page<Object[]> getDeviceModelPage(String type,
                                             String keyword,
                                             String deviceTypeCode,
                                             Pageable pageable) {
        QDeviceModel deviceModel = QDeviceModel.deviceModel;
        QDeviceModelImage deviceModelImage = QDeviceModelImage.deviceModelImage;

        JPQLQuery<DeviceModel> query = from(deviceModel);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                deviceModel.deviceModelNo,  //디바이스모델번호0
                deviceModel.deviceTypeCode, //디바이스타입코드1
                deviceModel.deviceModelTitle,   //디바이스모델명2
                deviceModel.deviceModelResolutionWidth, //디바이스모델가로해상도3
                deviceModel.deviceModelResolutionHeight,    //디바이스모델세로해상도4
                deviceModel.deviceModelRegdate, //디바이스모델등록일5
                deviceModelImage.deviceModelImageNo,    //디바이스모델이미지번호6
                deviceModelImage.deviceModelImageName,  //디바이스모델이미지명7
                deviceModelImage.deviceModelImageDbName,    //디바이스모델디비명8
                deviceModelImage.deviceModelImageExtension, //디바이스모델이미지확장자9
                deviceModelImage.deviceModelImageThumbnailUrl,  //디바이스모델이미지썸네일주소10
                deviceModelImage.deviceModelImageUrl    //디바이스모델이미지주소11
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(deviceModelImage).on(deviceModel.deviceModelNo.eq(deviceModelImage.deviceModelNo));

        //조건식

        //디바이스 타입에 따른 검색조건
        if(!deviceTypeCode.equals("ALL"))
            tupleJPQLQuery.where(deviceModel.deviceTypeCode.eq(deviceTypeCode));

       if(keyword != null){
           tupleJPQLQuery.where(deviceModel.deviceModelTitle.like("%"+keyword+"%"));
       }

        //정렬
        tupleJPQLQuery.orderBy(deviceModel.deviceModelRegdate.desc());

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
    public Object[] getDeviceModelDetail(Long deviceModelNo) {
        QDeviceModel deviceModel = QDeviceModel.deviceModel;
        QDeviceModelImage deviceModelImage = QDeviceModelImage.deviceModelImage;

        JPQLQuery<DeviceModel> query = from(deviceModel);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                deviceModel.deviceModelNo,  //디바이스모델번호0
                deviceModel.deviceTypeCode, //디바이스타입코드1
                deviceModel.deviceModelTitle,   //디바이스모델명2
                deviceModel.deviceModelResolutionWidth, //디바이스모델가로해상도3
                deviceModel.deviceModelResolutionHeight,    //디바이스모델세로해상도4
                deviceModel.deviceModelRegdate, //디바이스모델등록일5
                deviceModelImage.deviceModelImageNo,    //디바이스모델이미지번호6
                deviceModelImage.deviceModelImageName,  //디바이스모델이미지명7
                deviceModelImage.deviceModelImageDbName,    //디바이스모델디비명8
                deviceModelImage.deviceModelImageExtension, //디바이스모델이미지확장자9
                deviceModelImage.deviceModelImageThumbnailUrl,  //디바이스모델이미지썸네일주소10
                deviceModelImage.deviceModelImageUrl    //디바이스모델이미지주소11
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(deviceModelImage).on(deviceModel.deviceModelNo.eq(deviceModelImage.deviceModelNo));

        //조건식
        tupleJPQLQuery.where(deviceModel.deviceModelNo.eq(deviceModelNo));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }

    @Override
    public List<Object[]> getDeviceModelList(String deviceTypeCode) {
        QDeviceModel deviceModel = QDeviceModel.deviceModel;

        JPQLQuery<DeviceModel> query = from(deviceModel);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                deviceModel.deviceModelNo,
                deviceModel.deviceTypeCode,
                deviceModel.deviceModelTitle
        );

        //조건문

        //디바이스 타입에 맞는 디바이스 모델만 가져옴
        tupleJPQLQuery.where(deviceModel.deviceTypeCode.eq(deviceTypeCode));

        //정렬
        tupleJPQLQuery.orderBy(deviceModel.deviceModelTitle.asc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
