package com.ilionx.timetable.model;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.ilionx.timetable.utils.LessonStrengthComparator;
import com.ilionx.timetable.utils.RoomStrengthComparator;
import com.ilionx.timetable.utils.TimeslotStrengthComparator;
import jakarta.persistence.Entity;

import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@PlanningEntity(difficultyComparatorClass = LessonStrengthComparator.class)
@ToString
public class Lesson {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @PlanningId
  private Long id;

  private String subject;

  @JoinColumn
  @ManyToOne(fetch = EAGER, cascade = MERGE)
  private Teacher teacher;

  @JoinColumn
  @ManyToOne(fetch = EAGER, cascade = MERGE)
  private StudentGroup studentGroup;

  @Enumerated(STRING)
  private LessonType lessonType;

  @Enumerated(STRING)
  private Year year;

  // Duration in minutes
  private int duration;

  @JoinColumn
  @JsonIdentityReference
  @ManyToOne(fetch = EAGER)
  @PlanningVariable(strengthComparatorClass = TimeslotStrengthComparator.class)
  private Timeslot timeslot;

  @JoinColumn
  @JsonIdentityReference
  @ManyToOne(fetch = EAGER)
  @PlanningVariable(strengthComparatorClass = RoomStrengthComparator.class)
  private Room room;

}
