package com.ilionx.timetable.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.*;


@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
@EqualsAndHashCode
@JsonIdentityInfo(scope = Timeslot.class,generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@NoArgsConstructor
@ToString
public class Timeslot {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @PlanningId
  private Long id;

  @Enumerated(STRING)
  private DayOfWeek dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;

}

