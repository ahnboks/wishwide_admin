package com.wishwide.wishwide.persistence.productCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomProductCategory {
    //매장 리스트 페이지
//    public Page<Object[]> getStorePage(String keyword,
//                                       String searchUserId,
//                                       String roleCode,
//                                       String sessionId,
//                                       String serviceOperationCode,
//                                       Pageable pageable);

//    //매장 상세 페이지
//    public Object[] getStoreDetail(String storeId);

    //대분류명 리스트
    public List<Object[]> getMajorCategoryList(String storeId);

    //중분류명 리스트
    public List<Object[]> getSubCategoryList(Long majorCategoryNo);
}
