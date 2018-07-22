package com.wishwide.wishwide.persistence.device;

import com.wishwide.wishwide.domain.Device;
import com.wishwide.wishwide.domain.Store;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomDeviceRepository extends CrudRepository<Device, Long>, CustomDevice {
    @Modifying
    @Query(value = "UPDATE Device d SET d.deviceVisibleCode = ?1 WHERE d.storeId = ?2")
    public void changeDeviceVisibleCode(int visibleCode, String storeId);

    @Query(value = "select count(d.deviceNo) from Device d")
    public int findByStoreDeviceCnt();

    public Device findTop1ByOrderByDeviceNoDesc();
}
