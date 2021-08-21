package com.japharr.referral.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(long id) {
    super("Post id: " + id + " was not found. ");
  }
}
