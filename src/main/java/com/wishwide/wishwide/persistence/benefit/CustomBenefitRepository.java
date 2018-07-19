package com.wishwide.wishwide.persistence.benefit;

import com.wishwide.wishwide.domain.MembershipCustomer;
import org.springframework.data.repository.CrudRepository;

public interface CustomBenefitRepository extends CrudRepository<MembershipCustomer, Long>, CustomBenefit {

}
