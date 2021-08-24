package com.japharr.referral.web.route;

import com.japharr.referral.model.RouterBean;
import com.japharr.referral.web.handler.ProductHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;

public class ProductRoute implements RouterBean.Routing {
  private final ProductHandler productHandler;

  public ProductRoute(ProductHandler productHandler) {
    this.productHandler = productHandler;
  }

  public static ProductRoute instance(ProductHandler productHandler) {
    return new ProductRoute(productHandler);
  }

  @Override
  public RouterBean routes(RouterBean bean) {
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
