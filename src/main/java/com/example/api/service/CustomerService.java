package com.example.api.service;

import com.example.api.model.entity.Customer;
import com.example.api.model.entity.Email;
import com.example.api.model.entity.LedgerAccount;
import com.example.api.model.entity.PhoneNumber;
import com.example.api.model.enumerated.MethodOfContactInfo;
import com.example.api.model.enumerated.Role;
import com.example.api.model.request.ContactInfoRequest;
import com.example.api.model.request.CustomerRegistrationRequest;
import com.example.api.model.request.SearchRequest;
import com.example.api.model.request.TransferRequest;
import com.example.api.repository.CustomerRepository;
import com.example.api.repository.EmailRepository;
import com.example.api.repository.LedgerAccountRepository;
import com.example.api.repository.PhoneNumberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final EmailRepository emailRepository;
    private final LedgerAccountRepository ledgerAccountRepository;
    private final PasswordEncoder passwordEncoder;

    private static final double LIMIT_PERCENTAGE = 207.0;
    private static final double MAX_PERCENT_VALUE = 100.0;
    private static final double INCREASE_PERCENTAGE = 5.0;

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    public ResponseEntity<String> registerCustomer(CustomerRegistrationRequest request){
        if(!alreadyExistsLogin(request.getLogin()) && !alreadyExistsEmail(request.getEmail()) && !alreadyExistsPhoneNumber(request.getPhoneNumber())){
            PhoneNumber phoneNumber = PhoneNumber.builder()
                    .value(request.getPhoneNumber())
                    .build();
            Email email = Email.builder()
                    .value(request.getEmail())
                    .build();
            Long accountNumber = generateAccountNumber();
            LedgerAccount ledgerAccount = LedgerAccount.builder()
                    .accountNumber(accountNumber)
                    .initialAmount(request.getInitialAmount())
                    .amount(request.getInitialAmount())
                    .build();
            Customer customer = Customer.builder()
                    .login(request.getLogin())
                    .fullName(request.getFullName())
                    .dateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                    .phoneNumberList(List.of(phoneNumber))
                    .emailList(List.of(email))
                    .password(passwordEncoder.encode(request.getPassword()))
                    .ledgerAccount(ledgerAccount)
                    .role(Role.ROLE_CUSTOMER)
                    .build();

            phoneNumber.setCustomer(customer);
            email.setCustomer(customer);
            ledgerAccount.setCustomer(customer);
            customerRepository.save(customer);
            logger.info("Customer " + customer.getLogin() + " has been registered");

            return new ResponseEntity<>("You have successfully created customer", HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<String> removeContactInfo(String login, ContactInfoRequest request){
        return switchMethodsOfContactInfo(request, login, MethodOfContactInfo.REMOVE);
    }

    private boolean allFieldsNull(ContactInfoRequest request){
        return Stream.of(request.getPhoneNumber(), request.getNewPhoneNumber(), request.getEmail(),
                request.getNewEmail()).allMatch(Objects::isNull);
    }

    public ResponseEntity<String> addContactInfo(String login, ContactInfoRequest request){
        return switchMethodsOfContactInfo(request, login, MethodOfContactInfo.ADD);
    }

    public ResponseEntity<String> changeContactInfo(String login, ContactInfoRequest request){
        return switchMethodsOfContactInfo(request, login, MethodOfContactInfo.CHANGE);
    }

    public ResponseEntity<String> switchMethodsOfContactInfo(ContactInfoRequest request, String login, MethodOfContactInfo method) {
        Optional<Customer> customer = customerRepository.findByLogin(login);
        if (!allFieldsNull(request) && customer.isPresent()) {
            String phoneNumber = request.getPhoneNumber();
            String email = request.getEmail();
            String newPhoneNumber = request.getNewPhoneNumber();
            String newEmail = request.getNewEmail();
            String response = "";
            switch (method) {
                case ADD -> {
                    boolean addedPhoneNumber = false;
                    if(newPhoneNumber != null && !newPhoneNumber.isBlank() && !alreadyExistsPhoneNumber(newPhoneNumber)) {
                        phoneNumberRepository.save(PhoneNumber.builder()
                                .customer(customer.get())
                                .value(newPhoneNumber)
                                .build());
                        addedPhoneNumber = true;
                        response = "You have successfully added new phone number";
                        logger.info(customer.get().getLogin() + " added new phone number " + newPhoneNumber);
                    }
                    if(newEmail != null && !newEmail.isBlank() && !alreadyExistsEmail(newEmail)) {
                        emailRepository.save(Email.builder()
                                .customer(customer.get())
                                .value(newEmail)
                                .build());
                        response += (addedPhoneNumber ? " and email" : "You have successfully added a new email");
                        logger.info(customer.get().getLogin() + " added new email: " + newEmail);
                    }
                    if(response.isEmpty()){
                        logger.error(customer.get().getLogin() + " entered null values (newPhoneNumber, newEmail)");
                        return ResponseEntity.noContent().build();
                    }
                    return new ResponseEntity<>(response, HttpStatus.CREATED);

                }
                case CHANGE -> {
                    boolean updatedPhoneNumber = false;
                    if(phoneNumber != null && !phoneNumber.isBlank() && alreadyExistsPhoneNumberByCustomer(customer.get(), phoneNumber) &&
                        !alreadyExistsPhoneNumber(newPhoneNumber) && newPhoneNumber != null && !newPhoneNumber.isBlank()) {
                        String message = "phone number from " + phoneNumber + " to " + newPhoneNumber;
                        phoneNumberRepository.update(newPhoneNumber, customer.get(), phoneNumber);
                        updatedPhoneNumber = true;
                        response = "You have successfully updated " + message;
                        logger.info("Customer " + customer.get().getLogin() + " changed " + message);
                    }
                    if(email != null && !email.isBlank() && alreadyExistsEmailByCustomer(customer.get(), email) && !alreadyExistsEmail(newEmail)
                     && newEmail != null && !newEmail.isBlank()){
                        String message = "email from " + email + " to " + newEmail;
                        emailRepository.update(newEmail, customer.get(), email);
                        response += updatedPhoneNumber ? " also " + message:
                                "You have successfully updated " + message;
                        logger.info("Customer " + customer.get().getLogin() + " changed " + message);
                    }
                    if(response.isEmpty()){
                        return ResponseEntity.noContent().build();
                    }
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                case REMOVE -> {
                    List<PhoneNumber> customersPhoneNumberList = phoneNumberRepository.findAllByCustomer(customer.get());
                    List<Email> customersEmailList = emailRepository.findAllByCustomer(customer.get());
                    boolean removedPhoneNumber = false;
                    String responseOfLimit = "";
                    boolean phoneLimit = false;
                    boolean emailLimit = false;
                    if (phoneNumber != null && !phoneNumber.isBlank() && alreadyExistsPhoneNumberByCustomer(customer.get(), phoneNumber)) {
                        if(customersPhoneNumberList.size() > 1) {
                            phoneNumberRepository.deletePhoneNumberByCustomerAndValue(customer.get(), phoneNumber);
                            removedPhoneNumber = true;
                            response = "You have successfully removed phone number: " + phoneNumber;
                            logger.info(customer.get().getLogin() + " removed phone number " + phoneNumber);
                        } else {
                            responseOfLimit = "You can't remove last phone number";
                            phoneLimit = true;
                        }
                    }
                    if (email != null && !email.isBlank() && alreadyExistsEmailByCustomer(customer.get(), email)) {
                        if(customersEmailList.size() > 1) {
                            String message = "email: " + email;
                            emailRepository.deleteEmailByCustomerAndValue(customer.get(), email);
                            response += removedPhoneNumber ? ", and " + message :
                                    "You have successfully removed " + message;
                            logger.info(customer.get().getLogin() + " removed email " + email);
                        } else {
                            responseOfLimit += phoneLimit ? " and email" : "You can't remove last email";
                            emailLimit = true;
                        }
                    }
                    if(!responseOfLimit.isEmpty() && phoneLimit && emailLimit){
                        return new ResponseEntity<>(responseOfLimit, HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                    if(response.isEmpty()){
                        logger.error(customer.get().getLogin() + " entered null values (phoneNumber, email)");
                        return ResponseEntity.noContent().build();
                    }
                    response += "\n" + responseOfLimit;
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        return ResponseEntity.noContent().build();
    }

    private boolean alreadyExistsLogin(String login){
        return customerRepository.existsByLogin(login);
    }

    private boolean alreadyExistsPhoneNumber(String phoneNumber){
        return phoneNumberRepository.existsPhoneNumberByValue(phoneNumber);
    }

    private boolean alreadyExistsEmail(String email){
        return emailRepository.existsEmailByValue(email);
    }

    private boolean alreadyExistsPhoneNumberByCustomer(Customer customer, String phoneNumber){
        return phoneNumberRepository.existsPhoneNumberByCustomerAndValue(customer, phoneNumber);
    }

    private boolean alreadyExistsEmailByCustomer(Customer customer, String email){
        return emailRepository.existsEmailByCustomerAndValue(customer, email);
    }

    private boolean alreadyExistsLedgerAccountByAccountNumber(Long accountNumber){
        return ledgerAccountRepository.existsLedgerAccountByAccountNumber(accountNumber);
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void increaseAmounts() {
        synchronized (customerRepository.findAll()) {
            for (Customer customer : customerRepository.findAll()) {
                double limitAmount = (LIMIT_PERCENTAGE - INCREASE_PERCENTAGE) * customer.getLedgerAccount().getInitialAmount() / MAX_PERCENT_VALUE;
                if(customer.getLedgerAccount().getAmount() < limitAmount) {
                    double amount = customer.getLedgerAccount().getAmount() + (INCREASE_PERCENTAGE * customer.getLedgerAccount().getInitialAmount() / MAX_PERCENT_VALUE);
                    customer.getLedgerAccount().setAmount(amount);
                    ledgerAccountRepository.updateAmountByCustomer(customer.getLedgerAccount().getAmount(), customer);
                }
            }
            logger.info("Customer ledger account's value has been increased");
        }
    }

    public ResponseEntity<Page<Customer>> searchCustomers(SearchRequest request){
        return new ResponseEntity<>(customerRepository.searchByDateOfBirthAndPhoneNumberAndFullNameAndEmail(Optional.ofNullable(
                        request.getDateOfBirth() == null || request.getDateOfBirth().isBlank() ? null :
                LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))),
                Optional.ofNullable(request.getPhoneNumber()), Optional.ofNullable(request.getFullName()),
                Optional.ofNullable(request.getEmail()),
                request.getPageable() != null ? PageRequest.of(request.getPageable().getPage(), request.getPageable().getSize())
                : PageRequest.of(0, 5)), HttpStatus.OK);
    }

    public ResponseEntity<String> transferMoney(String login, TransferRequest request){
        Optional<Customer> customer = customerRepository.findByLogin(login);
        if(customer.isPresent()) {
            Optional<LedgerAccount> ledgerAccount = ledgerAccountRepository.findLedgerAccountByCustomer(customer.get());
            Optional<LedgerAccount> secondLedgerAccount = ledgerAccountRepository.findLedgerAccountByAccountNumber(request.getAccountNumber());
            if(ledgerAccount.isPresent() && secondLedgerAccount.isPresent() && ledgerAccount.get().getAmount() >= request.getAmount()) {
                ledgerAccount.get().setAmount(ledgerAccount.get().getAmount() - request.getAmount());
                ledgerAccountRepository.updateAmountByCustomer(ledgerAccount.get().getAmount(), customer.get());
                secondLedgerAccount.get().setAmount(secondLedgerAccount.get().getAmount() + request.getAmount());
                ledgerAccountRepository.updateAmountByAccountNumber(secondLedgerAccount.get().getAmount(), request.getAccountNumber());
                logger.info("Customer " + customer.get().getLogin() + " transferred " + request.getAmount() + " to " + request.getAccountNumber());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    private long generateAccountNumber() {
        long accountNumber;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        do {
            for (int i = 0; i < 16; i++) {
                int digit = random.nextInt(10);
                sb.append(digit);
            }
            accountNumber = Long.parseLong(sb.toString());
        } while (alreadyExistsLedgerAccountByAccountNumber(accountNumber));

        return accountNumber;
    }

    public Customer getByUsername(String login) {
        return customerRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

    }
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}
