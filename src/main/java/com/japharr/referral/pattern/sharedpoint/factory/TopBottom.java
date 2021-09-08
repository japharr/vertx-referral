package com.japharr.referral.pattern.sharedpoint.factory;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.pattern.sharedpoint.SharedPoint;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class TopBottom implements SharedPoint {
  @Override
  public void compute(List<MemberProduct> items, Double sp) {
    System.out.println("before: items: " + items);
    items.forEach(ii -> System.out.println("item level: " + ii.getLvl()));

    var sum2 = items.stream().mapToInt(MemberProduct::getLvl).sum();
    System.out.println("total sum2: " + sum2);
    IntStream.range(0, items.size())
      .forEach(index -> {
        System.out.println("index: " + index);
        var r2 = items.get(index);
        var ap2 = ((double)(index + 1) / sum2) * sp;
        System.out.println("calc value: ap2: " + ap2);
        r2.setPoint(r2.getPoint().add(BigDecimal.valueOf(ap2)));
      });
    System.out.println("after: items: " + items);
  }
}
