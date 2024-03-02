package com.example.api.repository;

import com.example.api.model.entity.Customer;
import com.example.api.model.entity.Email;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Email e WHERE e.customer = :customer and e.value = :value")
    void deleteEmailByCustomerAndValue(Customer customer, String value);

    @Modifying
    @Transactional
    @Query("UPDATE Email e SET e.value = :value WHERE e.customer = :customer and e.value = :oldValue")
    void update(String value, Customer customer, String oldValue);

    boolean existsEmailByValue(String value);

    boolean existsEmailByCustomerAndValue(Customer customer, String value);

    List<Email> findAllByCustomer(Customer customer);
}
