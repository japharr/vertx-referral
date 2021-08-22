package com.japharr.referral.repository;

import com.japharr.referral.entity.Merchant;
import com.japharr.referral.entity.Merchant_;
import com.japharr.referral.exception.NotFoundException;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class MerchantRepository {
  private final Mutiny.SessionFactory sessionFactory;

  private MerchantRepository(Mutiny.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public static MerchantRepository instance(Mutiny.SessionFactory sessionFactory) {
    return new MerchantRepository(sessionFactory);
  }

  public Uni<List<Merchant>> findAll() {
    CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Merchant> query = cb.createQuery(Merchant.class);
    Root<Merchant> root = query.from(Merchant.class);
    return this.sessionFactory.withSession(session -> session.createQuery(query).getResultList());
  }

  public Uni<List<Merchant>> findByKeyword(String q, int offset, int limit) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create query
    CriteriaQuery<Merchant> query = cb.createQuery(Merchant.class);
    // set the root class
    Root<Merchant> root = query.from(Merchant.class);

    // if keyword is provided
    if (q != null && !q.trim().isEmpty()) {
      query.where(
        cb.or(
          cb.like(root.get(Merchant_.name), "%" + q + "%"),
          cb.like(root.get(Merchant_.email), "%" + q + "%")
        )
      );
    }
    //perform query
    return this.sessionFactory.withSession(session -> session.createQuery(query)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList());
  }


  public Uni<Merchant> findById(Long id) {
    Objects.requireNonNull(id, "id can not be null");
    return this.sessionFactory.withSession(session -> session.find(Merchant.class, id))
      .onItem().ifNull().failWith(() -> new NotFoundException(id));
  }

  public Uni<Merchant> save(Merchant merchant) {
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

  public Uni<Merchant[]> saveAll(List<Merchant> data) {
    Merchant[] array = data.toArray(new Merchant[0]);
    return this.sessionFactory.withSession(session -> {
      session.persistAll(array);
      session.flush();
      return Uni.createFrom().item(array);
    });
  }

  public Uni<Integer> deleteById(Long id) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Merchant> delete = cb.createCriteriaDelete(Merchant.class);
    Root<Merchant> root = delete.from(Merchant.class);
    // set where clause
    delete.where(cb.equal(root.get(Merchant_.id), id));
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }

  public Uni<Integer> deleteAll() {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create delete
    CriteriaDelete<Merchant> delete = cb.createCriteriaDelete(Merchant.class);
    // set the root class
    Root<Merchant> root = delete.from(Merchant.class);
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }
}
