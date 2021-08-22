package com.japharr.referral.model;

import lombok.Data;

@Data
public class MemberProductDto {
  private Long memberId;
  private String referralCode;
}
