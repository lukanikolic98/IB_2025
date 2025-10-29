package com.ftn.pki.dto;

import java.time.LocalDate;

import com.ftn.pki.model.CertificateTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CertificateDto {
  private Long id;
  private String commonName;
  private String organization;
  private String organizationalUnit;
  private String country;
  private String email;
  private CertificateTypeEnum type;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean revoked;
}
