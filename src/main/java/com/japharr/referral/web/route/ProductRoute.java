package com.japharr.referral.web.route;

import com.japharr.referral.utils.RouteBuilder;
import com.japharr.referral.web.handler.ProductHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ProductRoute implements RouteBuilder.Routing {
  private final ProductHandler productHandler;

  @Override
  public RouteBuilder routes(RouteBuilder bean) {
    Router router = bean.getRouter();

    router.get("/products").produces("application/json")
      .respond(productHandler::all);
    router.get("/products/:id").produces("application/json")
      .respond(productHandler::get)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.post("/products").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(productHandler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.put("/products/:id").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(productHandler::update);
    router.delete("/products/:id")
      .respond(productHandler::delete);

    return bean;
  }
}
