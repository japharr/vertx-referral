package com.japharr.referral.pattern.sharedpoint.factory;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.pattern.sharedpoint.SharedPoint;

import java.math.BigDecimal;
import java.util.List;

public class BottomTop implements SharedPoint {
  @Override
  public void compute(List<MemberProduct> items, Double sp) {
    var sum = items.stream().mapToInt(MemberProduct::getLvl).sum();
    items.forEach(r -> {
      var ap2 = ((double)r.getLvl() / sum) * sp;
      r.setPoint(r.getPoint().add(BigDecimal.valueOf(ap2)));
    });
  }
}
