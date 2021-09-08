package com.japharr.referral.pattern.sharedpoint;

import com.japharr.referral.entity.enumeration.SharedPointType;
import com.japharr.referral.pattern.sharedpoint.factory.BottomTop;
import com.japharr.referral.pattern.sharedpoint.factory.Parallel;
import com.japharr.referral.pattern.sharedpoint.factory.TopBottom;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.japharr.referral.entity.enumeration.SharedPointType.PARALLEL;
import static com.japharr.referral.entity.enumeration.SharedPointType.BOTTOM_TOP;
import static com.japharr.referral.entity.enumeration.SharedPointType.TOP_BOTTOM;

@Component
public class SharedPointSupplier {
  private static final Map<SharedPointType, Supplier<SharedPoint>>
    PLAYER_SUPPLIER = Map.of(PARALLEL, Parallel::new, BOTTOM_TOP, BottomTop::new, TOP_BOTTOM, TopBottom::new);

  public SharedPoint supplySharedPoint(SharedPointType type) {
    Supplier<SharedPoint> player = PLAYER_SUPPLIER.get(type);

    if (player == null) {
      throw new IllegalArgumentException("Invalid player type: " + type);
    }

    return player.get();
  }
}
