package com.wishwide.wishwide.persistence.partner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPartner {
    //파트너 리스트 페이지
    public Page<Object[]> getPartnerPage(String keyword,
                                         String searchUserId,
                                         String roleCode,
                                         String sessionId,
                                         Pageable pageable);
    //파트너 상세 페이지
    public Object[] getPartnerDetail(String partnerId);

    //파트너명 리스트
    public List<Object[]> getPartnerNameList();

    //파트너 가맹점 리스트
    public List<Object[]> getPartnerStore(String partnerId);

    //모든 가맹점 리스트
    public List<Object[]> getAllPartnerStore(String partnerId);

}
