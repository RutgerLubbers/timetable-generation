package com.ilionx.timetable.repository;

import com.ilionx.timetable.model.Constraint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstraintRepo extends JpaRepository<Constraint, Long> {

  List<Constraint> findAllByOrderByIdAsc();
}
