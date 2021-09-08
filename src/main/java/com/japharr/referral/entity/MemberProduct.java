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
  private int lvl;

  @ManyToOne(targetEntity = Member.class)
  @JoinColumn(name = "member_id", insertable = false, updatable = false)
  private Member member;

  @ManyToOne(targetEntity = Product.class)
  @JoinColumn(name = "product_id", insertable = false, updatable = false)
  private Product product;

  @ManyToOne(targetEntity = MemberProduct.class, cascade = MERGE)
  private MemberProduct parent;
}
