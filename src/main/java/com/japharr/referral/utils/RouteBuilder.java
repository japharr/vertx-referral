package com.japharr.referral.utils;

import io.vertx.mutiny.ext.web.Router;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RouteBuilder {
  private final Router router;

  public RouteBuilder addRoute(Routing routing) {
    return routing.routes(this);
  }

  public interface Routing {
    RouteBuilder routes(RouteBuilder bean);
  }
}
