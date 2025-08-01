package com.scaleorange.laptoprental.controller;

import com.scaleorange.laptoprental.dto.LoginDto;
import com.scaleorange.laptoprental.dto.RegisterDto;
import com.scaleorange.laptoprental.dto.ResetPasswordDto;
import com.scaleorange.laptoprental.dto.ResponseDto;
import com.scaleorange.laptoprental.entity.User;
import com.scaleorange.laptoprental.repo.UserRepo;
import com.scaleorange.laptoprental.service.JwtService;
import com.scaleorange.laptoprental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto register) {
        try {
            User user = new User();
            user.setName(register.getName());
            user.setEmail(register.getEmail());
            user.setPassword(register.getPassword());
            user.setPhoneno(register.getPhoneno());
            user.setRole(register.getRole()); // ✅ assign role

            service.registerUser(user);
            return new ResponseEntity<>("Registered Successfully", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto login) {
        ResponseDto response = new ResponseDto();
        try {
            Authentication authentication = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(login.getEmail());
                response.setToken(token);
                response.setIsLogged("yes");
            }
        } catch (Exception e) {
            response.setToken(null);
            response.setIsLogged("no");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
//        Optional<User> optionalUser = userRepo.findByResetToken(request.getToken());
//
//        if (optionalUser.isEmpty()) {
//            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
//        }
//
//        User user = optionalUser.get();
//        user.setPassword(passwordEncoder.encode(request.getNewPassword())); // Use your existing encoder
//        user.setResetToken(null); // Clear token after successful reset
//        userRepo.save(user);
//
//        return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String token = userService.generateResetToken(request.get("email"));
        return ResponseEntity.ok("Reset token: " + token);
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
//        userService.resetPassword(request.getToken(), request.getNewPassword());
//        return ResponseEntity.ok("Password has been reset.");
//    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
        try {
            String message = userService.resetPassword(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
