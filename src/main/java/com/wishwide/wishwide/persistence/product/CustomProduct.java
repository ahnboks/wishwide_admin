package com.wishwide.wishwide.persistence.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomProduct {
    //매장 상품 리스트 페이지
    public Page<Object[]> getStoreProductPage(String type,
                                              String keyword,
                                              String searchUserId,
                                              String roleCode,
                                              String sessionId,
                                              int productSellStatusCode,
                                              int giftProductRegisterCode,
                                              Pageable pageable);

    //파트너 상품 리스트 페이지
    public Page<Object[]> getPartnerProductPage(String type,
                                                String keyword,
                                                String searchUserId,
                                                String roleCode,
                                                String sessionId,
                                                int productSellStatusCode,
                                                int giftProductRegisterCode,
                                                Pageable pageable);

    public Object[] getStoreProductDetail(Long productNo);

    public Object[] getPartnerProductDetail(Long productNo);

   //매장 상품 리스트 불러오기
    List<Object[]> getStoreProductList(String storeId);

    //파트너 상품 리스트 불러오기
    List<Object[]> getPartnerProductList(String partnerId);

    //서브상품 리스트 불러오기
    List<Object[]> getSubProductList(Long productNo);

    //선물상품 리스트 불러오기
    public List<Object[]> getGiftProduct(Long productNo);

}
