package com.patrick.timetableappbackend.solver;

import ai.timefold.solver.core.api.domain.solution.ConstraintWeightOverrides;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import com.patrick.timetableappbackend.model.ConstraintModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstraintWeightOverridesImpl
    implements ConstraintWeightOverrides<HardMediumSoftScore> {

  private final Map<String, HardMediumSoftScore> weightOverrides = new HashMap<>();

  public ConstraintWeightOverridesImpl(List<ConstraintModel> constraintList) {
    constraintList.forEach(
        (constraint) -> {
          weightOverrides.put(constraint.getDescription(), toScore(constraint));
        });
  }

  private HardMediumSoftScore toScore(ConstraintModel constraint) {
    return switch (constraint.getWeight()) {
      case "HARD" -> HardMediumSoftScore.ONE_HARD;
      case "MEDIUM" -> HardMediumSoftScore.ONE_MEDIUM;
      case "SOFT" -> HardMediumSoftScore.ONE_SOFT;
      default -> HardMediumSoftScore.ZERO;
    };
  }

  @Override
  public HardMediumSoftScore getConstraintWeight(String constraintName) {
    return weightOverrides.get(constraintName);
  }

  @Override
  public Set<String> getKnownConstraintNames() {
    return weightOverrides.keySet();
  }
}
