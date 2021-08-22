package com.japharr.referral.entity;

import com.japharr.referral.entity.key.MemberProductPK;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@IdClass(MemberProductPK.class)
@Entity
public class MemberProduct {
  @Id
  private Long merchantId;
  @Id
  private Long productId;
}
