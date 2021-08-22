package com.japharr.referral.handler;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.entity.Product;
import com.japharr.referral.model.MemberProductDto;
import com.japharr.referral.service.MemberProductService;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.List;

public class MemberProductHandler {
  private final MemberProductService memberProductService;

  private MemberProductHandler(MemberProductService memberProductService) {
    this.memberProductService = memberProductService;
  }

  public static MemberProductHandler instance(MemberProductService memberProductService) {
    return new MemberProductHandler(memberProductService);
  }

  public Uni<List<MemberProduct>> all(RoutingContext rc) {
    return this.memberProductService.findAll();
  }

  public Uni<List<MemberProduct>> findByProductId(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    return this.memberProductService.findByProductId(productId);
  }

  public Uni<MemberProduct> save(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    var form = rc.getBodyAsJson().mapTo(MemberProductDto.class);

    return memberProductService.addMemberToProduct(form.getMemberId(), productId, form.getReferralCode());
  }
}
