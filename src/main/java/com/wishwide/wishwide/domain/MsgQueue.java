package com.wishwide.wishwide.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "msg_queue")
@EqualsAndHashCode(of = "mseq")
public class MsgQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 11, nullable = false, name = "mseq")
    private int mseq;

    @Column(length = 1, nullable = false, name = "msg_type",columnDefinition="char(1)")
    private String msg_type = "1";

    @Column(length = 1, nullable = false, name = "send_type",columnDefinition="char(1)")
    private String send_type = "1";

    @Column(length = 50, nullable = false, name = "dkey")
    private String dkey = "0";

    @Column(length = 11, nullable = false, name = "dcnt")
    private int dcnt = 0;

    @Column(length = 20, nullable = false, name = "dstaddr")
    private String dstaddr = "0";

    @Column(length = 20,  name = "callback")
    private String callback;

    @Column(length = 1, nullable = false, name = "stat",columnDefinition="char(1)")
    private String stat;

    @Column(length = 120, nullable = false, name = "subject",columnDefinition="varchar2(120)")
    private String subject = "0";

    @Column(length = 1, nullable = false, name = "text_type",columnDefinition="char(1)")
    private String text_type = "0";

    @Column(length = 4000, nullable = false, name = "text",columnDefinition="varchar2(4000)")
    private String text;

    @Column(length = 4000, name = "text2",columnDefinition="varchar2(4000)")
    private String text2;

    @Column(length = 8, name = "expiretime",columnDefinition="number(8)")
    private int expiretime = 86400;

    @Column(length = 30, name = "k_template_code",columnDefinition="varchar2(30)")
    private String k_template_code;

    @Column(length = 30, name = "k_expiretime",columnDefinition="number(6)")
    private String k_expiretime;

    @Column(length = 2, name = "k_button_type",columnDefinition="char(2)")
    private String k_button_type;

    @Column(length = 56, name = "k_button_name")
    private String k_button_name;

    @Column(length = 256, name = "k_button_url",columnDefinition="varchar2(256)")
    private String k_button_url;

    @Column(length = 256, name = "k_button_url2",columnDefinition="varchar2(256)")
    private String k_button_url2;

    @Column(length = 2, name = "k_button2_type",columnDefinition="char(2)")
    private String k_button2_type;

    @Column(length = 56, name = "k_button2_name")
    private String k_button2_name;

    @Column(length = 256, name = "k_button2_url",columnDefinition="varchar2(256)")
    private String k_button2_url;

    @Column(length = 256, name = "k_button2_url2",columnDefinition="varchar2(256)")
    private String k_button2_url2;

    @Column(length = 2, name = "k_button3_type",columnDefinition="char(2)")
    private String k_button3_type;

    @Column(length = 56, name = "k_button3_name")
    private String k_button3_name;

    @Column(length = 256, name = "k_button3_url",columnDefinition="varchar2(256)")
    private String k_button3_url;

    @Column(length = 256, name = "k_button3_url2",columnDefinition="varchar2(256)")
    private String k_button3_url2;

    @Column(length = 2, name = "k_button4_type",columnDefinition="char(2)")
    private String k_button4_type;

    @Column(length = 56, name = "k_button4_name")
    private String k_button4_name;

    @Column(length = 256, name = "k_button4_url",columnDefinition="varchar2(256)")
    private String k_button4_url;

    @Column(length = 256, name = "k_button4_url2",columnDefinition="varchar2(256)")
    private String k_button4_url2;

    @Column(length = 2, name = "k_button5_type",columnDefinition="char(2)")
    private String k_button5_type;

    @Column(length = 56, name = "k_button5_name")
    private String k_button5_name;

    @Column(length = 256, name = "k_button5_url",columnDefinition="varchar2(256)")
    private String k_button5_url;

    @Column(length = 256, name = "k_button5_url2",columnDefinition="varchar2(256)")
    private String k_button5_url2;

    @Column(length = 256, name = "k_img_link_url",columnDefinition="varchar2(256)")
    private String k_img_link_url;

    @Column(length = 1, name = "k_next_type",columnDefinition="char(1)")
    private String k_next_type = "0";

    @Column(length = 2, name = "filecnt",columnDefinition="number(2)")
    private int filecnt = 0;

    @Column(length = 512, name = "fileloc1",columnDefinition="varchar2(512)")
    private String fileloc1;

    @Column(length = 11, name = "filesize1 ",columnDefinition="number(11)")
    private int filesize1;

    @Column(length = 512, name = "fileloc2",columnDefinition="varchar2(512)")
    private String fileloc2;

    @Column(length = 11, name = "filesize2 ",columnDefinition="number(11)")
    private int filesize2;

    @Column(length = 512, name = "fileloc3",columnDefinition="varchar2(512)")
    private String fileloc3;

    @Column(length = 11, name = "filesize3 ",columnDefinition="number(11)")
    private int filesize3;

    @Column(length = 512, name = "fileloc4",columnDefinition="varchar2(512)")
    private String fileloc4;

    @Column(length = 11, name = "filesize4 ",columnDefinition="number(11)")
    private int filesize4;

    @Column(length = 2, name = "filecnt_checkup ",columnDefinition="number(2)")
    private int filecnt_checkup;

    @CreationTimestamp
    @Column(name = "insert_time", nullable = false)
    private LocalDateTime insert_time;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime request_time;

    @Column(name = "report_time")
    private LocalDateTime report_time;

    @Column(name = "send_time")
    private LocalDateTime send_time;

    @Column(name = "save_time")
    private LocalDateTime save_time;

    @Column(name = "tcprecv_time")
    private LocalDateTime tcprecv_time;

    @Column(length = 10, name = "telecom",columnDefinition="varchar2(10)")
    private String telecom;

    @Column(length = 4, name = "result",columnDefinition="char(4)")
    private String result;

    @Column(length = 11, name = "repcnt ",columnDefinition="number(11)")
    private int repcnt;

    @Column(length = 11, name = "server_id ",columnDefinition="number(11)")
    private int server_id;

    @Column(length = 20, name = "opt_id",columnDefinition="varchar2(20)")
    private String opt_id;

    @Column(length = 40, name = "opt_cmp",columnDefinition="varchar2(40)")
    private String opt_cmp;

    @Column(length = 40, name = "opt_post",columnDefinition="varchar2(40)")
    private String opt_post;

    @Column(length = 40, name = "opt_name",columnDefinition="varchar2(40)")
    private String opt_name;

    @Column(length = 11, name = "ext_col0 ",columnDefinition="number(11)")
    private int ext_col0;

    @Column(length = 64, name = "ext_col1",columnDefinition="varchar2(64)")
    private String ext_col1;

    @Column(length = 32, name = "ext_col2",columnDefinition="varchar2(32)")
    private String ext_col2;

    @Column(length = 33, name = "ext_col3",columnDefinition="varchar2(33)")
    private String ext_col3;

    @Column(length = 10, name = "pseq",columnDefinition="varchar2(10)")
    private String pseq;

    @Column(length = 40, name = "sender_key",columnDefinition="varchar2(40)")
    private String sender_key;

    @Column(length = 1, name = "k_at_send_type",columnDefinition="char(1)")
    private String k_at_send_type = "0";

    @Column(length = 1, name = "k_ad_flag",columnDefinition="char(1)")
    private String k_ad_flag = "N";
}
