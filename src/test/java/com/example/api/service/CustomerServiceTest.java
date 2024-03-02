package com.example.api.service;

import com.example.api.model.entity.Customer;
import com.example.api.model.entity.Email;
import com.example.api.model.entity.LedgerAccount;
import com.example.api.model.entity.PhoneNumber;
import com.example.api.model.enumerated.Role;
import com.example.api.model.request.TransferRequest;
import com.example.api.repository.CustomerRepository;
import com.example.api.repository.LedgerAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerServiceTest {
    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private LedgerAccountRepository ledgerAccountRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void transferMoney_ValidData_TransferSuccessful(){
        String login = "mylogin";

        LedgerAccount ledgerAccount = createMainLedgerAccount();
        LedgerAccount recipientLedgerAccount = createRecipientAccount();
        PhoneNumber phoneNumber = createPhoneNumber();
        Email email = createEmail();
        Customer customer = createCustomer(login);
        customer.setEmailList(List.of(email));
        customer.setLedgerAccount(ledgerAccount);
        customer.setPhoneNumberList(List.of(phoneNumber));

        TransferRequest request = TransferRequest.builder()
                .accountNumber(1111222233334444L)
                .amount(10D)
                .build();

        when(customerRepository.findByLogin(login)).thenReturn(Optional.of(customer));
        when(ledgerAccountRepository.findLedgerAccountByCustomer(customer)).thenReturn(Optional.of(ledgerAccount));
        when(ledgerAccountRepository.findLedgerAccountByAccountNumber(request.getAccountNumber())).thenReturn(Optional.of(recipientLedgerAccount));

        ResponseEntity<String> response = customerService.transferMoney(login, request);
        int expectedStatusCode = 200;
        assertEquals(expectedStatusCode, response.getStatusCode().value());
        verify(ledgerAccountRepository).updateAmountByCustomer(anyDouble(), eq(customer));
        verify(ledgerAccountRepository).updateAmountByAccountNumber(anyDouble(), eq(request.getAccountNumber()));
    }

    @Test
    public void transferMoney_NoValidData_AccountNumberNotFound() {
        String login = "mylogin";

        LedgerAccount ledgerAccount = createMainLedgerAccount();
        PhoneNumber phoneNumber = createPhoneNumber();
        Email email = createEmail();
        Customer customer = createCustomer(login);
        customer.setEmailList(List.of(email));
        customer.setLedgerAccount(ledgerAccount);
        customer.setPhoneNumberList(List.of(phoneNumber));

        TransferRequest request = TransferRequest.builder()
                .accountNumber(1111222233334444L)
                .amount(10D)
                .build();

        when(customerRepository.findByLogin(login)).thenReturn(Optional.of(customer));
        when(ledgerAccountRepository.findLedgerAccountByCustomer(customer)).thenReturn(Optional.of(ledgerAccount));
        when(ledgerAccountRepository.findLedgerAccountByAccountNumber(request.getAccountNumber())).thenReturn(Optional.empty());

        ResponseEntity<String> response = customerService.transferMoney(login, request);
        int expectedCode = 400;
        assertEquals(expectedCode, response.getStatusCode().value());
        verify(ledgerAccountRepository, never()).updateAmountByCustomer(any(), any());
        verify(ledgerAccountRepository, never()).updateAmountByAccountNumber(any(), any());
    }

    @Test
    public void transferMoney_InsufficientFunds_TransferFailed() {
        String login = "mylogin";

        LedgerAccount ledgerAccount = createMainLedgerAccount();
        LedgerAccount recipientLedgerAccount = createRecipientAccount();
        PhoneNumber phoneNumber = createPhoneNumber();
        Email email = createEmail();
        Customer customer = createCustomer(login);
        customer.setEmailList(List.of(email));
        customer.setLedgerAccount(ledgerAccount);
        customer.setPhoneNumberList(List.of(phoneNumber));

        TransferRequest request = TransferRequest.builder()
                .accountNumber(1111222233334444L)
                .amount(1000D)
                .build();

        when(customerRepository.findByLogin(login)).thenReturn(Optional.of(customer));
        when(ledgerAccountRepository.findLedgerAccountByCustomer(customer)).thenReturn(Optional.of(ledgerAccount));
        when(ledgerAccountRepository.findLedgerAccountByAccountNumber(request.getAccountNumber())).thenReturn(Optional.of(recipientLedgerAccount));

        ResponseEntity<String> response = customerService.transferMoney(login, request);
        int expectedCode = 400;
        assertEquals(expectedCode, response.getStatusCode().value());
        verify(ledgerAccountRepository, never()).updateAmountByCustomer(any(), any());
        verify(ledgerAccountRepository, never()).updateAmountByAccountNumber(any(), any());
    }

    private LedgerAccount createMainLedgerAccount(){
        return LedgerAccount.builder()
                .accountNumber(1234567887654321L)
                .initialAmount(100D)
                .amount(100D)
                .build();
    }

    private LedgerAccount createRecipientAccount(){
        return LedgerAccount.builder()
                .accountNumber(1111222233334444L)
                .initialAmount(50D)
                .amount(50D)
                .build();
    }

    private Customer createCustomer(String login){
        return Customer.builder()
                .id(1L)
                .login(login)
                .fullName("Test Test")
                .password(passwordEncoder.encode("test"))
                .dateOfBirth(LocalDate.now())
                .role(Role.ROLE_CUSTOMER)
                .build();
    }

    private PhoneNumber createPhoneNumber(){
        return PhoneNumber.builder()
                .value("+71233334444")
                .build();
    }

    private Email createEmail(){
        return Email.builder()
                .value("test@gmail.com")
                .build();
    }
}
