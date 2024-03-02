package com.example.api.controller;

import com.example.api.model.entity.Customer;
import com.example.api.model.request.*;
import com.example.api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @DeleteMapping("/remove-contact-info")
    public ResponseEntity<String> removePhoneNumberAndEmail(Authentication authentication, @Valid @RequestBody ContactInfoRequest request){
        return customerService.removeContactInfo(authentication.getName(), request);
    }

    @PostMapping("/add-contact-info")
    public ResponseEntity<String> addPhoneNumber(Authentication authentication, @Valid @RequestBody ContactInfoRequest request){
        return customerService.addContactInfo(authentication.getName(), request);
    }

    @PostMapping("/change-contact-info")
    public ResponseEntity<String> changePhoneNumber(Authentication authentication, @Valid @RequestBody ContactInfoRequest request){
        return customerService.changeContactInfo(authentication.getName(), request);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request){
        LocalDate localDate = LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        if(localDate.isAfter(LocalDate.now())){
            return ResponseEntity.badRequest().build();
        }
        return customerService.registerCustomer(request);
    }

    @PostMapping("/ledgerAccount/transfer")
    public ResponseEntity<String> transferMoney(Authentication authentication, @Valid @RequestBody TransferRequest request){
        return customerService.transferMoney(authentication.getName(), request);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Customer>> searchCustomer(
            @Valid @RequestBody SearchRequest request){
        return customerService.searchCustomers(request);
    }
}
