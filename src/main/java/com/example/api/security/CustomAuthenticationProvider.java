package com.example.api.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customerService;

    public CustomAuthenticationProvider(CustomUserDetailsService customerService) {
        this.customerService = customerService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        UserDetails customer = customerService.loadUserByUsername(login);
        if (authentication.getCredentials().equals(customer.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    customer,
                    customer.getPassword(),
                    customer.getAuthorities()
            );
        }
        throw new BadCredentialsException("Invalid login or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}