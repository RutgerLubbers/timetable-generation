package com.patrick.timetableappbackend.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
@Entity(name = "planning_constraint")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Constraint {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  // This is the constraint's name.
  private String description;

  @Enumerated(STRING)
  private ConstraintWeight weight;
}
