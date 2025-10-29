package com.ftn.pki.dto;

import java.time.LocalDate;
import java.util.List;

import com.ftn.pki.model.CertificateTypeEnum;

import lombok.Data;

@Data
public class CertificateRequest {

  private String commonName;
  private String organization;
  private String organizationalUnit;
  private String country;
  private String email;
  private CertificateTypeEnum type; // ROOT, INTERMEDIATE, END_ENTITY
  private LocalDate endDate; // do kog datuma vazi sertifikat
  private Long issuerId;
  private int validityDays;
  private List<String> keyUsages; // ["digitalSignature", "keyEncipherment"]
  private Integer pathLength;

}
