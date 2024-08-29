package com.patrick.timetableappbackend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy;
import com.patrick.timetableappbackend.model.Timetable;
import com.patrick.timetableappbackend.service.TimetableService;
import jakarta.websocket.server.PathParam;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
public class TimetableController {

  private final TimetableService timetableService;

  @GetMapping("/list")
  public Collection<String> list() {
    return timetableService.getJobIds();
  }

  @GetMapping
  public ResponseEntity<Timetable> generateTimetableData() {
    Timetable timetable = timetableService.getTimetableData();
    return new ResponseEntity<>(timetable, HttpStatus.OK);
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> solve(@RequestBody Timetable ignored) {
    // retrieve from db... and set constraint configuration.
    Timetable timetable = timetableService.getTimetableData();
    String jobId = timetableService.solve(timetable);
    Map<String, String> response = new HashMap<>();
    response.put("jobId", jobId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping(
      value = "/analyze",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ScoreAnalysis<HardSoftScore> analyze(
      @RequestBody Timetable problem,
      @RequestParam(name = "fetchPolicy", required = false) ScoreAnalysisFetchPolicy fetchPolicy) {
    return timetableService.analyze(problem, fetchPolicy);
  }

  @GetMapping(value = "/{jobId}", produces = APPLICATION_JSON_VALUE)
  public Timetable getTimeTable(@PathVariable("jobId") String jobId) {
    return timetableService.getTimetable(jobId);
  }

  @GetMapping(value = "/{jobId}/status", produces = APPLICATION_JSON_VALUE)
  public Timetable getStatus(@PathVariable("jobId") String jobId) {
    return timetableService.getStatus(jobId);
  }

  @DeleteMapping(value = "/{jobId}", produces = APPLICATION_JSON_VALUE)
  public Timetable terminateSolving(@PathParam("jobId") String jobId) {
    return timetableService.terminateSolving(jobId);
  }
}
