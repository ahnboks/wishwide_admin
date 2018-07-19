package com.wishwide.wishwide.persistence.gift;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomGiftBox {
    //고객 선물 리스트 불러오기
    List<Object[]> getCustomerGiftBox(Long customerNo);
}
