package com.ilionx.timetable.solver.justifications;

import ai.timefold.solver.core.api.score.stream.ConstraintJustification;
import com.ilionx.timetable.model.Lesson;
import com.ilionx.timetable.model.Teacher;

public record TeacherRoomStabilityJustification(
    String teacher, Lesson lesson1, Lesson lesson2, String description)
    implements ConstraintJustification {

  public TeacherRoomStabilityJustification(Teacher teacher, Lesson lesson1, Lesson lesson2) {
    this(
        teacher.getName(),
        lesson1,
        lesson2,
        "Teacher '%s' has two lessons in different rooms: room '%s' at '%s %s' and room '%s' at '%s %s'"
            .formatted(
                teacher,
                lesson1.getRoom(),
                lesson1.getTimeslot().getDayOfWeek(),
                lesson1.getTimeslot().getStartTime(),
                lesson2.getRoom(),
                lesson2.getTimeslot().getDayOfWeek(),
                lesson2.getTimeslot().getStartTime()));
  }
}
