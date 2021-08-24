package com.japharr.referral;

import com.japharr.referral.utils.RouteBuilder;
import com.japharr.referral.web.handler.MemberHandler;
import com.japharr.referral.web.handler.MemberProductHandler;
import com.japharr.referral.web.handler.MerchantHandler;
import com.japharr.referral.web.handler.ProductHandler;
import com.japharr.referral.web.route.MemberProductRoute;
import com.japharr.referral.web.route.MemberRoute;
import com.japharr.referral.web.route.MerchantRoute;
import com.japharr.referral.web.route.ProductRoute;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.http.HttpServer;
import io.vertx.mutiny.ext.web.Router;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainVerticle extends AbstractVerticle {

  private final MerchantHandler merchantHandler;
  private final MemberProductHandler memberProductHandler;
  private final MemberHandler memberHandler;
  private final ProductHandler productHandler;

  @Override
  public Uni<Void> asyncStart() {
    Router router = Router.router(vertx);

    // Configure routes
    router = (RouteBuilder.of(router))
      .addRoute(MerchantRoute.of(merchantHandler))
      .addRoute(MemberRoute.of(memberHandler))
      .addRoute(ProductRoute.of(productHandler))
      .addRoute(MemberProductRoute.of(memberProductHandler))
      .addRoute(routeBuilder -> {
        routeBuilder.getRouter().get("/hello")
          .respond(rc -> Uni.createFrom().item("Hello from my route"));
        return routeBuilder;
      })
      .getRouter();

    Uni<HttpServer> startHttpServer = vertx.createHttpServer()
      .requestHandler(router)
      .listen(8088)
      .onItem().invoke(() -> System.out.println("✅ HTTP server listening on port 8088"))
      .onFailure().invoke(Throwable::printStackTrace);

    return startHttpServer.replaceWithVoid();
  }
}
