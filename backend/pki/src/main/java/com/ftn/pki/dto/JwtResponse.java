package com.ftn.pki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.ftn.pki.model.UserRoleEnum;

@Data
@AllArgsConstructor
public class JwtResponse {
  private String accessToken;
  private String refreshToken;
  private String email;
  private String firstname;
  private String lastname;
  private UserRoleEnum role;
}