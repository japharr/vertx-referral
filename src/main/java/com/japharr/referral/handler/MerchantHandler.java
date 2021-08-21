package com.japharr.referral.handler;

import com.japharr.referral.entity.Merchant;
import com.japharr.referral.repository.MerchantRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.ext.web.RoutingContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MerchantHandler {
  private static final Logger LOGGER = Logger.getLogger(MerchantHandler.class.getSimpleName());

  private final MerchantRepository repository;

  private MerchantHandler(MerchantRepository repository) {
    this.repository = repository;
  }

  public static MerchantHandler instance(MerchantRepository merchantRepository) {
    return new MerchantHandler(merchantRepository);
  }

  public void all(RoutingContext rc) {
    this.repository.findAll()
      .subscribe()
      .with(
        data -> rc.response().endAndAwait(Json.encode(data)),
        rc::fail
      );
  }

  public void get(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    this.repository.findById(id)
      .subscribe()
      .with(
        post -> rc.response().endAndAwait(Json.encode(post)),
        throwable -> rc.fail(404, throwable)
      );
  }

//  public Uni<Merchant> get(RoutingContext rc) {
//    var params = rc.pathParams();
//    var id = Long.parseLong(params.get("id"));
//    return this.repository.findById(id);
//  }

  public void save(RoutingContext rc) {
    //rc.getBodyAsJson().mapTo(PostForm.class)
    var body = rc.getBodyAsJson();
    var form = body.mapTo(Merchant.class);
    this.repository
      .save(Merchant.builder()
        .name(form.getName())
        .email(form.getEmail())
        .build()
      )
      .subscribe()
      .with(
        savedId -> rc.response()
          .putHeader("Location", "/merchants/" + savedId.getId())
          .setStatusCode(201)
          .endAndAwait(Json.encode(savedId)),
        throwable -> rc.fail(404, throwable)
      );
  }

  public void update(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    var body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "\npath param id: {0}\nrequest body: {1}", new Object[]{id, body});
    var form = body.mapTo(Merchant.class);
    this.repository.findById(id)
      .flatMap(
        post -> {
          post.setName(form.getName());
          post.setEmail(form.getEmail());

          return this.repository.save(post);
        }
      )
      .subscribe()
      .with(
        data -> rc.response()
          .setStatusCode(200)
          .endAndAwait(Json.encode(data)),
        throwable -> rc.fail(404, throwable)
      );
  }

  /*
  public Uni<Integer> delete(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    return this.repository.findById(id)
      .flatMap(
        post -> this.repository.deleteById(id)
      )
      .onItem().invoke(deleted -> rc.response()
        .setStatusCode(204).end()
      )
      .onFailure().invoke(rc::fail);
  }
  */

  public void delete(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    this.repository.findById(id)
      .flatMap(
        post -> this.repository.deleteById(id)
      )
      .subscribe()
      .with(
        data -> rc.response().setStatusCode(204).endAndAwait(),
        throwable -> rc.fail(404, throwable)
      );
  }
}
