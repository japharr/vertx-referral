package com.japharr.referral.service;

import com.japharr.referral.entity.Member;
import com.japharr.referral.entity.MemberProduct;
import com.japharr.referral.entity.Product;
import com.japharr.referral.entity.enumeration.SharedPointType;
import com.japharr.referral.entity.key.MemberProductPK;
import com.japharr.referral.model.Temp;
import com.japharr.referral.pattern.sharedpoint.SharedPoint;
import com.japharr.referral.pattern.sharedpoint.SharedPointSupplier;
import com.japharr.referral.repository.MemberProductRepository;
import com.japharr.referral.repository.MemberRepository;
import com.japharr.referral.repository.ProductRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.tuples.Tuple3;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class MemberProductService {
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final MemberProductRepository repository;
  private final SharedPointSupplier sharedPointSupplier;

  public Uni<List<MemberProduct>> findAll() {
    return repository.findAll();
  }

  public Uni<List<MemberProduct>> findByProductId(Long productId) {
    return repository.findByProductId(productId);
  }

  public Uni<List<MemberProduct>> getParentList(Long productId, String referralCode) {
    Uni<Product> productUni = productRepository.findById(productId);
    Uni<Member> referralUni = memberRepository.findByReferralCode(referralCode);

    Uni<Tuple2<Product, Member>> responses = Uni.combine().all().unis(productUni, referralUni).asTuple();
    return responses.flatMap(tuple -> {
      Product product = tuple.getItem1();
      Member referral = tuple.getItem2();

      if(product != null & referral != null) {
        return repository.getParentList(product.getId(), referral.getId());
      } else {
        return Uni.createFrom().nullItem();
      }
    });
  }

  public Uni<List<MemberProduct>> creditMember(Long productId, String memberCode) {
    Uni<Product> productUni = productRepository.findById(productId);
    Uni<Member> memberUni = memberRepository.findByReferralCode(memberCode);

    Uni<Tuple2<Product, Member>> responses = Uni.combine().all().unis(productUni, memberUni).asTuple();
    return responses.flatMap(tuple -> {
      Product product = tuple.getItem1();
      Member member = tuple.getItem2();

      if(product != null & member != null) {
        Uni<List<MemberProduct>> parentList = repository.getParentList(product.getId(), member.getId());
        var type = product.getSharedPointType();

        var sp = product.getSharedPoint();
        return parentList.map(items -> {
          sharedPointSupplier.supplySharedPoint(type).compute(items, sp);
          return items;
        }).flatMap(repository::updateAll);
      } else {
        return Uni.createFrom().nullItem();
      }
    });
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
            .point(BigDecimal.valueOf(product.getBasePoint()))
            .build();
            return repository.save(memberProduct);
        });
      } else {
        MemberProduct memberProduct = MemberProduct.builder()
          .id(MemberProductPK.of(member.getId(), product.getId()))
          .point(BigDecimal.valueOf(product.getBasePoint()))
          .build();
        return repository.save(memberProduct);
      }
    });
  }
}
