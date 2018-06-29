package com.wishwide.wishwide.vo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageVO {
    private static final int DEFAULT_SIZE=10;
    private static final int DEFAULT_MAX_SIZE=50;

    private int page;
    private int size;

    private String keyword;
    private String type;

    //매장 리스트
    private String serviceOperationCode = "ALL";    //서비스운영상태코드
    private String userId = "ALL"; //매장 아이디

    //상품 리스트
    private int productSellStatusCode = 2;  //상품판매코드
    private int giftProductRegisterCode = 2;  //선물등록여부코드

    //디바이스 리스트
    private String deviceTypeCode = "ALL"; //디바이스타입코드

    //선물거래내역 리스트
    private String giftPaymentStatusCode ="ALL";    //선물결제상태코드

    //쿠폰발급내역 리스트
    private String couponTypeCode = "ALL";  //쿠폰유형코드
    private String couponTargetTypeCode = "ALL";    //쿠폰발급대상코드
    private String couponPublishTypeCode = "ALL";   //쿠폰발급경로코드
    private int couponUseCode = 2;  //쿠폰사용여부코드

    //고객 리스트
    private String customerBenefitTypeCode = "ALL";   //고객혜택타입코드
    private String customerGradeTypeCode = "ALL";   //고객등급유형코드

    //알림 리스트
    private String alarmTypeCode = "ALL";
    private String alarmPurposeCode = "ALL";
    private String alarmTargetTypeCode = "ALL";

    //마커 리스트
    private int storeArGameTypeCode = 2;

   private int visibleCode = 2;
    private int visibleCode2 = 2;   //같은 리스트의 활성화 여부를 위한 활성화 여부 검사

    public PageVO() {
        this.page=1;
        this.size=DEFAULT_SIZE;
    }

    public int getStoreArGameTypeCode() {
        return storeArGameTypeCode;
    }

    public void setStoreArGameTypeCode(int storeArGameTypeCode) {
        this.storeArGameTypeCode = storeArGameTypeCode;
    }

    public String getCustomerBenefitTypeCode() {
        return customerBenefitTypeCode;
    }

    public void setCustomerBenefitTypeCode(String customerBenefitTypeCode) {
        this.customerBenefitTypeCode = customerBenefitTypeCode;
    }

    public String getAlarmTypeCode() {
        return alarmTypeCode;
    }

    public void setAlarmTypeCode(String alarmTypeCode) {
        this.alarmTypeCode = alarmTypeCode;
    }

    public String getAlarmPurposeCode() {
        return alarmPurposeCode;
    }

    public void setAlarmPurposeCode(String alarmPurposeCode) {
        this.alarmPurposeCode = alarmPurposeCode;
    }

    public String getAlarmTargetTypeCode() {
        return alarmTargetTypeCode;
    }

    public void setAlarmTargetTypeCode(String alarmTargetTypeCode) {
        this.alarmTargetTypeCode = alarmTargetTypeCode;
    }

    public String getCustomerGradeTypeCode() {
        return customerGradeTypeCode;
    }

    public void setCustomerGradeTypeCode(String customerGradeTypeCode) {
        this.customerGradeTypeCode = customerGradeTypeCode;
    }

    public String getCouponTypeCode() {
        return couponTypeCode;
    }

    public void setCouponTypeCode(String couponTypeCode) {
        this.couponTypeCode = couponTypeCode;
    }

    public String getCouponTargetTypeCode() {
        return couponTargetTypeCode;
    }

    public void setCouponTargetTypeCode(String couponTargetTypeCode) {
        this.couponTargetTypeCode = couponTargetTypeCode;
    }

    public String getCouponPublishTypeCode() {
        return couponPublishTypeCode;
    }

    public void setCouponPublishTypeCode(String couponPublishTypeCode) {
        this.couponPublishTypeCode = couponPublishTypeCode;
    }

    public int getCouponUseCode() {
        return couponUseCode;
    }

    public void setCouponUseCode(int couponUseCode) {
        this.couponUseCode = couponUseCode;
    }

    public int getGiftProductRegisterCode() {
        return giftProductRegisterCode;
    }

    public void setGiftProductRegisterCode(int giftProductRegisterCode) {
        this.giftProductRegisterCode = giftProductRegisterCode;
    }

    public String getGiftPaymentStatusCode() {
        return giftPaymentStatusCode;
    }

    public void setGiftPaymentStatusCode(String giftPaymentStatusCode) {
        this.giftPaymentStatusCode = giftPaymentStatusCode;
    }

    public String getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductSellStatusCode() {
        return productSellStatusCode;
    }

    public void setProductSellStatusCode(int productSellStatusCode) {
        this.productSellStatusCode = productSellStatusCode;
    }

    public String getServiceOperationCode() {
        return serviceOperationCode;
    }

    public void setServiceOperationCode(String serviceOperationCode) {
        this.serviceOperationCode = serviceOperationCode;
    }

    public int getVisibleCode2() {
        return visibleCode2;
    }

    public void setVisibleCode2(int visibleCode2) {
        this.visibleCode2 = visibleCode2;
    }

    public int getVisibleCode() {
        return visibleCode;
    }

    public void setVisibleCode(int visibleCode) {
        this.visibleCode = visibleCode;
    }

    public String getKeyword() {
        return keyword;
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page < 0 ? 1 : page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {

        this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public Pageable makePageable(int direction, String... props) {

        Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(this.page - 1, this.size, dir, props);
    }
}