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
@Table(name = "tb_alarm_message_variable")
@EqualsAndHashCode(of = "alarmMessageVariableNo")
public class AlarmMessageVariable {
    //알림메시지 변수 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false, name = "alarm_message_variable_no")
    private Long alarmMessageVariableNo;

    //알림 번호
    @Column(length = 20, nullable = false, name = "alarm_no")
    private Long alarmNo;

    //매장 아이디
    @Column(length = 20, nullable = false, name = "store_id")
    private String storeId;

    //알림 변수코드
    @Column(length = 10, nullable = false, name = "alarm_variable_code")
    private String alarmVariableCode;

    //알림 변수명
    @Column(length = 20, nullable = false, name = "alarm_variable_name")
    private String alarmVariableName;

    //알림 변수 설명
    @Column(length = 50, name = "alarm_variable_description")
    private String alarmVariableDescription;

    //알림메시지 변수 등록일시
    @CreationTimestamp
    @Column(nullable = false, name = "alarm_message_variable_regdate")
    private LocalDateTime alarmMessageVariableRegdate;

    //알림 메시지 변수 수정일시
    @UpdateTimestamp
    @Column(nullable = false, name = "alarm_message_variable_updatedate")
    private LocalDateTime alarmMessageVariableUpdatedate;
}
