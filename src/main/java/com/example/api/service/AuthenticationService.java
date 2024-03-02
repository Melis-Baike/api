package com.example.api.service;

import com.example.api.model.request.CustomerSignInRequest;
import com.example.api.model.response.JwtAuthenticationResponse;
import com.example.api.security.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final CustomerService customerService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    public AuthenticationService(@Lazy CustomerService customerService, JwtService jwtService,
                                 AuthenticationManager authenticationManager){
        this.customerService = customerService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthenticationResponse signIn(CustomerSignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));

        UserDetails customer = customerService
                .userDetailsService()
                .loadUserByUsername(request.getLogin());

        logger.info(request.getLogin() + " has signed in");

        var jwt = jwtService.generateToken(customer);
        return new JwtAuthenticationResponse(jwt);
    }
}