package com.ftn.pki.repository;

import com.ftn.pki.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
  Optional<Certificate> findBySerialNumber(String serialNumber);

  List<Certificate> findByIssuerIdAndRevoked(Long issuerId, boolean revoked);

  List<Certificate> findByIssuerId(Long issuerId);
}