package com.wishwide.wishwide.persistence.ar;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomMarkerRepositoryImpl extends QuerydslRepositorySupport implements CustomMarker {
    public CustomMarkerRepositoryImpl() {
        super(Store.class);
    }

    @Override
    public Page<Object[]> getMarkerPage(String searchUserId,
                                        String roleCode,
                                        String sessionId,
                                        int storeArGameTypeCode,
                                        Pageable pageable) {
        QStore store = QStore.store;

        JPQLQuery<Store> query = from(store);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                store.storeId,  //매장아이디0
                store.storeName,    //매장명1
                store.storeArGameUseCode,   //ar 게임사용여부2
                store.storeArGameTypeCode,  //ar 게임타입3
                store.storeArGameTypeName   //ar 게임타입명4
        );

        //조건식

        //권한:매장이 로그인했을 경우 한개의 리스트만 가져오기
        if (roleCode.equals("ST"))
            tupleJPQLQuery.where(store.storeId.eq(sessionId));

        //검색조건 : 가맹점명
        if (!searchUserId.equals("ALL")) {
            System.out.println("검색아이디" + searchUserId);
            tupleJPQLQuery.where(store.storeId.eq(searchUserId));
        }
        //검색조건 : 게임타입
        if (storeArGameTypeCode != 2) {
            tupleJPQLQuery.where(store.storeArGameTypeCode.eq(storeArGameTypeCode));
        }

        //ar게임 사용하는 매장리스트만 가져오기
        tupleJPQLQuery.where(store.storeArGameUseCode.eq(1));

        //정렬;
        tupleJPQLQuery.orderBy(store.storeRegdate.desc());

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
    public Object[] getMarkerDetail(String storeId) {
        QStore store = QStore.store;
        QMarkerDataFile markerDataFile = QMarkerDataFile.markerDataFile;

        JPQLQuery<Store> query = from(store);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                store.storeId,  //매장아이디0
                store.storeName,    //매장명1
                markerDataFile.markerDataFileNo,    //dat파일번호2
                markerDataFile.markerDatFileName,    //dat파일명3
                markerDataFile.markerDatDbFile,    //datdb파일명4
                markerDataFile.markerDatFileExtension,    //dat파일확장자5
                markerDataFile.markerDatFileUrl,    //dat파일주소6
                markerDataFile.markerXmlFileName,    //xml파일명7
                markerDataFile.markerXmlDbFile,    //xmldb파일명8
                markerDataFile.markerXmlFileExtension,    //xml파일확장자9
                markerDataFile.markerXmlFileUrl    //xml파일주소10
        );

        //조인문
        tupleJPQLQuery
                .leftJoin(markerDataFile).on(store.storeId.eq(markerDataFile.storeId));

        //조건식
        tupleJPQLQuery.where(store.storeId.eq(storeId));

        //한 레코드만 반환
        Tuple tuple = tupleJPQLQuery.fetchOne();

        return tuple.toArray();
    }
}
