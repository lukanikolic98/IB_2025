package com.ftn.pki.controller;

import com.ftn.pki.dto.JwtResponse;
import com.ftn.pki.dto.LoginRequest;
import com.ftn.pki.dto.LoginResponse;
import com.ftn.pki.dto.RegisterRequest;
import com.ftn.pki.model.User;
import com.ftn.pki.model.VerificationToken;
import com.ftn.pki.repository.UserRepository;
import com.ftn.pki.service.AuthService;
import com.ftn.pki.service.CustomUserDetailsService;
import com.ftn.pki.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthService authService;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private JwtService jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

    if (optionalUser.isPresent()) {
      User user = optionalUser.get();

      if (!user.isActivated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Account is not verified. Please check your email for the activation link.");
      }

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String accessToken = jwtUtil.generateToken(userDetails, (long) 5 * 60 * 1000); // 5 min
      String refreshToken = jwtUtil.generateTokenFromUsername(userDetails.getUsername(), 7 * 24 * 60 * 60 * 1000); // 7
                                                                                                                   // dana

      // kreira JwtResponse sa svim podacima
      return ResponseEntity.ok(new LoginResponse(
          accessToken,
          refreshToken,
          user.getEmail(),
          user.getFirstname(),
          user.getLastname(),
          user.getRole()));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found after successful authentication.");
    }
  }

  @GetMapping("/confirm-registration/{token}")
  public ResponseEntity<?> confirmRegistration(@PathVariable String token) {
    try {
      authService.verifyToken(token);
      return ResponseEntity.ok(Map.of("message", "Activation successful"));
    } catch (Exception e) {

      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

    try {
      if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("message",
            "Email is already taken"));
      }
      VerificationToken token = authService.registerRegularUser(registerRequest);
      return ResponseEntity
          .ok(Map.of("message", "User registered successfully", "activationToken",
              token.getToken()));

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
    String username = null;
    try {
      username = jwtUtil.extractUsername(refreshToken);
    } catch (Exception e) {
      // Greska pri parsiranju tokena
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
    }

    if (username != null) {
      Optional<User> optionalUser = userRepository.findByEmail(username);

      if (optionalUser.isPresent()) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // pozovi validateToken sa oba argumenta
        if (jwtUtil.validateToken(refreshToken, userDetails)) {
          User user = optionalUser.get();
          String newAccessToken = jwtUtil.generateTokenFromUsername(username, 5 * 60 * 1000); // 5 min

          return ResponseEntity.ok(new JwtResponse(
              newAccessToken,
              refreshToken,
              user.getEmail(),
              user.getFirstname(),
              user.getLastname(),
              user.getRole()));
        }
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found for this refresh token.");
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
  }
}
