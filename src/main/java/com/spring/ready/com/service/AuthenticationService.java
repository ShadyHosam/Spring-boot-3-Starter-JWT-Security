package com.spring.ready.com.service;

import com.spring.ready.com.Config.JwtService;
import com.spring.ready.com.Constants.ResponseConstants;
import com.spring.ready.com.controllers.AuthenticationRequest;
import com.spring.ready.com.controllers.AuthenticationResponse;
import com.spring.ready.com.controllers.RegisterRequest;
import com.spring.ready.com.entities.User;
import com.spring.ready.com.repositories.UserRepo;
import com.spring.ready.com.util.Role;
import com.spring.ready.com.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jshell.execution.Util;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepo userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    Role role;

    public ResponseEntity<String>register(RegisterRequest request) {
        // first thing check if the request is valid or not !!
        try {
            if (validateSignupRequest(request)) {
                // if the request is valid then lets check if the user already exists or not
                Optional<User> user = userRepository.findByEmail(request.getEmail());
                if (user.isEmpty()) {
                    userRepository.save(getUserFromRequest(request));
                    var validUser = userRepository.findByEmail(request.getEmail()).get();
                    var jwtToken = jwtService.generateToken(validUser);

                   // System.out.println(jwtToken);
                    return Utils.getResponseEntity(ResponseConstants.USER_SIGNUP_SUCCESS+ " " + jwtToken, HttpStatus.OK);
                } else {
                    return Utils.getResponseEntity(ResponseConstants.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(ResponseConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(ResponseConstants.SOME_THING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ResponseEntity<String> login(AuthenticationRequest request) {
        System.out.println(request.getEmail());
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail()).get();
            var jwtToken = jwtService.generateToken(user);
            System.out.println(user);

            return ResponseEntity.ok(jwtToken);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }


    private boolean validateSignupRequest(RegisterRequest request){
        return request.getFirstname() != null && request.getLastname() != null && request.getEmail() != null && request.getPassword() != null;
    }
    private User getUserFromRequest(RegisterRequest request){
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setContactNumber(request.getContactNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        return user;
    }
}