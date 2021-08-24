package com.japharr.referral.web.route;

import com.japharr.referral.model.RouterBean;
import com.japharr.referral.web.handler.MerchantHandler;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;

public class MerchantRoute implements RouterBean.Routing {
  private final MerchantHandler merchantHandler;

  private MerchantRoute(MerchantHandler merchantHandler) {
    this.merchantHandler = merchantHandler;
  }

  public static MerchantRoute instance(MerchantHandler merchantHandler) {
    return new MerchantRoute(merchantHandler);
  }

  @Override
  public RouterBean routes(RouterBean bean) {
    Router router = bean.getRouter();

    router.get("/merchants").produces("application/json")
      .handler(merchantHandler::all);
    router.get("/merchants/:id").produces("application/json")
      .handler(merchantHandler::get)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.post("/merchants").consumes("application/json")
      .handler(BodyHandler.create())
      .handler(merchantHandler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.put("/merchants/:id").consumes("application/json")
      .handler(BodyHandler.create())
      .handler(merchantHandler::update);
    router.delete("/merchants/:id")
      .handler(merchantHandler::delete);

    return bean;
  }

  /*
  public Router routes() {
    // Create a Router
    // register BodyHandler globally.
    //router.route().handler(BodyHandler.create());

    router.get("/merchants").produces("application/json")
      .handler(merchantHandler::all);
    router.get("/merchants/:id").produces("application/json")
      .handler(merchantHandler::get)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.post("/merchants").consumes("application/json")
      .handler(BodyHandler.create())
      .handler(merchantHandler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));
    router.put("/merchants/:id").consumes("application/json")
      .handler(BodyHandler.create())
      .handler(merchantHandler::update);
    router.delete("/merchants/:id")
      .handler(merchantHandler::delete);

    return router;
  }
  */
}
