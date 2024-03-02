package com.example.api.repository;

import com.example.api.model.entity.Customer;
import com.example.api.model.entity.PhoneNumber;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM PhoneNumber p WHERE p.customer = :customer and p.value = :value")
    void deletePhoneNumberByCustomerAndValue(Customer customer, String value);

    @Modifying
    @Transactional
    @Query("UPDATE PhoneNumber p SET p.value = :value WHERE p.customer = :customer and  p.value = :oldValue")
    void update(String value, Customer customer, String oldValue);

    boolean existsPhoneNumberByValue(String value);

    boolean existsPhoneNumberByCustomerAndValue(Customer customer, String value);

    List<PhoneNumber> findAllByCustomer(Customer customer);
}
