package com.patrick.timetableappbackend.repository;

import com.patrick.timetableappbackend.model.Constraint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstraintRepo extends JpaRepository<Constraint, Long> {

  List<Constraint> findAllByOrderByIdAsc();
}
