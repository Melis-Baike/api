package com.example.api.security;

import com.example.api.model.entity.Customer;
import com.example.api.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Optional<Customer> customerOptional = customerRepository.findByLogin(login);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
        } else {
            throw new UsernameNotFoundException("User with login " + login + " was not found");
        }
    }
}
