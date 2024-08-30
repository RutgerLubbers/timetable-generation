package com.ilionx.timetable.model;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import jakarta.persistence.Id;

import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;

import java.util.Set;
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
@ToString
@NoArgsConstructor
public class Teacher {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String name;

  @EqualsAndHashCode.Exclude
  @JoinTable
  @JsonIdentityReference
  @ManyToMany(fetch = EAGER)
  @ToString.Exclude
  private Set<Timeslot> timeslots = new HashSet<>();

}
