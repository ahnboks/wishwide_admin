package com.wishwide.wishwide.persistence.device;

import com.wishwide.wishwide.domain.DeviceImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeviceImageRepository extends CrudRepository<DeviceImage, Long> {
    @Query(value = "select d from DeviceImage d where d.deviceNo =?1")
    public DeviceImage findByDeviceImageAndDeviceNo(Long deviceNo);
}
