package com.ilionx.timetable.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;

import lombok.*;


@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class StudentGroup {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Enumerated(STRING)
  private Year year;

  private String name;
  private String studentGroup;

  @Enumerated(STRING)
  private SemiGroup semiGroup;

  private Long numberOfStudents;
}
