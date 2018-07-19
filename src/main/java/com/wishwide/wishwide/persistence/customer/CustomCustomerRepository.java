package com.wishwide.wishwide.persistence.customer;

import com.wishwide.wishwide.domain.MembershipCustomer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomCustomerRepository extends CrudRepository<MembershipCustomer, Long>, CustomCustomer {
    @Query(value = "select c from MembershipCustomer c where c.storeId =?1")
    public List<MembershipCustomer> findCustomerByStoreId(String storeId);

    @Query(value = "select c from MembershipCustomer c where c.storeId =?1 and c.customerGradeTypeCode = 'VIP'")
    public List<MembershipCustomer> findVIPCustomerByStoreId(String storeId);

    @Modifying
    @Query(value = "UPDATE MembershipCustomer c SET c.customerBenefitValue = ?1 WHERE c.membershipCustomerNo = ?2")
    public void changeCustomerBenefitValue(int benefitValue, Long membershipCustomerNo);
}
