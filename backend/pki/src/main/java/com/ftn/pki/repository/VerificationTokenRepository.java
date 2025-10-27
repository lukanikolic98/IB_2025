package com.ftn.pki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ftn.pki.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);
}