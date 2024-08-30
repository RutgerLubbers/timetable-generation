package com.ilionx.timetable.service;

import com.ilionx.timetable.model.Constraint;
import com.ilionx.timetable.repository.ConstraintRepo;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConstraintService {

  private final ConstraintRepo constraintRepo;

  public List<Constraint> getAllConstraints() {
    return constraintRepo.findAllByOrderByIdAsc();
  }

  public Optional<Constraint> getConstraintById(Long id) {
    return constraintRepo.findById(id);
  }

  public long getConstraintCount() {
    return constraintRepo.count();
  }

  public Constraint createConstraint(Constraint constraint) {
    return constraintRepo.save(constraint);
  }

  public Constraint updateConstraint(Long id, Constraint updatedConstraint) {
    if (constraintRepo.existsById(id)) {
      updatedConstraint =
          Constraint.builder()
              .id(id)
              .description(updatedConstraint.getDescription())
              .weight(updatedConstraint.getWeight())
              .build();
      return constraintRepo.save(updatedConstraint);
    } else {
      throw new RuntimeException("Constraint not found with id: " + id);
    }
  }

  public void deleteConstraint(Long id) {
    constraintRepo.deleteById(id);
  }
}
