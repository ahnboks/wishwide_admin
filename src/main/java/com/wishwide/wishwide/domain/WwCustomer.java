package com.wishwide.wishwide.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_ww_customer")
@EqualsAndHashCode(of = "customerNo")
public class WwCustomer {
    //고객번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false, name = "customer_no")
    private Long customerNo;

    //고객 전화번호
    @Column(length = 12, nullable = false, name = "customer_phone")
    private String customerPhone;

    //고객명
    @Column(length = 20, name = "customer_name")
    private String customerName;

    //고객성별타입
    @Column(length = 20, name = "customer_gender_type_code")
    private String customerGenderTypeCode;

    //고객생일
    @Column(length = 20, name = "customer_birth")
    private String customerBirth;

    //고객이메일
    @Column(length = 40, name = "customer_email")
    private String customerEmail;

    //고객메모
    @Column(length = 400, name = "customer_memo")
    private String customerMemo;

    //고객 가입수단 코드
    @Column(length = 10, name = "customer_join_path")
    private String customerJoinPath;

    //고객활성여부
    @Column(name="customer_visibleCode", nullable = false, length = 1)
    private int customerVisibleCode = 1;

    //고객등록일시
    @CreationTimestamp
    @Column(nullable = false, name = "customer_regdate")
    private LocalDateTime customerRegdate;

    //고객수정일시
    @UpdateTimestamp
    @Column(nullable = false, name = "customer_updatedate")
    private LocalDateTime customerUpdatedate;
}
