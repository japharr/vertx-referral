package com.japharr.referral.pattern.sharedpoint.factory;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.pattern.sharedpoint.SharedPoint;

import java.math.BigDecimal;
import java.util.List;

public class Parallel implements SharedPoint {
  @Override
  public void compute(List<MemberProduct> items, Double sp) {
    var ap = sp/items.size();
    items.forEach(r -> r.setPoint(r.getPoint().add(BigDecimal.valueOf(ap))));
  }
}
