package com.example.api.repository;

import com.example.api.model.entity.Customer;
import com.example.api.model.entity.LedgerAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {
    Optional<LedgerAccount> findLedgerAccountByCustomer(Customer customer);

    Optional<LedgerAccount> findLedgerAccountByAccountNumber(Long accountNumber);

    boolean existsLedgerAccountByAccountNumber(Long accountNumber);

    @Modifying
    @Transactional
    @Query("UPDATE LedgerAccount l SET l.amount = :amount WHERE l.customer = :customer")
    void updateAmountByCustomer(Double amount, Customer customer);

    @Modifying
    @Transactional
    @Query("UPDATE LedgerAccount l SET l.amount = :amount WHERE l.accountNumber = :accountNumber")
    void updateAmountByAccountNumber(Double amount, Long accountNumber);
}
