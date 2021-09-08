package com.japharr.referral.web.route;

import com.japharr.referral.utils.RouteBuilder;
import com.japharr.referral.web.handler.MemberProductHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class MemberProductRoute implements RouteBuilder.Routing {
  private final MemberProductHandler handler;

  @Override
  public RouteBuilder routes(RouteBuilder bean) {
    Router router = bean.getRouter();

    router.get("/products/:productId/members-list/:memberId").produces("application/json")
      .respond(handler::findByProductIdAndMemberId);
    router.post("/products/:productId/members/:referralCode").produces("application/json")
      .respond(handler::creditMember);
    router.get("/products/:productId/members").produces("application/json")
      .respond(handler::findByProductId);
    router.post("/products/:productId/members").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(handler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));

    return bean;
  }
}
