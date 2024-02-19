package com.spring.ready.com.service;

import com.spring.ready.com.Config.JwtService;
import com.spring.ready.com.controllers.AuthenticationRequest;
import com.spring.ready.com.controllers.AuthenticationResponse;
import com.spring.ready.com.controllers.RegisterRequest;
import com.spring.ready.com.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepo userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }
}