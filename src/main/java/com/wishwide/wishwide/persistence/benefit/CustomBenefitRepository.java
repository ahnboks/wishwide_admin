package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomBenefitRepository extends CrudRepository<Customer, Long>, CustomBenefit {

}
