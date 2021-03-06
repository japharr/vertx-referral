package com.japharr.referral.web.handler;

import com.japharr.referral.entity.Member;
import com.japharr.referral.repository.MemberRepository;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.Json;
import io.vertx.mutiny.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class MemberHandler {
  private static final Logger LOGGER = Logger.getLogger(MemberHandler.class.getSimpleName());

  private final MemberRepository repository;

  public Uni<List<Member>> all(RoutingContext rc) {
    return this.repository.findAll();
  }

  public Uni<Member> get(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    return this.repository.findById(id);
  }

  public Uni<Member> save(RoutingContext rc) {
    //rc.getBodyAsJson().mapTo(PostForm.class)
    var body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "request body: {0}", body);
    var form = body.mapTo(Member.class);
    return this.repository
      .save(Member.builder()
        .name(form.getName())
        .email(form.getEmail())
        .referralCode(form.getReferralCode())
        .build()
      )
      .onItem().invoke(saved -> rc.response()
        .putHeader("Location", "/members/" + saved.getId())
        .setStatusCode(201).end(Json.encode(saved))
      )
      .onFailure().invoke(rc::fail);
  }

  public Uni<Member> update(RoutingContext rc) {
    var params = rc.pathParams();
    var id = Long.parseLong(params.get("id"));
    var body = rc.getBodyAsJson();
    LOGGER.log(Level.INFO, "\npath param id: {0}\nrequest body: {1}", new Object[]{id, body});
    var form = body.mapTo(Member.class);
    return this.repository.findById(id)
      .flatMap(
        post -> {
          post.setName(form.getName());
          post.setEmail(form.getEmail());
          post.setReferralCode(form.getReferralCode());
          return this.repository.save(post);
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
        post -> this.repository.deleteById(id)
      )
      .onItem().invoke(deleted -> rc.response()
        .setStatusCode(204).end()
      )
      .onFailure().invoke(rc::fail);
  }
}
