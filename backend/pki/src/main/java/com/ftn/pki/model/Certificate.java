package com.ftn.pki.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "certificates")
public class Certificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String commonName;
  private String organization;
  private String organizationalUnit;
  private String country;
  private String email;

  private LocalDate revocationDate;
  private Integer revocationReason;

  @Enumerated(EnumType.STRING)
  private CertificateTypeEnum type; // ROOT, INTERMEDIATE, END_ENTITY

  private LocalDate startDate;
  private LocalDate endDate;

  @Lob
  private byte[] certificateData;

  @Lob
  private byte[] privateKeyData;

  private boolean revoked = false;

  private String serialNumber;

  private Long issuerId;
}