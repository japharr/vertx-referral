package com.japharr.referral.entity.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Setter @Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Embeddable
public class MemberProductPK implements Serializable {
  private Long memberId;
  private Long productId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemberProductPK that = (MemberProductPK) o;
    return memberId.equals(that.memberId) && productId.equals(that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(memberId, productId);
  }
}
