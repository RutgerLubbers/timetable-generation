package com.patrick.timetableappbackend.model;

import ai.timefold.solver.core.api.domain.solution.ConstraintWeightOverrides;
import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.solver.SolverStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@PlanningSolution
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {

  //    private String name;

  @ProblemFactCollectionProperty @ValueRangeProvider private List<Timeslot> timeslots;
  @ProblemFactCollectionProperty @ValueRangeProvider private List<Room> rooms;
  @PlanningEntityCollectionProperty private List<Lesson> lessons;

  @JsonIgnore private ConstraintWeightOverrides<HardMediumSoftScore> constraintConfiguration;

  @PlanningScore private HardMediumSoftScore score;

  // Ignored by Timefold, used by the UI to display solve or stop solving button
  private SolverStatus solverStatus;

  private Long duration;

  public Timetable(
      List<Timeslot> timeslots,
      List<Room> rooms,
      List<Lesson> lessons,
      ConstraintWeightOverrides<HardMediumSoftScore> constraintConfiguration,
      Long duration) {
    this.timeslots = timeslots;
    this.rooms = rooms;
    this.lessons = lessons;
    this.constraintConfiguration = constraintConfiguration;
    this.duration = duration;
  }
}
