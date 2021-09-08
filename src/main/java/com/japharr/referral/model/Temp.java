package com.japharr.referral.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
public class Temp {
  // product_id, member_id, point
  private Long product_id;
  private Long member_id;
  private BigDecimal point;
  private int lvl;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Temp temp = (Temp) o;
    return product_id.equals(temp.product_id) && member_id.equals(temp.member_id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(product_id, member_id);
  }
}
