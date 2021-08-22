package com.japharr.referral.repository;

import com.japharr.referral.entity.Member;
import com.japharr.referral.entity.Member_;
import com.japharr.referral.exception.NotFoundException;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class MemberRepository {
  private final Mutiny.SessionFactory sessionFactory;

  private MemberRepository(Mutiny.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public static MemberRepository instance(Mutiny.SessionFactory sessionFactory) {
    return new MemberRepository(sessionFactory);
  }

  public Uni<List<Member>> findAll() {
    CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Member> query = cb.createQuery(Member.class);
    Root<Member> root = query.from(Member.class);
    return this.sessionFactory.withSession(session -> session.createQuery(query).getResultList());
  }

  public Uni<List<Member>> findByKeyword(String q, int offset, int limit) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create query
    CriteriaQuery<Member> query = cb.createQuery(Member.class);
    // set the root class
    Root<Member> root = query.from(Member.class);

    // if keyword is provided
    if (q != null && !q.trim().isEmpty()) {
      query.where(
        cb.or(
          cb.like(root.get(Member_.name), "%" + q + "%"),
          cb.like(root.get(Member_.email), "%" + q + "%")
        )
      );
    }
    //perform query
    return this.sessionFactory.withSession(session -> session.createQuery(query)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList());
  }

  public Uni<Member> findById(Long id) {
    Objects.requireNonNull(id, "id can not be null");
    return this.sessionFactory.withSession(session -> session.find(Member.class, id))
      .onItem().ifNull().failWith(() -> new NotFoundException(id));
  }

  public Uni<Member> findByReferralCode(String referralCode) {
    if(referralCode == null) return Uni.createFrom().nullItem();

    Objects.requireNonNull(referralCode, "referralCode can not be null");
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create query
    CriteriaQuery<Member> query = cb.createQuery(Member.class);
    // set the root class
    Root<Member> root = query.from(Member.class);
    query.where(
      cb.equal(root.get(Member_.referralCode), referralCode)
    );
    return this.sessionFactory.withSession(session ->
        session.createQuery(query).getSingleResult())
      .onItem().ifNull().failWith(() -> new NotFoundException(referralCode));
  }

  public Uni<Member> save(Member merchant) {
    if(merchant.getId() == null) {
      return this.sessionFactory.withSession(session ->
        session.persist(merchant)
          .chain(session::flush)
          .replaceWith(merchant));
    } else {
      return this.sessionFactory.withSession(session ->
        session.merge(merchant)
          .onItem().call(session::flush)
      );
    }
  }

  public Uni<Member[]> saveAll(List<Member> data) {
    Member[] array = data.toArray(new Member[0]);
    return this.sessionFactory.withSession(session -> {
      session.persistAll(array);
      session.flush();
      return Uni.createFrom().item(array);
    });
  }

  public Uni<Integer> deleteById(Long id) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Member> delete = cb.createCriteriaDelete(Member.class);
    Root<Member> root = delete.from(Member.class);
    // set where clause
    delete.where(cb.equal(root.get(Member_.id), id));
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }

  public Uni<Integer> deleteAll() {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create delete
    CriteriaDelete<Member> delete = cb.createCriteriaDelete(Member.class);
    // set the root class
    Root<Member> root = delete.from(Member.class);
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }
}
