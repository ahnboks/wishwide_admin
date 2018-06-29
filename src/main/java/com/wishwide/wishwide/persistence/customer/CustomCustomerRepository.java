package com.wishwide.wishwide.persistence.customer;

import com.wishwide.wishwide.domain.Customer;
import com.wishwide.wishwide.domain.MarkerDataFile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomCustomerRepository extends CrudRepository<Customer, Long>, CustomCustomer {
    @Query(value = "select c from Customer c where c.storeId =?1")
    public List<Customer> findCustomerByStoreId(String storeId);

    @Query(value = "select c from Customer c where c.storeId =?1 and c.customerGradeTypeCode = 'VIP'")
    public List<Customer> findVIPCustomerByStoreId(String storeId);

    @Modifying
    @Query(value = "UPDATE Customer c SET c.customerBenefitValue = ?1 WHERE c.customerNo = ?2")
    public void changeCustomerBenefitValue(int benefitValue, Long customerNo);
}
