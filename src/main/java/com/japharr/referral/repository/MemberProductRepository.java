package com.japharr.referral.repository;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.entity.MemberProduct_;
import com.japharr.referral.entity.key.MemberProductPK;
import com.japharr.referral.entity.key.MemberProductPK_;
import com.japharr.referral.exception.NotFoundException;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

public class MemberProductRepository {
  private final Mutiny.SessionFactory sessionFactory;

  private MemberProductRepository(Mutiny.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public static MemberProductRepository instance(Mutiny.SessionFactory sessionFactory) {
    return new MemberProductRepository(sessionFactory);
  }

  public Uni<List<MemberProduct>> findAll() {
    CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<MemberProduct> query = cb.createQuery(MemberProduct.class);
    Root<MemberProduct> root = query.from(MemberProduct.class);
    return this.sessionFactory.withSession(session -> session.createQuery(query).getResultList());
  }

  public Uni<MemberProduct> findById(MemberProductPK id) {
    Objects.requireNonNull(id, "id can not be null");
    return this.sessionFactory.withSession(session -> session.find(MemberProduct.class, id))
      .onItem().ifNull().failWith(() -> new NotFoundException(id));
  }

  public Uni<List<MemberProduct>> findByProductId(Long productId) {
    Objects.requireNonNull(productId, "productId can not be null");
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create query
    CriteriaQuery<MemberProduct> query = cb.createQuery(MemberProduct.class);
    // set the root class
    Root<MemberProduct> root = query.from(MemberProduct.class);
    query.where(cb.equal(root.get(MemberProduct_.id).get(MemberProductPK_.productId), productId));
    return this.sessionFactory.withSession(session ->
        session.createQuery(query).getResultList())
      .onItem().ifNull().failWith(() -> new NotFoundException(productId));
  }

  public Uni<MemberProduct> save(MemberProduct merchant) {
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

  public Uni<MemberProduct[]> saveAll(List<MemberProduct> data) {
    MemberProduct[] array = data.toArray(new MemberProduct[0]);
    return this.sessionFactory.withSession(session -> {
      session.persistAll(array);
      session.flush();
      return Uni.createFrom().item(array);
    });
  }

  public Uni<Integer> deleteById(MemberProductPK id) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    CriteriaDelete<MemberProduct> delete = cb.createCriteriaDelete(MemberProduct.class);
    Root<MemberProduct> root = delete.from(MemberProduct.class);
    // set where clause
    delete.where(cb.equal(root.get(MemberProduct_.id), id));
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }

  public Uni<Integer> deleteAll() {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create delete
    CriteriaDelete<MemberProduct> delete = cb.createCriteriaDelete(MemberProduct.class);
    // set the root class
    Root<MemberProduct> root = delete.from(MemberProduct.class);
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }
}
