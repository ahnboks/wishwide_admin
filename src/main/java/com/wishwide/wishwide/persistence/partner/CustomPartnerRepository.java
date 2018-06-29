package com.wishwide.wishwide.persistence.partner;

import com.wishwide.wishwide.domain.Partner;
import com.wishwide.wishwide.domain.Store;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomPartnerRepository extends CrudRepository<Partner, String>, CustomPartner {

}
