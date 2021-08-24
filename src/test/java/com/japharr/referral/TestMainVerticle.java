package com.japharr.referral;

import io.vertx.mutiny.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientRequest;
import io.vertx.mutiny.core.http.HttpClientResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  private final static Logger LOGGER = Logger.getLogger(TestMainVerticle.class.getName());

  HttpClient client;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).subscribe().with(
      id -> {
        LOGGER.info("deployed:" + id);
        testContext.completeNow();
      },
      testContext::failNow
    );
    //vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    var options = new HttpClientOptions().setDefaultPort(8088);
    this.client = vertx.createHttpClient(options);
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  public void testVertx(Vertx vertx, VertxTestContext testContext) {
    assertThat(vertx).isNotNull();
    testContext.completeNow();
  }

  // Repeat this test 3 times
  @RepeatedTest(3)
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @DisplayName("Check the HTTP response...")
  void testHello(Vertx vertx, VertxTestContext testContext) {
    client.request(HttpMethod.GET, "/hello")
      .flatMap(HttpClientRequest::send)
      .subscribe()
      .with(
        buffer ->
          testContext.verify(
            () -> {
              buffer.body().subscribe().with(r -> {
                LOGGER.log(Level.INFO, "response buffer: {0}", new Object[]{r.toString()});
                assertThat(r.toString()).isEqualTo("Hello from my route");
                testContext.completeNow();
              });
            }
          ),
        e -> {
          LOGGER.log(Level.ALL, "error: {0}", e.getMessage());
          testContext.failNow(e);
        }
      );
  }

  @Test
  void testGetAll(VertxTestContext testContext) {
    LOGGER.log(Level.INFO, "running test: {0}", "testGetAll");
    client.request(HttpMethod.GET, "/products")
      .flatMap(HttpClientRequest::send)
      .flatMap(HttpClientResponse::body)
      .subscribe()
      .with(buffer ->
          testContext.verify(
            () -> {
              LOGGER.log(Level.INFO, "response buffer: {0}", new Object[]{buffer.toString()});
              assertThat(buffer.toJsonArray().size()).isEqualTo(0);
              testContext.completeNow();
            }
          ),
        e -> {
          LOGGER.log(Level.ALL, "error: {0}", e.getMessage());
          testContext.failNow(e);
        }
      );
  }
}
