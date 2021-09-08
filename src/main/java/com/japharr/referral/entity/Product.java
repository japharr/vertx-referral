package com.japharr.referral.entity;

import com.japharr.referral.entity.enumeration.SharedPointType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "name", unique = true)
  private String name;

  @Column(name = "base_point")
  private Double basePoint = 0.0;
  @Column(name = "shared_point")
  private Double sharedPoint = 0.0;
  @Column(name = "shared_point_include_member")
  private boolean sharedPointIncludeMember;
  @Column(name = "shared_point_type")
  private SharedPointType sharedPointType = SharedPointType.PARALLEL;
}
