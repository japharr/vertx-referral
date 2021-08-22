package com.japharr.referral.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@Entity
@Table(name = "members")
public class Member {
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String email;

  @Column(name = "referral_code", unique = true)
  private String referralCode;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Member member = (Member) o;

    return Objects.equals(id, member.id);
  }

  @Override
  public int hashCode() {
    return 2023240484;
  }
}
