package com.patrick.timetableappbackend.solver;

import static org.slf4j.LoggerFactory.getLogger;

import ai.timefold.solver.core.api.domain.solution.ConstraintWeightOverrides;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import com.patrick.timetableappbackend.model.ConstraintModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

public class ConstraintWeightOverridesImpl
    implements ConstraintWeightOverrides<HardMediumSoftScore> {

  /**
   * The logger to use.
   */
  private static final Logger LOGGER = getLogger(ConstraintWeightOverridesImpl.class);

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
    HardMediumSoftScore score = weightOverrides.get(constraintName);
    LOGGER.info("Constraint '{}' has weight '{}'.", constraintName, score);
    if (score == null) {
      return HardMediumSoftScore.ZERO;
    }
    return score;
  }

  @Override
  public Set<String> getKnownConstraintNames() {
    return weightOverrides.keySet();
  }
}
