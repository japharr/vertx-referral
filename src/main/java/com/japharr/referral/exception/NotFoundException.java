package com.japharr.referral.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(Object id) {
    super("Post id: " + id + " was not found. ");
  }
}
