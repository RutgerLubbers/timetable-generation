package com.patrick.timetableappbackend.model;

import static jakarta.persistence.FetchType.EAGER;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  private String name;

  @JsonIdentityReference
  @ManyToMany(fetch = EAGER)
  @JoinTable(
      name = "teacher_timeslot",
      joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "timeslot_id", referencedColumnName = "id"))
  @ToString.Exclude
  private Set<Timeslot> timeslots = new HashSet<>(); // teacher preffered Timeslots

  // todo: replace timeslots with periods (9AM to 4PM)

  // should I add a @OneToMany/@ManyToMany relationship with Lessons and make it optional?

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Teacher teacher = (Teacher) o;
    return id != null && Objects.equals(id, teacher.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
