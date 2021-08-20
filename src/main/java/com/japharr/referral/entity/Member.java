package com.japharr.referral.entity;

public class Member {
  private long id;
  private String name;
  private String email;

  public Member() {}

  public Member(long id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }
}
