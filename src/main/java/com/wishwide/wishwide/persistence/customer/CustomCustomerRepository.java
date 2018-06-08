package com.wishwide.wishwide.persistence.customer;

import com.wishwide.wishwide.domain.Customer;
import com.wishwide.wishwide.domain.Device;
import org.springframework.data.repository.CrudRepository;

public interface CustomCustomerRepository extends CrudRepository<Customer, Long>, CustomCustomer {


}
