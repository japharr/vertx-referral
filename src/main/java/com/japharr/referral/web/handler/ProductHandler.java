package com.japharr.referral.web.handler;

import com.japharr.referral.entity.Product;
import com.japharr.referral.repository.ProductRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class ProductHandler {
  private static final Logger LOGGER = Logger.getLogger(ProductHandler.class.getSimpleName());

  private final ProductRepository repository;

  public Uni<List<Product>> all(RoutingContext rc) {
    return this.repository.findAll();
  }

  public Uni<Product> get(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    return this.repository.findById(id);
  }

  public Uni<Product> save(RoutingContext rc) {
    //rc.getBodyAsJson().mapTo(PostForm.class)
    var body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "request body: {0}", body);
    var form = body.mapTo(Product.class);
    return this.repository
      .save(Product.builder()
        .name(form.getName())
        .basePoint(form.getBasePoint())
        .sharedPoint(form.getSharedPoint())
        .sharedPointIncludeMember(form.isSharedPointIncludeMember())
        .sharedPointType(form.getSharedPointType())
        .build()
      )
      .onItem().invoke(saved -> rc.response()
        .putHeader("Location", "/products/" + saved.getId())
        .setStatusCode(201).end(Json.encode(saved))
      )
      .onFailure().invoke(rc::fail);
  }

  public Uni<Product> update(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    var body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "\npath param id: {0}\nrequest body: {1}", new Object[]{id, body});
    var form = body.mapTo(Product.class);
    return this.repository.findById(id)
      .flatMap(
        product -> {
          product.setName(form.getName());
          return this.repository.save(product);
        }
      )
      .onItem().invoke(updated -> rc.response()
        .setStatusCode(200).end(Json.encode(updated))
      )
      .onFailure().invoke(rc::fail);
  }

  public Uni<Integer> delete(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    return this.repository.findById(id)
      .flatMap(
        product -> this.repository.deleteById(id)
      )
      .onItem().invoke(deleted -> rc.response()
        .setStatusCode(204).end()
      )
      .onFailure().invoke(rc::fail);
  }
}
