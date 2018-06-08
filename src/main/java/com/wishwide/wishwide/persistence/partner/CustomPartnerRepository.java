package com.wishwide.wishwide.persistence.partner;

import com.wishwide.wishwide.domain.Store;
import org.springframework.data.repository.CrudRepository;

public interface CustomPartnerRepository extends CrudRepository<Store, String>, CustomPartner {
}
