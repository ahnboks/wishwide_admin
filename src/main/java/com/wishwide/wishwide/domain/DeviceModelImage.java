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
@Entity
@Table(name = "tb_device_model_image")
@EqualsAndHashCode(of = "deviceModelImageNo")
@ToString
public class DeviceModelImage {
    //디바이스 모델 이미지 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false, name = "device_model_image_no")
    private Long deviceModelImageNo;

    @Column(length = 20, nullable = false, name = "device_model_no")
    private Long deviceModelNo;

    //디바이스 모델 이미지 타입 코드
    @Column(length = 10, nullable = false, name = "device_model_image_type_code")
    private String deviceModelImageTypeCode;

    //디바이스 모델 이미지 DB저장명
    @Column(length = 4000, nullable = false, name = "device_model_image_db_name")
    private String deviceModelImageDbName;

    //디바이스 모델 이미지명
    @Column(length = 4000, nullable = false, name = "device_model_image_name")
    private String deviceModelImageName;

    //디바이스 모델 이미지 데이터타입명
    @Column(length = 40, nullable = false, name = "device_model_image_extension")
    private String deviceModelImageExtension;

    //디바이스 모델 이미지 파일크기
    @Column(length = 10, nullable = false,  name = "device_model_image_size", columnDefinition = "int default 0")
    private int deviceModelImageSize;

    //디바이스 모델 이미지 파일주소
    @Column(length = 400, nullable = false, name = "device_model_image_url")
    private String deviceModelImageUrl;

    //디바이스 모델 이미지 썸네일파일명
    @Column(length = 400, nullable = false,  name = "device_model_image_thumbnailName")
    private String deviceModelImageThumbnailName;

    //디바이스 모델 이미지 썸네일파일주소
    @Column(length = 400, nullable = false,  name = "device_model_image_thumbnailUrl")
    private String deviceModelImageThumbnailUrl;

    //디바이스 모델 이미지 등록일시
    @CreationTimestamp
    @Column(nullable = false, name = "device_model_image_regdate")
    private LocalDateTime deviceModelImageRegdate;

    //디바이스 모델 이미지 수정일시
    @UpdateTimestamp
    @Column(nullable = false, name = "device_model_image_updatedate")
    private LocalDateTime deviceModelImageUpdatedate;
}
