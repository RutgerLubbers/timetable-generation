package com.ilionx.timetable.service;

import static java.util.UUID.randomUUID;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.api.solver.SolverStatus;
import com.ilionx.timetable.exception.TimetableSolverException;
import com.ilionx.timetable.model.Constraint;
import com.ilionx.timetable.model.Lesson;
import com.ilionx.timetable.model.Room;
import com.ilionx.timetable.model.Timeslot;
import com.ilionx.timetable.model.Timetable;
import com.ilionx.timetable.repository.ConstraintRepo;
import com.ilionx.timetable.repository.LessonRepo;
import com.ilionx.timetable.repository.RoomRepo;
import com.ilionx.timetable.repository.TimeslotRepo;
import com.ilionx.timetable.solver.ConstraintWeightOverridesImpl;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimetableService.class);

  private final RoomRepo roomRepo;
  private final TimeslotRepo timeslotRepo;
  private final LessonRepo lessonRepo;
  private final ConstraintRepo constraintRepo;
  private final SolverManager<Timetable, String> solverManager;
  private final SolutionManager<Timetable, HardSoftScore> solutionManager;

  @Value("${timefold.solver.termination.spent-limit}")
  private Duration duration;

  // TODO: Without any "time to live", the map may eventually grow out of memory.
  private final ConcurrentMap<String, Job> jobIdToJob = new ConcurrentHashMap<>();

  public Collection<String> getJobIds() {
    return jobIdToJob.keySet();
  }

  public Timetable getTimetableData() {

    Long problemDuration = duration.getSeconds();

     List<Timeslot> timeslots = timeslotRepo.findAll();
     List<Room> rooms = roomRepo.findAll();
     List<Constraint> constraints = constraintRepo.findAll();
    //    final TimetableConstraintConfiguration timetableConstraintConfiguration =
    //        new TimetableConstraintConfiguration(constraintModels);
     List<Lesson> lessons = lessonRepo.findAll();

    return new Timetable(
        timeslots,
        rooms,
        lessons,
        new ConstraintWeightOverridesImpl(constraints),
        problemDuration);
  }

  // How to integrate with Spring JPA to persist the Timetable solution
  // How to get the best solution
  public String solve(Timetable problem) {
     ConcurrentMap<String, Timetable> timetableSolution = new ConcurrentHashMap<>();
    String jobId = randomUUID().toString();
    jobIdToJob.put(jobId, Job.ofTimetable(problem));
    solverManager
        .solveBuilder()
        .withProblemId(jobId)
        // todo: to see how to implement this termination Config properly on a new version of
        // Timefold
        // no need to add duration because we take it from application.properties
        //                .withConfigOverride(new SolverConfigOverride<Timetable>()
        //                        .withTerminationConfig(new
        // TerminationConfig().withMinutesSpentLimit(problem.getDuration())))
        .withProblemFinder(jobId_ -> jobIdToJob.get(jobId).timetable)
        .withBestSolutionConsumer(solution -> jobIdToJob.put(jobId, Job.ofTimetable(solution)))
        // .withFinalBestSolutionConsumer(solution -> jobIdToJob/timetableSolution.put(jobId,
        // solution))
        .withExceptionHandler(
            (jobId_, exception) -> {
              jobIdToJob.put(jobId, Job.ofException(exception));
              LOGGER.error("Failed solving jobId ({}).", jobId, exception);
            })
        .run();
    return jobId;
  }

  public ScoreAnalysis<HardSoftScore> analyze(
      Timetable problem, ScoreAnalysisFetchPolicy fetchPolicy) {
    return fetchPolicy == null
        ? solutionManager.analyze(problem)
        : solutionManager.analyze(problem, fetchPolicy);
  }

  public Timetable getTimetable(String jobId) {
    Timetable timetable = getTimetableAndCheckForExceptions(jobId);
    SolverStatus solverStatus = solverManager.getSolverStatus(jobId);
    return timetable.toBuilder().solverStatus(solverStatus).build();
  }

  public Timetable getStatus(String jobId) {
    return getTimetable(jobId);
  }

  public Timetable terminateSolving(String jobId) {
    // TODO: Replace with .terminateEarlyAndWait(... [, timeout]); see
    // https://github.com/TimefoldAI/timefold-solver/issues/77
    solverManager.terminateEarly(jobId);
    return getTimetable(jobId);
  }

  private Timetable getTimetableAndCheckForExceptions(String jobId) {
    Job job = jobIdToJob.get(jobId);
    if (job == null) {
      throw new TimetableSolverException(jobId, HttpStatus.NOT_FOUND, "No timetable found.");
    }
    if (job.exception != null) {
      throw new TimetableSolverException(jobId, job.exception);
    }
    return job.timetable;
  }

  private record Job(Timetable timetable, Throwable exception) {

    static Job ofTimetable(Timetable timetable) {
      return new Job(timetable, null);
    }

    static Job ofException(Throwable error) {
      return new Job(null, error);
    }
  }
}
