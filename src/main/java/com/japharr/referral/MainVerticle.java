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
    router = (RouterBean.instance(router))
      .addRoutes(MerchantRoute.instance(merchantHandler))
      .addRoutes(MemberRoute.instance(memberHandler))
      .addRoutes(ProductRoute.instance(productHandler))
      .addRoutes(MemberProductRoute.instance(memberProductHandler))
      .getRouter();

    Uni<HttpServer> startHttpServer = vertx.createHttpServer()
      .requestHandler(router)
      .listen(8088)
      .onItem().invoke(() -> System.out.println("✅ HTTP server listening on port 8080"))
      .onFailure().invoke(Throwable::printStackTrace);

    return startHttpServer.replaceWithVoid();
  }
}
