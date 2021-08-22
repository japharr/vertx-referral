package com.japharr.referral;

import com.japharr.referral.handler.MerchantHandler;
import com.japharr.referral.handler.ProductHandler;
import com.japharr.referral.repository.MerchantRepository;
import com.japharr.referral.repository.ProductRepository;
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

    // Configure routes
    var router = routes(merchantHandler);
    var productRoutes = productRoutes(productHandler);

    Uni<HttpServer> startHttpServer = vertx.createHttpServer()
      .requestHandler(router::handle)
      .requestHandler(productRoutes::handle)
      .listen(8088)
      .onItem().invoke(() -> System.out.println("✅ HTTP server listening on port 8080"))
      .onFailure().invoke(Throwable::printStackTrace);

    return startHttpServer.replaceWithVoid();
  }

  private Router routes(MerchantHandler merchantHandler) {
    // Create a Router
    Router router = Router.router(vertx);
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

  private Router productRoutes(ProductHandler productHandler) {
    // Create a Router
    Router router = Router.router(vertx);
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
}
