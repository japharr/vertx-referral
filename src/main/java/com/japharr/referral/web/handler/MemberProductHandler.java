package com.japharr.referral.web.handler;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.model.MemberProductDto;
import com.japharr.referral.model.Temp;
import com.japharr.referral.repository.MemberProductRepository;
import com.japharr.referral.service.MemberProductService;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberProductHandler {
  private final MemberProductService memberProductService;
  private final MemberProductRepository memberProductRepository;

  public Uni<List<MemberProduct>> all(RoutingContext rc) {
    return this.memberProductService.findAll();
  }

  public Uni<List<MemberProduct>> findByProductId(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    return this.memberProductService.findByProductId(productId);
  }

  public Uni<List<Temp>> findByProductIdAndMemberId(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    var memberId = Long.parseLong(params.get("memberId"));
    return this.memberProductRepository.getParentList2(productId, memberId);
  }

  public Uni<List<MemberProduct>> creditMember(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    var memberCode = params.get("referralCode");
    return this.memberProductService.creditMember(productId, memberCode);
  }

  public Uni<MemberProduct> save(RoutingContext rc) {
    var params = rc.pathParams();
    var productId = Long.parseLong(params.get("productId"));
    var form = rc.getBodyAsJson().mapTo(MemberProductDto.class);

    return memberProductService.addMemberToProduct(form.getMemberId(), productId, form.getReferralCode());
  }
}
