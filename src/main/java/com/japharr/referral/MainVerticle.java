package com.japharr.referral;

import com.japharr.referral.model.RouterBean;
import com.japharr.referral.web.handler.MemberHandler;
import com.japharr.referral.web.handler.MemberProductHandler;
import com.japharr.referral.web.handler.MerchantHandler;
import com.japharr.referral.web.handler.ProductHandler;
import com.japharr.referral.repository.MemberProductRepository;
import com.japharr.referral.repository.MemberRepository;
import com.japharr.referral.repository.MerchantRepository;
import com.japharr.referral.repository.ProductRepository;
import com.japharr.referral.service.MemberProductService;
import com.japharr.referral.web.route.MemberProductRoute;
import com.japharr.referral.web.route.MemberRoute;
import com.japharr.referral.web.route.MerchantRoute;
import com.japharr.referral.web.route.ProductRoute;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.mutiny.core.http.HttpServer;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.stereotype.Component;

import javax.persistence.Persistence;

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

    router.get("/hello")
      //.handler(rc -> Uni.createFrom().item("Hello from my route").subscribe().with(r-> rc.response().endAndForget(r)));
      //.handler(rc -> rc.response().endAndForget("Hello from my route"));
      .respond(rc -> Uni.createFrom().item("Hello from my route"));

    // Configure routes
    router = (RouterBean.instance(router))
      .addRoutes(MerchantRoute.instance(merchantHandler))
      .addRoutes(MemberRoute.instance(memberHandler))
      .addRoutes(ProductRoute.instance(productHandler))
      .addRoutes(MemberProductRoute.instance(memberProductHandler))
      .getRouter();

    //router.get("/hello").handler(rc -> rc.response().endAndAwait("Hello from my route"));
    //router.get("/hello").handler(rc -> rc.response().end("Hello from my route"));

    Uni<HttpServer> startHttpServer = vertx.createHttpServer()
      .requestHandler(router)
      .listen(8088)
      .onItem().invoke(() -> System.out.println("✅ HTTP server listening on port 8088"))
      .onFailure().invoke(Throwable::printStackTrace);

    return startHttpServer.replaceWithVoid();
  }
}
