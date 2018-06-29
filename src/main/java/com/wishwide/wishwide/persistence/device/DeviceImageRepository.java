package com.wishwide.wishwide.persistence.device;

import com.wishwide.wishwide.domain.DeviceImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeviceImageRepository extends CrudRepository<DeviceImage, Long> {
    @Query(value = "select d from DeviceImage d where d.storeId =?1 and d.deviceImageTypeCode = ?2")
    public DeviceImage findByDeviceImageAndDeviceImageTypeCode(String storeId, String deviceImageTypeCode);
}
