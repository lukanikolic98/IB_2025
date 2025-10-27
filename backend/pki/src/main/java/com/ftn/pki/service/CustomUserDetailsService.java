package com.ftn.pki.service;

import java.util.List;

import com.ftn.pki.model.User;
import com.ftn.pki.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        user.isActivated(), // enabled
        true, // accountNonExpired
        true, // credentialsNonExpired
        true, // accountNonLocked
        List.of(new SimpleGrantedAuthority(user.getRole().name())) // e.g. ROLE_ADMIN
    );
  }

}
