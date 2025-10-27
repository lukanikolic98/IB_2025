package com.ftn.pki.controller;

import com.ftn.pki.dto.LoginRequest;
import com.ftn.pki.dto.LoginResponse;
import com.ftn.pki.dto.RegisterRequest;
import com.ftn.pki.model.User;
import com.ftn.pki.repository.UserRepository;
import com.ftn.pki.service.AuthService;
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userService;

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtService jwtUtil;

  /*
   * @PostMapping("/login")
   * public ResponseEntity<?> login(@RequestBody LoginRequest authRequest,
   * HttpServletResponse response) {
   * try {
   * Authentication auth = authenticationManager.authenticate(
   * new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
   * authRequest.getPassword()));
   * SecurityContextHolder.getContext().setAuthentication(auth);
   * 
   * // Check if user account is deleted or not yet activated, then proceed
   * User user = userService.findByEmail(authRequest.getEmail()).orElse(null);
   * if (user == null) {
   * return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",
   * "Account not found"));
   * 
   * }
   * if (!user.isActivated() && user.getActivationToken().equals("")) {
   * return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",
   * "This account has been deleted"));
   * } else if (user.isActivated() == false)
   * return ResponseEntity.status(HttpStatus.FORBIDDEN)
   * .body(Map.of("message",
   * "Account not yet activated. Please check your email for the activation link."
   * ));
   * 
   * UserDetails userDetails = (UserDetails) auth.getPrincipal();
   * String accessToken = jwtUtil.generateToken(userDetails, 1000 * 60 * 60); //
   * sat vremena access token
   * String refreshToken =
   * jwtUtil.generateTokenFromUsername(userDetails.getUsername(), 1000 * 60 * 60 *
   * 6); // 6 sati
   * // refresh
   * // token
   * 
   * return ResponseEntity.ok(new LoginResponse(
   * accessToken,
   * refreshToken,
   * user.getEmail(),
   * user.getFirstname(),
   * user.getLastname(),
   * user.getRole()));
   * } catch (BadCredentialsException ex) {
   * return
   * ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
   * }
   * }
   * 
   */
  @GetMapping("/confirm-registration/{token}")
  public ResponseEntity<?> confirmRegistration(@PathVariable String token) {
    try {
      authService.verifyToken(token);
      return ResponseEntity.ok(Map.of("message", "Activation successful"));
    } catch (Exception e) {
      // TODO: handle exception
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  /*
   * @PostMapping("/register")
   * public ResponseEntity<?> register(@RequestBody RegisterRequest
   * registerRequest) {
   * 
   * try {
   * if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
   * return ResponseEntity.badRequest().body(Map.of("message",
   * "Email is already taken"));
   * }
   * User user = authService.registerRegularUser(registerRequest);
   * return ResponseEntity
   * .ok(Map.of("message", "User registered successfully", "activationToken",
   * user.getActivationToken()));
   * 
   * } catch (Exception e) {
   * return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
   * }
   * }
   */

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    String message = authService.registerUser(registerRequest);
    return ResponseEntity
        .ok(Map.of("message", "User registered successfully"));
  }

}
