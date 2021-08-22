package com.japharr.referral.entity.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberProductPK implements Serializable {
  private Long memberId;
  private Long productId;
}
