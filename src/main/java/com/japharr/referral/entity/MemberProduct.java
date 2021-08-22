package com.japharr.referral.entity;

import com.japharr.referral.entity.key.MemberProductPK;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.CascadeType.MERGE;

@Getter @Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Entity
@Table(name = "member_products")
public class MemberProduct {
  @EmbeddedId
  private MemberProductPK id;
  private BigDecimal point = BigDecimal.ZERO;

  @ManyToOne(targetEntity = MemberProduct.class, cascade = MERGE)
  private MemberProduct parent;
}
