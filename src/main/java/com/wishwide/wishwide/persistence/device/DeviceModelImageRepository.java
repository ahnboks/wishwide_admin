package com.wishwide.wishwide.persistence.device;

import com.wishwide.wishwide.domain.DeviceImage;
import com.wishwide.wishwide.domain.DeviceModelImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeviceModelImageRepository extends CrudRepository<DeviceModelImage, Long> {
    @Query(value = "select dm from DeviceModelImage dm where dm.deviceModelNo =?1")
    public DeviceModelImage findByDeviceModelImageAndDeviceModelNo(Long deviceModelNo);
}
