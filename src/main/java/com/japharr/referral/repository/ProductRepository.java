package com.japharr.referral.repository;

import com.japharr.referral.entity.Product;
import com.japharr.referral.entity.Product_;
import com.japharr.referral.exception.NotFoundException;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductRepository {
  private final Mutiny.SessionFactory sessionFactory;

  public Uni<List<Product>> findAll() {
    CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    Root<Product> root = query.from(Product.class);
    return this.sessionFactory.withSession(session -> session.createQuery(query).getResultList());
  }

  public Uni<List<Product>> findByKeyword(String q, int offset, int limit) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create query
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    // set the root class
    Root<Product> root = query.from(Product.class);

    // if keyword is provided
    if (q != null && !q.trim().isEmpty()) {
      query.where(
        cb.or(
          cb.like(root.get(Product_.name), "%" + q + "%")
        )
      );
    }
    //perform query
    return this.sessionFactory.withSession(session -> session.createQuery(query)
      .setFirstResult(offset)
      .setMaxResults(limit)
      .getResultList());
  }


  public Uni<Product> findById(Long id) {
    Objects.requireNonNull(id, "id can not be null");
    return this.sessionFactory.withSession(session -> session.find(Product.class, id))
      .onItem().ifNull().failWith(() -> new NotFoundException(id));
  }

  public Uni<Product> save(Product merchant) {
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

  public Uni<Product[]> saveAll(List<Product> data) {
    Product[] array = data.toArray(new Product[0]);
    return this.sessionFactory.withSession(session -> {
      session.persistAll(array);
      session.flush();
      return Uni.createFrom().item(array);
    });
  }

  public Uni<Integer> deleteById(Long id) {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
    Root<Product> root = delete.from(Product.class);
    // set where clause
    delete.where(cb.equal(root.get(Product_.id), id));
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }

  public Uni<Integer> deleteAll() {
    CriteriaBuilder cb = this.sessionFactory.getCriteriaBuilder();
    // create delete
    CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
    // set the root class
    Root<Product> root = delete.from(Product.class);
    // perform update
    return this.sessionFactory.withTransaction((session, tx) ->
      session.createQuery(delete).executeUpdate()
    );
  }
}
