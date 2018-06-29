package com.wishwide.wishwide.persistence.device;

import com.wishwide.wishwide.domain.DeviceModel;
import com.wishwide.wishwide.domain.MajorCategory;
import org.springframework.data.repository.CrudRepository;

public interface CustomDeviceModelRepository extends CrudRepository<DeviceModel, Long>, CustomDeviceModel {
}
