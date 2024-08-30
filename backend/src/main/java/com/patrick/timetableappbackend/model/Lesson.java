package com.patrick.timetableappbackend.model;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.patrick.timetableappbackend.utils.LessonStrengthComparator;
import com.patrick.timetableappbackend.utils.RoomStrengthComparator;
import com.patrick.timetableappbackend.utils.TimeslotStrengthComparator;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

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

  @JoinColumn(name = "teacher_id")
  @ManyToOne(fetch = EAGER, cascade = MERGE)
  private Teacher teacher;

  @JoinColumn(name = "student_group_id")
  @ManyToOne(fetch = EAGER, cascade = MERGE)
  private StudentGroup studentGroup;

  @Enumerated(STRING)
  private LessonType lessonType;

  @Enumerated(STRING)
  private Year year;

  // Duration in minutes
  private int duration;

  @JoinColumn(name = "timeslot_id")
  @JsonIdentityReference
  @ManyToOne(fetch = EAGER)
  @PlanningVariable(strengthComparatorClass = TimeslotStrengthComparator.class)
  private Timeslot timeslot;

  @JoinColumn(name = "room_id")
  @JsonIdentityReference
  @ManyToOne(fetch = EAGER)
  @PlanningVariable(strengthComparatorClass = RoomStrengthComparator.class)
  private Room room;

}
