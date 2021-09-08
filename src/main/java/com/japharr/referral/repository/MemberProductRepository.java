package com.japharr.referral.repository;

import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.entity.MemberProduct_;
import com.japharr.referral.entity.key.MemberProductPK;
import com.japharr.referral.entity.key.MemberProductPK_;
import com.japharr.referral.exception.NotFoundException;
import com.japharr.referral.model.Temp;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberProductRepository {
  private final Mutiny.SessionFactory sessionFactory;

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
    return this.sessionFactory.withSession(session -> session.persistAll(data)
      .onItem().call(session::flush)
      .replaceWith(array));
  }

  public Uni<List<MemberProduct>> updateAll(List<MemberProduct> data) {
    MemberProduct[] array = data.toArray(new MemberProduct[0]);
    return this.sessionFactory.withSession(session -> session.mergeAll(array)
      .onItem().call(session::flush)
      .replaceWith(data));
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

  private static final String query = "WITH RECURSIVE ancestors(product_id, member_id, parent_product_id, parent_member_id, point, lvl) AS (SELECT cat.product_id, cat.member_id, cat.parent_product_id, cat.parent_member_id, cat.point, 1 AS lvl FROM member_products cat WHERE cat.product_id = :productId AND cat.member_id = :memberId UNION ALL SELECT parent.product_id, parent.member_id, parent.parent_product_id, parent.parent_member_id, parent.point, child.lvl + 1 AS lvl FROM member_products parent JOIN ancestors child ON parent.member_id = child.parent_member_id AND parent.product_id = child.parent_product_id) SELECT product_id, member_id, point, lvl from ancestors ORDER BY lvl DESC";
  private static final String query2 = "WITH RECURSIVE ancestors(product_id, member_id, parent_product_id, parent_member_id, point, lvl) AS (SELECT cat.product_id, cat.member_id, cat.parent_product_id, cat.parent_member_id, cat.point, 1 AS lvl FROM member_products cat WHERE cat.product_id = :productId AND cat.member_id = :memberId UNION ALL SELECT parent.product_id, parent.member_id, parent.parent_product_id, parent.parent_member_id, parent.point, child.lvl + 1 AS lvl FROM member_products parent JOIN ancestors child ON parent.member_id = child.parent_member_id AND parent.product_id = child.parent_product_id) SELECT product_id, member_id, point, lvl, parent_product_id, parent_member_id from ancestors ORDER BY lvl DESC";

  public Uni<List<MemberProduct>> getParentList(Long productId, Long memberId) {
    return sessionFactory.withSession(session -> session.createNativeQuery(query2, MemberProduct.class)
      .setParameter("productId", productId)
        .setParameter("memberId", memberId)
        .getResultList())
      .onItem().ifNull().failWith(() -> new NotFoundException(productId));
  }

  public Uni<List<Temp>> getParentList3(Long productId, Long memberId) {
    return sessionFactory.withSession(session -> session.createNativeQuery(query2, Temp.class)
      .setParameter("productId", productId)
        .setParameter("memberId", memberId)
        .getResultList())
      .onItem().ifNull().failWith(() -> new NotFoundException(productId));
  }

  public Uni<List<Temp>> getParentList2(Long productId, Long memberId) {
    return sessionFactory.withSession(session -> session.createNativeQuery(query2, Temp.class)
      .setParameter("productId", productId)
        .setParameter("memberId", memberId)
        .getResultList().map(ArrayList::new));
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
