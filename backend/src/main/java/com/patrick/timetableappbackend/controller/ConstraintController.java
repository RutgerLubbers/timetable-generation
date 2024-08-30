package com.patrick.timetableappbackend.controller;

import com.patrick.timetableappbackend.model.Constraint;
import com.patrick.timetableappbackend.service.ConstraintService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/constraints")
@RequiredArgsConstructor
@Slf4j
public class ConstraintController {

  private final ConstraintService constraintService;

  @GetMapping
  public ResponseEntity<List<Constraint>> getAllConstraints() {
    List<Constraint> constraints = constraintService.getAllConstraints();
    return new ResponseEntity<>(constraints, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Constraint> getConstraintById(@PathVariable Long id) {
    return constraintService
        .getConstraintById(id)
        .map(constraint -> new ResponseEntity<>(constraint, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getConstraintCount() {
    long constraintCount = constraintService.getConstraintCount();
    return new ResponseEntity<>(constraintCount, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Constraint> createConstraint(
      @RequestBody Constraint constraint) {
    Constraint createdConstraint = constraintService.createConstraint(constraint);
    return new ResponseEntity<>(createdConstraint, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Constraint> updateConstraint(
      @PathVariable Long id, @RequestBody Constraint updatedConstraint) {
    try {
      Constraint updated = constraintService.updateConstraint(id, updatedConstraint);
      return new ResponseEntity<>(updated, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteConstraint(@PathVariable Long id) {
    constraintService.deleteConstraint(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
