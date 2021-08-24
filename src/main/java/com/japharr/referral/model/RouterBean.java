package com.japharr.referral.model;

import io.vertx.mutiny.ext.web.Router;

public class RouterBean {
  private final Router router;

  private RouterBean(Router router) {
    this.router = router;
  }

  public static RouterBean instance(Router router) {
    return new RouterBean(router);
  }

  public Router getRouter() {
    return this.router;
  }

  public RouterBean addRoutes(Routing routing) {
    return routing.routes(this);
  }

  public interface Routing {
    RouterBean routes(RouterBean bean);
  }
}
