package com.japharr.referral.service;

import com.japharr.referral.entity.Member;
import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.entity.Product;
import com.japharr.referral.entity.key.MemberProductPK;
import com.japharr.referral.repository.MemberProductRepository;
import com.japharr.referral.repository.MemberRepository;
import com.japharr.referral.repository.ProductRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.tuples.Tuple3;
import lombok.AllArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor(staticName = "of")
public class MemberProductService {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final MemberProductRepository repository;

  public Uni<List<MemberProduct>> findAll() {
    return repository.findAll();
  }

  public Uni<List<MemberProduct>> findByProductId(Long productId) {
    return repository.findByProductId(productId);
  }

  public Uni<MemberProduct> addMemberToProduct(Long memberId, Long productId, String referralCode) {
    Uni<Member> memberUni = memberRepository.findById(memberId);
    Uni<Product> productUni = productRepository.findById(productId);
    Uni<Member> referralUni = memberRepository.findByReferralCode(referralCode);

    Uni<Tuple3<Member, Product, Member>> responses = Uni.combine().all().unis(memberUni, productUni, referralUni).asTuple();
    return responses.flatMap(tuple -> {
      Member member = tuple.getItem1();
      Product product = tuple.getItem2();
      Member referral = tuple.getItem3();

      if(referral != null) {
        // get the referral's product
        System.out.println("referral is found: " + referral);
        return repository.findById(MemberProductPK.of(referral.getId(), productId)).flatMap(referralProduct -> {
          System.out.println("referral product is found: " + referralProduct);
          MemberProduct memberProduct = MemberProduct.builder()
            .id(MemberProductPK.of(member.getId(), product.getId()))
            .parent(referralProduct)
            .point(BigDecimal.ZERO)
            .build();
            return repository.save(memberProduct);
        });
      } else {
        MemberProduct memberProduct = MemberProduct.builder()
          .id(MemberProductPK.of(member.getId(), product.getId()))
          .point(BigDecimal.ZERO)
          .build();
        return repository.save(memberProduct);
      }
    });
  }
}
