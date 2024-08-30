package com.ilionx.timetable.config;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import com.ilionx.timetable.model.Lesson;
import com.ilionx.timetable.model.Timetable;
import com.ilionx.timetable.solver.TimetableConstraintProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ConstraintConfig {

  @Bean
  public ConstraintVerifier<TimetableConstraintProvider, Timetable> buildConstraintVerifier() {
    return ConstraintVerifier.build(
        new TimetableConstraintProvider(), Timetable.class, Lesson.class);
  }
}
