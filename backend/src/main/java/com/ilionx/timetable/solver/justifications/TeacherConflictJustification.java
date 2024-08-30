package com.ilionx.timetable.solver.justifications;

import ai.timefold.solver.core.api.score.stream.ConstraintJustification;
import com.ilionx.timetable.model.Lesson;
import com.ilionx.timetable.model.Teacher;

public record TeacherConflictJustification(
    String teacher, Lesson lesson1, Lesson lesson2, String description)
    implements ConstraintJustification {

  public TeacherConflictJustification(Teacher teacher, Lesson lesson1, Lesson lesson2) {
    this(
        teacher.toString(),
        lesson1,
        lesson2,
        "Teacher '%s' needs to teach lesson '%s' for student group '%s' and lesson '%s' for student group '%s' at '%s %s'"
            .formatted(
                teacher,
                lesson1.getSubject(),
                lesson1.getStudentGroup(),
                lesson2.getSubject(),
                lesson2.getStudentGroup(),
                lesson1.getTimeslot().getDayOfWeek(),
                lesson1.getTimeslot().getStartTime()));
  }
}
