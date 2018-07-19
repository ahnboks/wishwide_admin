package com.wishwide.wishwide.persistence.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCustomer {
    //고객 리스트 페이지
    Page<Object[]> getCustomerPage(String type,
                                   String keyword,
                                   String searchUserId,
                                   String roleCode,
                                   String sessionId,
                                   String customerBenefitTypeCode,
                                   String customerGradeTypeCode,
                                   Pageable pageable);

    //고객 상세 불러오기
    Object[] getCustomerDetail(Long customerNo);

    //매장 고객 리스트 불러오기
    List<Object[]> getStoreCustomerList(String storeId);
}
