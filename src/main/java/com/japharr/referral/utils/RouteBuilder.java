package com.japharr.referral.utils;

import io.vertx.mutiny.ext.web.Router;

public class RouteBuilder {
  private final Router router;

  private RouteBuilder(Router router) {
    this.router = router;
  }

  public static RouteBuilder instance(Router router) {
    return new RouteBuilder(router);
  }

  public Router getRouter() {
    return this.router;
  }

  public RouteBuilder addRoute(Routing routing) {
    return routing.routes(this);
  }

  public interface Routing {
    RouteBuilder routes(RouteBuilder bean);
  }
}
