package com.wishwide.wishwide.alarm;

import com.wishwide.wishwide.domain.*;
import com.wishwide.wishwide.persistence.alarm.*;
import com.wishwide.wishwide.persistence.coupon.CustomCouponRepository;
import com.wishwide.wishwide.persistence.customer.CustomCustomerRepository;
import com.wishwide.wishwide.persistence.store.CustomStoreRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log
@Component
public class AlarmManager {
    @Autowired
    MsgQueueRepository msgQueueRepository;

    @Autowired
    CustomAlarmSetRepository customAlarmSetRepository;

    @Autowired
    CustomAlarmSendHistoryRepository customAlarmSendHistoryRepository;

    @Autowired
    AlarmSendLogRepository alarmSendLogRepository;

    @Autowired
    AlarmMessageVariableRepository alarmMessageVariableRepository;

    @Autowired
    CustomStoreRepository customStoreRepository;

    @Autowired
    CustomCouponRepository customCouponRepository;

    @Autowired
    CustomCustomerRepository customCustomerRepository;

    private String alarmMessage = "";

    private Alarm alarm;

    //GET 매장 알림발송설정 여부(storeId)
    public Alarm getStoreAlarmSetCode(String storeId, String alarmCode) {
        log.info("매장아이디 : "+storeId+" 코드 : "+alarmCode);

        log.info("알림 정보" + customAlarmSetRepository.findByAlarmSetByStoreId(storeId));

        List<Alarm> alarmList = customAlarmSetRepository.findByAlarmSetByStoreId(storeId);

        if (alarmList != null) {
            customAlarmSetRepository.findByAlarmSetByStoreId(storeId).forEach(alarmSet -> {
                if (alarmSet.getAlarmPurposeCode().equals(alarmCode)) {
                    if (alarmSet.getAlarmVisibleCode() == 1) {
                        alarm = alarmSet;
                    } else {
                        alarm = null;
                    }
                }
            });
        } else {
            alarm = null;
        }



        return alarm;
    }

    //SAVE 알림발송내역 & 알림발송로그(VO)
    public void saveAlarmHistoryAndLog(MembershipCustomer membershipCustomer, AlarmSendHistory alarmSendHistoryVO){
        //내역 저장
        AlarmSendHistory alarmSendHistory = new AlarmSendHistory();
        alarmSendHistory.setMembershipCustomerNo(membershipCustomer.getMembershipCustomerNo());
        alarmSendHistory.setCustomerPhone(membershipCustomer.getCustomerPhone());
        alarmSendHistory.setCustomerGradeTypeCode(membershipCustomer.getCustomerGradeTypeCode());
        alarmSendHistory.setCustomerName(membershipCustomer.getCustomerName());
        alarmSendHistory.setAlarmMessage(alarmSendHistoryVO.getAlarmMessage());
        alarmSendHistory.setStoreId(alarmSendHistoryVO.getStoreId());
        alarmSendHistory.setAlarmSendTypeCode(alarmSendHistoryVO.getAlarmSendTypeCode());
        alarmSendHistory.setAlarmSendWayCode(alarmSendHistoryVO.getAlarmSendWayCode());

        if(alarmSendHistoryVO.getAlarmReservationTime() != null) {
            alarmSendHistory.setAlarmReservationTime(alarmSendHistoryVO.getAlarmReservationTime());
        }

        customAlarmSendHistoryRepository.save(alarmSendHistory);

        //로그 저장
        AlarmSendLog alarmSendLog = new AlarmSendLog();
        alarmSendLog.setMembershipCustomerNo(membershipCustomer.getMembershipCustomerNo());
        alarmSendLog.setCustomerPhone(membershipCustomer.getCustomerPhone());
        alarmSendLog.setCustomerGradeTypeCode(membershipCustomer.getCustomerGradeTypeCode());
        alarmSendLog.setCustomerName(membershipCustomer.getCustomerName());
        alarmSendLog.setAlarmMessage(alarmSendHistoryVO.getAlarmMessage());
        alarmSendLog.setStoreId(alarmSendHistoryVO.getStoreId());
        alarmSendLog.setAlarmSendTypeCode(alarmSendHistoryVO.getAlarmSendTypeCode());
        alarmSendLog.setAlarmSendWayCode(alarmSendHistoryVO.getAlarmSendWayCode());

        if(alarmSendHistoryVO.getAlarmReservationTime() != null) {
            alarmSendLog.setAlarmReservationTime(alarmSendHistoryVO.getAlarmReservationTime());
        }

        log.info("내역 & 로그 저장 완료");

       alarmSendLogRepository.save(alarmSendLog);
    }

