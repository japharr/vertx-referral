package com.japharr.referral.entity.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Embeddable
public class MemberProductPK implements Serializable {
  private Long memberId;
  private Long productId;
}
