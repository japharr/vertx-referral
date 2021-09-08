package com.japharr.referral.entity.enumeration;

public enum SharedPointType {
  PARALLEL, // divide shared-point by number of member
  TOP_BOTTOM, // % of shared-point by member-level decreasing from top to bottom
  BOTTOM_TOP // % of shared-point by member-level decreasing from bottom to top
}
