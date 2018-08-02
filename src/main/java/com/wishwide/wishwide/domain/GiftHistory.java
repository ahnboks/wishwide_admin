package com.wishwide.wishwide.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_gift_history")
@EqualsAndHashCode(of = "giftHistoryNo")
@ToString
public class GiftHistory {
    //선물내역번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_history_no", nullable = false)
    private Long giftHistoryNo;

    //선물번호
    @Column(name = "gift_box_no", nullable = false)
    private Long giftBoxNo;

    //선물구매번호
    @Column(name = "gift_payment_no", nullable = false)
    private Long giftPaymentNo;

    //선물 수신자 타입 코드(1-본인, 2-타인)
    @Column(length = 10, nullable = false, name = "gh_gift_receiver_type_code")
    private String ghGiftReceiverTypeCode;

    //매장 아이디
    @Column(length = 20, nullable = false, name = "store_id")
    private String storeId;

    //멤버쉽고객번호
    @Column(length = 20, nullable = false, name = "membership_customer_no")
    private Long membershipCustomerNo;

    //선물발신자번호
    @Column(name = "gh_gift_sender_phone", nullable = false, length = 20)
    private String ghGiftSenderPhone;

    //선물발신자명
    @Column(name = "gh_gift_sender_name", nullable = false, length = 20)
    private String ghGiftSenderName;

    //선물수신자번호
    @Column(name = "gh_gift_receiver_phone", nullable = false, length = 20)
    private String ghGiftReceiverPhone;

    //선물수신자명
    @Column(name = "gh_gift_receiver_name", nullable = false, length = 20)
    private String ghGiftReceiverName;

    //선물상품번호
    @Column(name = "gift_product_no", nullable = false)
    private Long giftProductNo;

    //상품명
    @Column(nullable = false, name = "gh_product_title", length = 100)
    private String ghProductTitle;

    //선물 메시지타입 코드
    @Column(name = "gh_gift_message_type_code", nullable = false, length = 10)
    private String ghGiftMessageTypeCode;

    //선물메시지
    @Column(name = "gh_gift_message", length = 100)
    private String ghGiftMessage;

    //선물함 시작일시
    @Column(name = "gh_gift_begindate", nullable = false)
    private LocalDate ghGiftBegindate;

    //선물함 만료일시
    @Column(name = "gh_gift_finishdate", nullable = false)
    private LocalDate ghGiftFinishdate;

    //선물사용코드
    @Column(name = "gh_gift_use_code", nullable = false, length = 10, columnDefinition = "int default 0")
    private int ghGiftUseCode;

    //선물사용일시
    @Column(name = "gh_gift_usedate")
    private LocalDateTime ghGiftUsedate;

    //선물 사용 가맹점
    @Column(name = "gh_gift_use_store_id", length = 20)
    private String gh_iftUseStoreId;

    //선물내역등록일시
    @CreationTimestamp
    @Column(name = "gift_history_regDate", nullable = false)
    private LocalDateTime giftHistoryRegDate;
}
