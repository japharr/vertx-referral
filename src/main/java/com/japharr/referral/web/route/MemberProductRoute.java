package com.japharr.referral.web.route;

import com.japharr.referral.model.RouterBean;
import com.japharr.referral.web.handler.MemberProductHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;

public class MemberProductRoute implements RouterBean.Routing {
  private final MemberProductHandler handler;

  private MemberProductRoute(MemberProductHandler handler) {
    this.handler = handler;
  }

  public static MemberProductRoute instance(MemberProductHandler handler) {
    return new MemberProductRoute(handler);
  }

  @Override
  public RouterBean routes(RouterBean bean) {
    Router router = bean.getRouter();

    router.get("/products/:productId/members").produces("application/json")
      .respond(handler::findByProductId);
    router.post("/products/:productId/members").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(handler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));

    return bean;
  }
}
