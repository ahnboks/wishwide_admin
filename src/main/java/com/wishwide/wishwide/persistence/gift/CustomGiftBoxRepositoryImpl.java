package com.wishwide.wishwide.persistence.gift;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.wishwide.wishwide.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CustomGiftBoxRepositoryImpl extends QuerydslRepositorySupport implements CustomGiftBox {
    public CustomGiftBoxRepositoryImpl(){
        super(GiftBox.class);
    }

    @Override
    public List<Object[]> getCustomerGiftBox(Long customerNo) {
        QGiftBox giftBox = QGiftBox.giftBox;
        QStore store = QStore.store;
        QMembershipCustomer customer = QMembershipCustomer.membershipCustomer;

        JPQLQuery<GiftBox> query = from(giftBox);

        JPQLQuery<Tuple> tupleJPQLQuery = query.select(
                giftBox.giftBoxNo,  //선물번호0
                store.storeId,  //매장아이디1
                store.storeName,    //가맹점명2
                giftBox.gbGiftSenderPhone,    //구매자 전화번호3
                giftBox.gbGiftSenderName, //구매자명4
                giftBox.gbGiftReceiverPhone,  //수신자 전화번호5
                giftBox.gbGiftReceiverName,   //수신자명6
                giftBox.gbProductTitle,   //상품명7
                giftBox.giftBegindate,   //선물시작일8
                giftBox.giftFinishdate,  //선물만료일9
                giftBox.giftUseCode,  //선물사용여부10
                giftBox.giftUsedate //선물사용일11
        );

        //조인문
        tupleJPQLQuery.join(store).on(giftBox.storeId.eq(store.storeId))
                .join(customer).on(giftBox.gbGiftReceiverPhone.eq(customer.membershipCustomerPhone));

        //조건식
        tupleJPQLQuery.where(customer.membershipCustomerNo.eq(customerNo)
                .and(customer.storeId.eq(giftBox.storeId)));

        //정렬;
        tupleJPQLQuery.orderBy(giftBox.giftRegdate.desc());

        //패치
        List<Tuple> tuples = tupleJPQLQuery.fetch();

        List<Object[]> resultList = new ArrayList<>();

        tuples.forEach(tuple -> {
            resultList.add(tuple.toArray());
        });

        return resultList;
    }
}
