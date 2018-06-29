package com.wishwide.wishwide.persistence.ar;

import com.wishwide.wishwide.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomMarkerRepository extends CrudRepository<Customer, Long>, CustomMarker {

}
