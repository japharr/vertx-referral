package com.japharr.referral.repository;

import com.japharr.referral.entity.Member;
import com.japharr.referral.exception.NotFoundException;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MemberRepository {
  private final PgPool client;
  private static final Function<Row, Member> MAPPER = row ->
    new Member(
      row.getLong("id"),
      row.getString("email"),
      row.getString("title")
    );

  private MemberRepository(PgPool client) {
    this.client = client;
  }

  public static MemberRepository instance(PgPool client) {
    return new MemberRepository(client);
  }

  public Future<List<Member>> findAll() {
    return client.query("SELECT * FROM members")
      .execute()
      .map(rows -> StreamSupport.stream(rows.spliterator(), false)
        .map(MAPPER)
        .collect(Collectors.toList()));
  }

  public Future<Member> findById(long id) {
    return client.preparedQuery("SELECT * FROM members WHERE id=$1")
      .execute(Tuple.of(id))
      .map(RowSet::iterator)
      .map((RowIterator<Row> it) -> {
        if(it.hasNext()) {
          return MAPPER.apply(it.next());
        }
        else throw new NotFoundException(id);
      });
  }
}
