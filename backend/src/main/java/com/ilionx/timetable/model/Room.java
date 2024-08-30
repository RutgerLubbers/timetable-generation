package com.ilionx.timetable.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import jakarta.persistence.Id;

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
@JsonIdentityInfo(scope = Room.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@ToString
public class Room {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @PlanningId
  private Long id;

  private String name;
  private Long capacity;
  private String building;

}