    //SAVE 알림(VO)
    public void saveMsgQueue(String alarmMessage, String customerPhone, LocalDateTime reservationTime){
        MsgQueue msgQueue = new MsgQueue();

        //알림메시지 바이트 크기가 81이 넘을 경우 LMS로 전송.
        if(alarmMessage.getBytes().length < 81){
            msgQueue.setMsg_type("1");
        }
        else{
            msgQueue.setMsg_type("3");
            msgQueue.setFilecnt(0);
        }

        msgQueue.setDstaddr(customerPhone);
        msgQueue.setText(alarmMessage);

        //나중에 매장번호로 수정해야 할 부분
        msgQueue.setCallback("01088041229");

        if(reservationTime != null)
            msgQueue.setRequest_time(reservationTime);
        else
            msgQueue.setRequest_time(LocalDateTime.now());
        msgQueue.setStat("0");

        log.info("msg_queue 저장 완료");

       msgQueueRepository.save(msgQueue);
    }

//    //SET 쿠폰 알림메시지
//    public String setAlarmMessage(Long alarmNo, String message) {
//        alarmMessage = message;
//        alarmMessageVariableRepository.findAlarmMessageVariableByAlarmNo(alarmNo)
//                .forEach(alarmMessageVariable -> {
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("SN")) {
//                        alarmMessage = alarmMessage.replace("#{매장명}",
//                                customStoreRepository.findById(alarmMessageVariable.getStoreId()).get().getStoreName());
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("GN")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("CN")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("PP")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("PN")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("RP")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("RN")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("2P")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("2D")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("2S")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("4S")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("4P")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("5S")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("5P")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("3E")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("3R")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("CS")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("VS")) {
//                    }
//                    if (alarmMessageVariable.getAlarmVariableCode().equals("PS")) {
//                    }
//                });
//
//        System.out.println(alarmMessage);
//
//        return alarmMessage;
//    }

    //SET 쿠폰발급 알림
    public void sendCouponAlarmMessage(Coupon coupon, MembershipCustomer membershipCustomer, String alarmCode) {
        //안녕하세요  #{수신자명}님.
        //#{매장명}으로부터 쿠폰이 도착했습니다.
        //쿠폰명 : #{쿠폰명}
        //유효기간 만료일 : #{유효기간 만료일}
        //쿠폰은 유효기간 만료일 내 사용하셔야 하며, 기간 만료 시 쿠폰을 사용할 수 없게 됩니다.
        //감사합니다.

        Alarm alarm = getStoreAlarmSetCode(coupon.getStoreId(), alarmCode);

        log.info("매장 알림 정보 : "+alarm);

        if(alarm != null){
            alarmMessage = alarm.getAlarmMessage();

            //알림메시지 set
            alarmMessageVariableRepository.findAlarmMessageVariableByAlarmNo(alarm.getAlarmNo())
                    .forEach(alarmMessageVariable -> {
                        if (alarmMessageVariable.getAlarmVariableCode().equals("SN")) {
                            alarmMessage = alarmMessage.replace("#{매장명}",
                                    customStoreRepository.findById(alarmMessageVariable.getStoreId()).get().getStoreName());
                        }
                        if (alarmMessageVariable.getAlarmVariableCode().equals("CN")) {
                            alarmMessage = alarmMessage.replace("#{쿠폰명}",
                                    customCouponRepository.findById(coupon.getCouponNo()).get().getCouponTitle());
                        }
                        if (alarmMessageVariable.getAlarmVariableCode().equals("3E")) {
                            alarmMessage = alarmMessage.replace("#{유효기간 만료일}",
                                    customCouponRepository.findById(coupon.getCouponNo()).get().getCouponFinishdate().toString());
                        }
                        if (alarmMessageVariable.getAlarmVariableCode().equals("RN")) {
                            if(membershipCustomer.getCustomerName() != null)
                                alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerName());
                            else
                                alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerPhone());
                        }
                    });

            log.info("변환된 알림메시지 : " + alarmMessage);

            //알림발송내역 & 알림발송로그 저장
            AlarmSendHistory alarmSendHistory = new AlarmSendHistory();
            alarmSendHistory.setAlarmSendWayCode(alarm.getAlarmSendWayCode());
            alarmSendHistory.setAlarmSendTypeCode(alarm.getAlarmSendTypeCode());
            alarmSendHistory.setAlarmMessage(alarmMessage);
            alarmSendHistory.setStoreId(alarm.getStoreId());
            saveAlarmHistoryAndLog(membershipCustomer, alarmSendHistory);

            //msg_queue 저장
            saveMsgQueue(alarmMessage, membershipCustomer.getCustomerPhone(), alarm.getAlarmReservationTime());

            log.info("쿠폰 알림 발송 완료");
        }
        else{
            log.info("쿠폰 알림 발송 미설정");
        }

    }

    //SET 혜택적립 알림
    public void sendBenefitAlarmMessage(Coupon coupon, MembershipCustomer membershipCustomer, String alarmCode) {
        //#{매장명}  #{수신자명} 님.
        //도장이 적립되셨습니다.
        //#{적립도장수}개가 적립되셔서
        //현재 적립된 총 도장수는 #{총도장수} 개 입니다.
        //감사합니다.

        Alarm alarm = getStoreAlarmSetCode(coupon.getStoreId(), alarmCode);

        log.info("매장 알림 정보 : "+alarm);

        if(alarm != null){
            alarmMessage = alarm.getAlarmMessage();

            //도장 적립 시
           if(coupon.getCouponTypeNo() == 2){
                //알림메시지 set
                alarmMessageVariableRepository.findAlarmMessageVariableByAlarmNo(alarm.getAlarmNo())
                        .forEach(alarmMessageVariable -> {
                            if (alarmMessageVariable.getAlarmVariableCode().equals("SN")) {
                                alarmMessage = alarmMessage.replace("#{매장명}",
                                        customStoreRepository.findById(alarmMessageVariable.getStoreId()).get().getStoreName());
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("4S")) {
                                alarmMessage = alarmMessage.replace("#{적립도장수}",
                                        String.valueOf(coupon.getCouponBenefitValue()) + "개");
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("AS")) {
                                alarmMessage = alarmMessage.replace("#{총도장수}",
                                        String.valueOf(customCustomerRepository.findById(membershipCustomer.getMembershipCustomerNo()).get().getCustomerBenefitValue()) + "개");
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("RN")) {
                                if(membershipCustomer.getCustomerName() != null)
                                    alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerName());
                                else
                                    alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerPhone());
                            }
                        });
            }
            //포인트 적립 시
            else if(coupon.getCouponTypeNo() == 3){
                //알림메시지 set
                alarmMessageVariableRepository.findAlarmMessageVariableByAlarmNo(alarm.getAlarmNo())
                        .forEach(alarmMessageVariable -> {
                            if (alarmMessageVariable.getAlarmVariableCode().equals("SN")) {
                                alarmMessage = alarmMessage.replace("#{매장명}",
                                        customStoreRepository.findById(alarmMessageVariable.getStoreId()).get().getStoreName());
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("4P")) {
                                alarmMessage = alarmMessage.replace("#{적립포인트}",
                                        String.valueOf(coupon.getCouponBenefitValue()) + "P");
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("AP")) {
                                alarmMessage = alarmMessage.replace("#{총포인트}",
                                        String.valueOf(customCustomerRepository.findById(membershipCustomer.getMembershipCustomerNo()).get().getCustomerBenefitValue()) + "P");
                            }
                            if (alarmMessageVariable.getAlarmVariableCode().equals("RN")) {
                                if(membershipCustomer.getCustomerName() != null)
                                    alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerName());
                                else
                                    alarmMessage = alarmMessage.replace("#{수신자명}", membershipCustomer.getCustomerPhone());
                            }
                        });
            }

            log.info("변환된 알림메시지 : " + alarmMessage);

            //알림발송내역 & 알림발송로그 저장
            AlarmSendHistory alarmSendHistory = new AlarmSendHistory();
            alarmSendHistory.setAlarmSendWayCode(alarm.getAlarmSendWayCode());
            alarmSendHistory.setAlarmSendTypeCode(alarm.getAlarmSendTypeCode());
            alarmSendHistory.setAlarmMessage(alarmMessage);
            alarmSendHistory.setStoreId(alarm.getStoreId());

            saveAlarmHistoryAndLog(membershipCustomer, alarmSendHistory);

            //msg_queue 저장
            saveMsgQueue(alarmMessage, membershipCustomer.getCustomerPhone(), alarm.getAlarmReservationTime());

            log.info("혜택적립 알림 발송 완료");
        }
        else{
            log.info("혜택적립 알림 발송 미설정");
        }

    }

    //SAVE 알림메시지의 알림 변수값
    public void setAlarmMessageVariable(String alarmMessage, Long alarmNo, String storeId) {
        List<AlarmMessageVariable> alarmMessageVariableList = new ArrayList<>();

        if (alarmMessage.contains("#{매장명}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("SN");
            alarmMessageVariable.setAlarmVariableName("#{매장명}");
            alarmMessageVariable.setAlarmVariableDescription("Store Name");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{선물명}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("GN");
            alarmMessageVariable.setAlarmVariableName("#{선물명}");
            alarmMessageVariable.setAlarmVariableDescription("Gift Name");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{쿠폰명}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("CN");
            alarmMessageVariable.setAlarmVariableName("#{쿠폰명}");
            alarmMessageVariable.setAlarmVariableDescription("Coupon Name");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{구매자전화번호}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("PP");
            alarmMessageVariable.setAlarmVariableName("#{구매자전화번호}");
            alarmMessageVariable.setAlarmVariableDescription("Payment Phone");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{구매자명}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("PN");
            alarmMessageVariable.setAlarmVariableName("#{구매자명}");
            alarmMessageVariable.setAlarmVariableDescription("Payment Name");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{수신자전화번호}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("RP");
            alarmMessageVariable.setAlarmVariableName("#{수신자전화번호}");
            alarmMessageVariable.setAlarmVariableDescription("Receive Phone");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{수신자명}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("RN");
            alarmMessageVariable.setAlarmVariableName("#{수신자명}");
            alarmMessageVariable.setAlarmVariableDescription("Receive Name");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{결제금액}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("2P");
            alarmMessageVariable.setAlarmVariableName("#{결제금액}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{결제일시}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("2D");
            alarmMessageVariable.setAlarmVariableName("#{결제일시}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{결제상태}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("2S");
            alarmMessageVariable.setAlarmVariableName("#{결제상태}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{적립도장수}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("4S");
            alarmMessageVariable.setAlarmVariableName("#{적립도장수}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{적립포인트}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("4P");
            alarmMessageVariable.setAlarmVariableName("#{적립포인트}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{차감도장수}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("5S");
            alarmMessageVariable.setAlarmVariableName("#{차감도장수}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{차감포인트}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("5P");
            alarmMessageVariable.setAlarmVariableName("#{차감포인트}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{유효기간 만료일}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("3E");
            alarmMessageVariable.setAlarmVariableName("#{유효기간 만료일}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{남은 유효기간}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("3R");
            alarmMessageVariable.setAlarmVariableName("#{남은 유효기간}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{쿠폰발급 기준 도장개수}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("CS");
            alarmMessageVariable.setAlarmVariableName("#{쿠폰발급 기준 도장개수}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{단골등업 기준 도장개수}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("VS");
            alarmMessageVariable.setAlarmVariableName("#{단골등업 기준 도장개수}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }
        if (alarmMessage.contains("#{포인트 사용기준 포인트}")) {
            AlarmMessageVariable alarmMessageVariable = new AlarmMessageVariable();
            alarmMessageVariable.setAlarmVariableCode("PS");
            alarmMessageVariable.setAlarmVariableName("#{포인트 사용기준 포인트}");
            alarmMessageVariable.setStoreId(storeId);
            alarmMessageVariable.setAlarmNo(alarmNo);

            alarmMessageVariableList.add(alarmMessageVariable);
        }

        //저장
        alarmMessageVariableList.forEach(alarmMessageVariable -> {
            alarmMessageVariableRepository.save(alarmMessageVariable);
        });

        log.info("알림메시지 변수 저장 완료");

    }
}
