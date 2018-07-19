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

public class CustomDeviceRepositoryImpl extends QuerydslRepositorySupport implements CustomDevice {
    public CustomDeviceRepositoryImpl(){
        super(Device.class);
    }

    @Override
    public Page<Object[]> getDevicePage(String type,
                                        String keyword,
                                        String searchUserId,
                                        String deviceTypeCode,
                                        String roleCode,
                                        String sessionId,
                                        int visibleCode,
                                        Pageable pageable) {
        QDevice device = QDevice.device;
        QDeviceImage deviceImage = QDeviceImage.deviceImage;
        QStore store = QStore.store;

        JPQLQuery<Device> query = from(device);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                device.deviceNo,    //디바이스번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                device.deviceTypeCode,  //디바이스타입코드3
                device.deviceModelTitle,    //모델명4
                device.deviceTitle, //디바이스명5
                device.deviceId,    //디바이스id 6
                device.posId,   //포스 id 7
                device.deviceVisibleCode,   //디바이스 활성화8
                device.deviceRegdate,   //등록일9
                deviceImage.deviceImageNo,  //이미지번호10
                deviceImage.deviceImageName,    //이미지명11
                deviceImage.deviceImageDbName,  //이미지 db명12
                deviceImage.deviceImageExtension,   //이미지 확장자13
                deviceImage.deviceImageThumbnailUrl,    //이미지 썸네일 주소14
                deviceImage.deviceImageUrl  //이미지 주소15
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(deviceImage).on(device.deviceNo.eq(deviceImage.deviceNo))
                .join(store).on(device.storeId.eq(store.storeId));

        //조건식

        //활성화코드가 전체 대상이 아닐 경우
        if(visibleCode != 2){
            tupleJPQLQuery.where(device.deviceVisibleCode.eq(visibleCode));
        }

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if(roleCode.equals("ST"))
            tupleJPQLQuery.where(device.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if(!searchUserId.equals("ALL")){
            tupleJPQLQuery.where(store.storeId.eq(searchUserId));
        }
        //검색조건 : 디바이스 타입
        if(!deviceTypeCode.equals("ALL")){
            tupleJPQLQuery.where(device.deviceTypeCode.eq(deviceTypeCode));
        }

        //검색조건 : 모델명, 디바이스명, 디바이스 id, POS id
        if(type != null) {
            switch (type.trim()) {
                case "ALL" :
                    tupleJPQLQuery.where(device.deviceModelTitle.like("%" + keyword + "%")
                            .or(device.deviceTitle.like("%" + keyword + "%"))
                            .or(device.deviceId.like("%" + keyword + "%"))
                            .or(device.posId.like("%" + keyword + "%"))
                    );
                    break;
                case "deviceModelTitle" :
                    tupleJPQLQuery.where(device.deviceModelTitle.like("%" + keyword + "%"));
                    break;
                case "deviceTitle" :
                    tupleJPQLQuery.where(device.deviceTitle.like("%" + keyword + "%"));
                    break;
                case "deviceId" :
                    tupleJPQLQuery.where(device.deviceId.like("%" + keyword + "%"));
                    break;
                case "posId" :
                    tupleJPQLQuery.where(device.posId.like("%" + keyword + "%"));
                    break;
            }
        }
        //정렬;
        tupleJPQLQuery.orderBy(device.deviceRegdate.desc());

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
    public Object[] getDeviceDetail(Long deviceNo) {
        QStore store = QStore.store;
        QDevice device = QDevice.device;
        QDeviceImage deviceImage = QDeviceImage.deviceImage;

        JPQLQuery<Device> query = from(device);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                device.deviceNo,    //디바이스번호0
                store.storeId,  //매장아이디1
                store.storeName,    //매장명2
                device.deviceTypeCode,  //디바이스타입코드3
                device.deviceModelNo,   //디바이스모델번호4
                device.deviceModelTitle,    //디바이스모델명5
                device.deviceTitle, //디바이스명6
                device.deviceId,    //디바이스아이디7
                device.posId,   //포스아이디8
                device.socketId,    //소켓아이디9
                device.deviceMacAddress,    //맥주소10
                deviceImage.deviceImageNo,  //이미지번호11
                deviceImage.deviceImageName,    //이미지명12
                deviceImage.deviceImageDbName,  //이미지 db명13
                deviceImage.deviceImageExtension,   //이미지 확장자14
                deviceImage.deviceImageUrl, //이미지 주소15
                deviceImage.deviceImageThumbnailUrl, //이미지 썸네일16
                deviceImage.deviceImageTypeCode //이미지타입코드17
        );

        //조인문
        tupleJPQLQuery
                .join(store).on(device.storeId.eq(store.storeId))
                .leftJoin(deviceImage).on(device.deviceNo.eq(deviceImage.deviceNo));

        //조건식
        tupleJPQLQuery.where(device.deviceNo.eq(deviceNo));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }


    @Override
    public List<Object[]> getStoreDeviceList(String storeId) {
        QDevice device = QDevice.device;
        QDeviceImage deviceImage = QDeviceImage.deviceImage;
        QStore store = QStore.store;

        JPQLQuery<Device> query = from(device);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                device.deviceNo,    //디바이스번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                device.deviceTypeCode,  //디바이스타입코드3
                device.deviceModelTitle,    //모델명4
                device.deviceTitle, //디바이스명5
                device.deviceId,    //디바이스id 6
                device.posId,   //포스 id 7
                device.deviceVisibleCode,   //디바이스 활성화8
                device.deviceRegdate,   //등록일9
                deviceImage.deviceImageNo,  //이미지번호10
                deviceImage.deviceImageName,    //이미지명11
                deviceImage.deviceImageDbName,  //이미지 db명12
                deviceImage.deviceImageExtension,   //이미지 확장자13
                deviceImage.deviceImageThumbnailUrl,    //이미지 썸네일 주소14
                deviceImage.deviceImageUrl  //이미지 주소15
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(deviceImage).on(device.deviceNo.eq(deviceImage.deviceNo))
                .join(store).on(device.storeId.eq(store.storeId));

        //조건식
        tupleJPQLQuery.where(device.storeId.eq(storeId));

        //정렬;
        tupleJPQLQuery.orderBy(device.deviceRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });


        return resultList;
    }
}
