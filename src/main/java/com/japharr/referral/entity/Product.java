package com.japharr.referral.entity;

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
}
