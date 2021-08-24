package com.japharr.referral.web.route;

import com.japharr.referral.utils.RouteBuilder;
import com.japharr.referral.web.handler.MemberHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;

public class MemberRoute implements RouteBuilder.Routing {
  private final MemberHandler memberHandler;

  private MemberRoute(MemberHandler memberHandler) {
    this.memberHandler = memberHandler;
  }

  public static MemberRoute instance(MemberHandler memberHandler) {
    return new MemberRoute(memberHandler);
  }

  @Override
  public RouteBuilder routes(RouteBuilder bean) {
    Router router = bean.getRouter();

    router.get("/members").produces("application/json")
      .respond(memberHandler::all);
    router.get("/members/:id").produces("application/json")
      .respond(memberHandler::get)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.post("/members").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(memberHandler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.put("/members/:id").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(memberHandler::update);
    router.delete("/members/:id")
      .respond(memberHandler::delete);

    return bean;
  }
}
