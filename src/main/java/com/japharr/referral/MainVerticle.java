package com.japharr.referral;

import com.japharr.referral.handler.MemberHandler;
import com.japharr.referral.handler.MemberProductHandler;
import com.japharr.referral.handler.MerchantHandler;
import com.japharr.referral.handler.ProductHandler;
import com.japharr.referral.repository.MemberProductRepository;
import com.japharr.referral.repository.MemberRepository;
import com.japharr.referral.repository.MerchantRepository;
import com.japharr.referral.repository.ProductRepository;
import com.japharr.referral.service.MemberProductService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.http.HttpServer;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.Persistence;

public class MainVerticle extends AbstractVerticle {

  @Override
  public Uni<Void> asyncStart() {

    Mutiny.SessionFactory emf = Persistence
      .createEntityManagerFactory("pg-demo")
      .unwrap(Mutiny.SessionFactory.class);

    MerchantRepository merchantRepository = MerchantRepository.instance(emf);
    MerchantHandler merchantHandler = MerchantHandler.instance(merchantRepository);

    ProductRepository productRepository = ProductRepository.instance(emf);
    ProductHandler productHandler = ProductHandler.instance(productRepository);

    MemberRepository memberRepository = MemberRepository.instance(emf);
    MemberHandler memberHandler = MemberHandler.instance(memberRepository);

    MemberProductRepository memberProductRepository = MemberProductRepository.instance(emf);
    MemberProductService memberProductService = MemberProductService.of(memberRepository, productRepository, memberProductRepository);
    MemberProductHandler memberProductHandler = MemberProductHandler.instance(memberProductService);

    Router router = Router.router(vertx);

    // Configure routes
    var merchantRoutes = routes(router, merchantHandler);
    var productRoutes = productRoutes(router, productHandler);
    var memberRoutes = memberRoutes(router, memberHandler);
    var memberProductRoutes = memberProductRoute(router, memberProductHandler);

    Uni<HttpServer> startHttpServer = vertx.createHttpServer()
      .requestHandler(router)
      .listen(8088)
      .onItem().invoke(() -> System.out.println("✅ HTTP server listening on port 8080"))
      .onFailure().invoke(Throwable::printStackTrace);

    return startHttpServer.replaceWithVoid();
  }

  private Router routes(Router router, MerchantHandler merchantHandler) {
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

  private Router productRoutes(Router router, ProductHandler productHandler) {
    // Create a Router
//    Router router = Router.router(vertx);
    // register BodyHandler globally.
    //router.route().handler(BodyHandler.create());

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

    return router;
  }

  private Router memberRoutes(Router router, MemberHandler memberHandler) {
    // Create a Router
    // register BodyHandler globally.
    //router.route().handler(BodyHandler.create());

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

    return router;
  }

  private Router memberProductRoute(Router router, MemberProductHandler memberProductHandler) {
    router.get("/products/:productId/members").produces("application/json")
      .respond(memberProductHandler::findByProductId);
    router.post("/products/:productId/members").consumes("application/json")
      .handler(BodyHandler.create())
      .respond(memberProductHandler::save)
      .failureHandler(frc -> frc.response().setStatusCode(404).endAndAwait(frc.failure().getMessage()));

    return router;
  }
}
