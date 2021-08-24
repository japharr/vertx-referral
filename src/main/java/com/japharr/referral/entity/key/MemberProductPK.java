package com.japharr.referral.entity.key;

import com.japharr.referral.entity.Member;
import com.japharr.referral.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Setter @Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Embeddable
public class MemberProductPK implements Serializable {
  @Column(name = "member_id")
  private Long memberId;
  @Column(name = "product_id")
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
