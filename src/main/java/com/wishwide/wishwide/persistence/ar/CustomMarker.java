package com.wishwide.wishwide.persistence.ar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMarker {
    //마커 리스트 페이지
    Page<Object[]> getMarkerPage(String searchUserId,
                                 String roleCode,
                                 String sessionId,
                                 int storeArGameTypeCode,
                                 Pageable pageable);

    //마커 상세 페이지
    public Object[] getMarkerDetail(String storeId);
}
