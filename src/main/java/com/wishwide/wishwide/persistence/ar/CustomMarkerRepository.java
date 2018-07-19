package com.wishwide.wishwide.persistence.ar;

import com.wishwide.wishwide.domain.MembershipCustomer;
import org.springframework.data.repository.CrudRepository;

public interface CustomMarkerRepository extends CrudRepository<MembershipCustomer, Long>, CustomMarker {

}
