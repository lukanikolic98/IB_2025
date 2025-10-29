package com.ftn.pki.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CertificateTypeEnum {
  ROOT,
  INTERMEDIATE,
  END_ENTITY;

  @JsonCreator
  public static CertificateTypeEnum fromString(String key) {
    return key == null ? null : CertificateTypeEnum.valueOf(key.toUpperCase().replace("-", "_"));
  }

  @JsonValue
  public String toValue() {
    return this.name();
  }
}
