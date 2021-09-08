package com.japharr.referral.pattern.sharedpoint;

import com.japharr.referral.entity.MemberProduct;

import java.util.List;

public interface SharedPoint {
  void compute(List<MemberProduct> items, Double sp);
}
