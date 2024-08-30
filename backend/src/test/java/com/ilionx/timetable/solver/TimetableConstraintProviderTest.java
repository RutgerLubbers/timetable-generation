package com.ilionx.timetable.solver;

import static com.ilionx.timetable.model.LessonType.COURSE;
import static com.ilionx.timetable.model.LessonType.LABORATORY;
import static com.ilionx.timetable.model.LessonType.SEMINAR;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.LocalTime.NOON;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import com.ilionx.timetable.model.Lesson;
import com.ilionx.timetable.model.LessonType;
import com.ilionx.timetable.model.Room;
import com.ilionx.timetable.model.StudentGroup;
import com.ilionx.timetable.model.Teacher;
import com.ilionx.timetable.model.Teacher.TeacherBuilder;
import com.ilionx.timetable.model.Timeslot;
import com.ilionx.timetable.model.Timetable;
import com.ilionx.timetable.model.Year;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TimetableConstraintProviderTest {

  private static final Room ROOM1 = aRoom(1L, "Room1");
  private static final Room ROOM2 = aRoom(2L, "Room2");
  private static final Timeslot TIMESLOT1 = aTimeslot(1L, MONDAY, NOON);
  private static final Timeslot TIMESLOT2 = aTimeslot(2L, TUESDAY, NOON);
  private static final Timeslot TIMESLOT3 = aTimeslot(3L, TUESDAY, NOON.plusMinutes(150));
  private static final Timeslot TIMESLOT4 = aTimeslot(4L, TUESDAY, NOON.plusHours(3));
  private static final Timeslot TIMESLOT5 = aTimeslot(5L, TUESDAY, NOON.plusHours(5));
  private static final Timeslot TIMESLOT6 = aTimeslot(5L, TUESDAY, NOON.plusHours(7));
  private static final Timeslot TIMESLOT7 = aTimeslot(5L, TUESDAY, NOON.plusHours(9));

  @Autowired ConstraintVerifier<TimetableConstraintProvider, Timetable> constraintVerifier;

  private static Lesson aLesson(Long id,
      String subject,
      Teacher teacher,
      StudentGroup studentGroup,
      Timeslot timeslot,
      Room room) {
    return aLesson(id, subject, null, teacher, studentGroup, timeslot, room);
  }

  private static Lesson aLesson(Long id,
      String subject,
      Teacher teacher,
      StudentGroup studentGroup,
      Integer hours,
      Timeslot timeslot,
      Room room) {
    return aLesson(id, subject, null, teacher, studentGroup, hours, timeslot, room);
  }

  private static Lesson aLesson(
      Long id,
      String subject,
      LessonType lessonType,
      Teacher teacher,
      StudentGroup studentGroup,
      Timeslot timeslot,
      Room room) {
    return aLesson(id, subject, lessonType, teacher, studentGroup, null, timeslot, room);
  }

  private static Lesson aLesson(
      Long id,
      String subject,
      LessonType lessonType,
      Teacher teacher,
      StudentGroup studentGroup,
      Integer hours,
      Timeslot timeslot,
      Room room) {
    return Lesson.builder()
        .id(id)
        .duration(hours == null ? 0 : hours)
        .lessonType(lessonType)
        .room(room)
        .subject(subject)
        .teacher(teacher)
        .studentGroup(studentGroup)
        .timeslot(timeslot)
        .build();
  }

  private static Room aRoom(Long id,
      String name) {
    return aRoom(id, name, null);
  }

  private static Room aRoom(Long id,
      String name,
      Long capacity) {
    return aRoom(id, name, capacity, null);
  }

  private static Room aRoom(Long id,
      String name,
      Long capacity,
      String building) {
    return Room.builder()
        .id(id)
        .building(building)
        .capacity(capacity)
        .name(name)
        .build();
  }

  private static StudentGroup aStudentGroup(long id,
      String name) {
    return aStudentGroup(id, null, name, 30L);
  }

  private static  StudentGroup aStudentGroup(long id,
      Year year,
      String name,
      Long numberOfStudents) {
    return StudentGroup.builder()
        .id(id)
        .name(name)
        .numberOfStudents(numberOfStudents)
        .year(year)
        .build();
  }

  private static  StudentGroup aStudentGroup(long id,
      Year year,
      String name,
      String studentGroup) {
    return StudentGroup.builder()
        .id(id)
        .name(name)
        .numberOfStudents(0L)
        .studentGroup(studentGroup)
        .year(year)
        .build();
  }
  private static Teacher aTeacher(long id,
      String name,
      Timeslot... timeslots) {
    TeacherBuilder builder = Teacher.builder()
        .id(id)
        .name(name);
    if (timeslots != null) {
      builder.timeslots(Set.of(timeslots));
    }
    return builder
        .build();
  }

  private static Timeslot aTimeslot(Long id,
      DayOfWeek dayOfWeek,
      LocalTime startTime,
      LocalTime endTime) {
    return Timeslot.builder()
        .id(id)
        .dayOfWeek(dayOfWeek)
        .endTime(endTime)
        .startTime(startTime)
        .build();
  }

  private static Timeslot aTimeslot(Long id,
      DayOfWeek dayOfWeek,
      LocalTime startTime) {
    return aTimeslot(id, dayOfWeek, startTime, startTime.plusMinutes(120));
  }

  @Test
  void capacityRoomConflict() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    StudentGroup studentGroup2 = aStudentGroup(2L, "Group1");

    Room room = aRoom(1L, "sala1", 60L);

    Lesson firstTuesdayLesson =
        aLesson(1L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, TIMESLOT2, room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L, "subject1", aTeacher(1L, "Teacher1"), studentGroup2, TIMESLOT2, room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::capacityRoomConflict)
        .given(firstTuesdayLesson, secondTuesdayLesson)
        .penalizesBy(0);
  }

  @Test
  void coursesGroupedInTheSameTimeslot() {

    StudentGroup studentGroup = aStudentGroup(1L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup2 = aStudentGroup(2L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup4 = aStudentGroup(4L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup3 = aStudentGroup(3L, Year.FIRST, "Group2", "2G");

    Room room = aRoom(1L, "sala1", 60L);
    Room room1 = aRoom(2L, "sala2", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT2,
            room1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::coursesGroupedInTheSameTimeslot)
        .given(firstTuesdayLesson, secondTuesdayLesson)
        .penalizesBy(1);
  }

  @Test
  void coursesInTheSameBuilding() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");

    Room room = aRoom(1L, "sala1", 60L, "Precis");
    Room room2 = aRoom(2L, "sala2", 60L, "EC");
    Room room3 = aRoom(3L, "sala2", 60L, "EC");

    Timeslot timeslot = aTimeslot(1L, TUESDAY, NOON);
    Timeslot timeslot1 = aTimeslot(2L, TUESDAY, NOON.plusHours(2));
    Timeslot timeslot2 = aTimeslot(3L, TUESDAY, NOON.plusHours(4));

    Lesson tuesdayLesson =
        aLesson(
            1L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            timeslot,
            room2);
    Lesson thirdTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            timeslot1,
            room3);
    Lesson fourthTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            timeslot2,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::coursesInTheSameBuilding)
        .given(tuesdayLesson, thirdTuesdayLesson, fourthTuesdayLesson)
        .rewardsWith(1);
  }

  @Test
  void labsGroupedInTheSameTimeslot() {

    StudentGroup studentGroup = aStudentGroup(1L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup2 = aStudentGroup(2L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup4 = aStudentGroup(4L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup3 = aStudentGroup(3L, Year.FIRST, "Group2", "2G");

    Room room = aRoom(1L, "sala1", 60L);
    Room room1 = aRoom(2L, "sala2", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT3,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::labsGroupedInTheSameTimeslot)
        .given(firstTuesdayLesson, secondTuesdayLesson)
        .penalizesBy(1);
  }

  @Test
  void lessonDurationConflict() {

    StudentGroup studentGroup3 = aStudentGroup(3L, "Group2");

    Timeslot timeslot = aTimeslot(8L, DayOfWeek.WEDNESDAY, NOON);

    Room room = aRoom(1L, "sala1", 60L);

    Lesson conflictLesson =
        aLesson(
            1L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            3,
            timeslot,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::lessonDurationConflict)
        .given(conflictLesson)
        .penalizesBy(1);
  }

  @Test
  void maximmumCoursesForStudents() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    StudentGroup studentGroup2 = aStudentGroup(2L, "Group2");

    Timeslot timeslot = aTimeslot(8L, MONDAY, NOON.plusHours(2));
    Timeslot timeslot8 = aTimeslot(8L, TUESDAY, NOON.plusHours(11));

    Lesson mondayLesson =
        aLesson(
            1L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, 2, TIMESLOT1, ROOM1);
    Lesson secondLesson =
        aLesson(
            11L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, 2, timeslot, ROOM1);
    Lesson firstTuesdayLesson =
        aLesson(
            2L,
            "subject2",
            aTeacher(2L, "Teacher2", TIMESLOT3),
            studentGroup,
            2,
            TIMESLOT2,
            ROOM1);
    Lesson tuesdayLesson =
        aLesson(
            3L, "subject3", aTeacher(3L, "Teacher3"), studentGroup2, 2, TIMESLOT3, ROOM1);
    Lesson secondTuesdayLesson =
        aLesson(
            4L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, 2, TIMESLOT3, ROOM1);
    Lesson thirdTuesdayLesson =
        aLesson(
            5L,
            "subject2",
            aTeacher(2L, "Teacher2", TIMESLOT3),
            studentGroup,
            2,
            TIMESLOT4,
            ROOM1);
    Lesson tuesdayLesson1 =
        aLesson(
            6L, "subject3", aTeacher(3L, "Teacher3"), studentGroup2, 2, TIMESLOT4, ROOM1);
    Lesson fourthTuesdayLesson =
        aLesson(
            7L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, 2, TIMESLOT5, ROOM1);
    Lesson tuesdayLesson2 =
        aLesson(
            8L,
            "subject2",
            aTeacher(2L, "Teacher2", TIMESLOT3),
            studentGroup2,
            2,
            TIMESLOT2,
            ROOM1);
    Lesson fifthTuesdayLesson =
        aLesson(
            9L, "subject3", aTeacher(3L, "Teacher3"), studentGroup, 2, TIMESLOT6, ROOM1);
    Lesson sixthTuesdayLesson =
        aLesson(
            10L, "subject4", aTeacher(3L, "Teacher3"), studentGroup, 2, TIMESLOT7, ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::maximumCoursesForStudents)
        .given(
            mondayLesson,
            secondLesson,
            firstTuesdayLesson,
            tuesdayLesson,
            secondTuesdayLesson,
            thirdTuesdayLesson,
            tuesdayLesson1,
            fourthTuesdayLesson,
            tuesdayLesson2,
            fifthTuesdayLesson,
            sixthTuesdayLesson)
        .penalizesBy(2);
  }

  @Test
  void maximmumCoursesTeached() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    StudentGroup studentGroup2 = aStudentGroup(2L, "Group1");
    StudentGroup studentGroup3 = aStudentGroup(3L, "Group2");

    Timeslot timeslot = aTimeslot(8L, DayOfWeek.WEDNESDAY, NOON);
    Timeslot timeslot2 = aTimeslot(9L, DayOfWeek.WEDNESDAY, NOON.plusHours(2));
    Timeslot timeslot3 = aTimeslot(10L, DayOfWeek.WEDNESDAY, NOON.plusHours(4));
    Timeslot timeslot4 = aTimeslot(11L, DayOfWeek.WEDNESDAY, NOON.plusHours(6));
    Timeslot timeslot5 = aTimeslot(12L, DayOfWeek.WEDNESDAY, NOON.plusHours(8));

    Room room = aRoom(1L, "sala1", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT2,
            room);
    Lesson tuesdayLesson =
        aLesson(
            3L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT2,
            room);
    Lesson thirdTuesdayLesson =
        aLesson(
            4L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT3,
            room);
    Lesson fourthTuesdayLesson =
        aLesson(
            6L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT3,
            room);
    Lesson anotherTuesdayLesson =
        aLesson(
            5L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT4,
            room);
    Lesson fifthTuesdayLesson =
        aLesson(
            7L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT5,
            room);
    Lesson sixthTuesdayLesson =
        aLesson(
            8L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT6,
            room);
    Lesson seventhTuesdayLesson =
        aLesson(
            9L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT7,
            room);
    Lesson eightTuesdayLesson =
        aLesson(
            10L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            timeslot,
            room);
    Lesson ninethTuesdayLesson =
        aLesson(
            11L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            timeslot2,
            room);
    Lesson tenthTuesdayLesson =
        aLesson(
            12L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            timeslot3,
            room);
    Lesson eleventhTuesdayLesson =
        aLesson(
            13L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            timeslot4,
            room);
    Lesson twelvethTuesdayLesson =
        aLesson(
            14L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            timeslot5,
            room);
    Lesson tuesday13Lesson =
        aLesson(
            15L,
            "subject1",
            SEMINAR,
            aTeacher(2L, "Teacher1"),
            studentGroup3,
            timeslot3,
            room);
    Lesson tuesday14Lesson =
        aLesson(
            16L,
            "subject1",
            SEMINAR,
            aTeacher(2L, "Teacher1"),
            studentGroup3,
            timeslot4,
            room);
    Lesson tuesday15Lesson =
        aLesson(
            17L,
            "subject1",
            SEMINAR,
            aTeacher(2L, "Teacher1"),
            studentGroup3,
            timeslot5,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::maximmumCoursesTeached)
        .given(
            firstTuesdayLesson,
            secondTuesdayLesson,
            tuesdayLesson,
            thirdTuesdayLesson,
            anotherTuesdayLesson,
            fourthTuesdayLesson,
            fifthTuesdayLesson,
            sixthTuesdayLesson,
            seventhTuesdayLesson,
            eightTuesdayLesson,
            ninethTuesdayLesson,
            tenthTuesdayLesson,
            eleventhTuesdayLesson,
            twelvethTuesdayLesson,
            tuesday13Lesson,
            tuesday14Lesson,
            tuesday15Lesson)
        .penalizesBy(0);
  }

  @Test
  void overlappingTimeslots() {

    StudentGroup studentGroup3 = aStudentGroup(3L, "Group2");

    Timeslot timeslot = aTimeslot(8L, DayOfWeek.WEDNESDAY, NOON);
    Timeslot timeslot2 = aTimeslot(9L, DayOfWeek.WEDNESDAY, NOON, NOON.plusHours(1));
    Timeslot timeslot3 = aTimeslot(10L, DayOfWeek.WEDNESDAY, NOON.plusHours(4));
    Timeslot timeslot4 = aTimeslot( 11L, DayOfWeek.WEDNESDAY, NOON.plusHours(4), NOON.plusHours(5));
    Timeslot timeslot5 = aTimeslot(12L, DayOfWeek.WEDNESDAY, NOON.plusHours(8));
    Timeslot timeslot6 = aTimeslot( 12L, DayOfWeek.WEDNESDAY, NOON.plusHours(7), NOON.plusHours(10));

    Room room = aRoom(1L, "sala1", 60L);

    Teacher teacher = aTeacher(1L, "Teacher1");
    Lesson eightTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot,
            room);
    Lesson ninethTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot2,
            room);
    Lesson tenthTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot3,
            room);
    Lesson eleventhTuesdayLesson =
        aLesson(
            4L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot4,
            room);
    Lesson twelvethTuesdayLesson =
        aLesson(
            5L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot5,
            room);
    Lesson nextLesson =
        aLesson(
            6L,
            "subject1",
            SEMINAR,
            teacher,
            studentGroup3,
            timeslot6,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::overlappingTimeslot)
        .given(
            eightTuesdayLesson,
            ninethTuesdayLesson,
            tenthTuesdayLesson,
            eleventhTuesdayLesson,
            twelvethTuesdayLesson,
            nextLesson)
        .penalizesBy(3);
  }

  @Test
  void roomConflict() {

    Lesson firstLesson = aLesson(1L, "Subject1",
        aTeacher(1L, "Teacher1"),
        aStudentGroup(1L, "Group1"),
        TIMESLOT1,
        ROOM1);

    Lesson conflictingLesson = aLesson(2L, "Subject2",
        aTeacher(2L, "Teacher2"),
        aStudentGroup(2L, "Group2"),
        TIMESLOT1,
        ROOM1);

    Lesson nonConflictingLesson = aLesson(3L, "Subject3",
        aTeacher(3L, "Teacher3"),
        aStudentGroup(3L, "Group3"),
        TIMESLOT2,
        ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::roomConflict)
        .given(firstLesson, conflictingLesson, nonConflictingLesson)
        .penalizesBy(1);
  }

  @Test
  void roomConflictUniversity() {

    // test more use cases
    Lesson firstLesson =
        aLesson(
            1L,
            "Subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            aStudentGroup(1L, Year.FIRST, "Group1", "1"),
            TIMESLOT1,
            ROOM1);
    Lesson conflictingLesson =
        aLesson(
            2L,
            "Subject1",
            LABORATORY,
            aTeacher(2L, "Teacher2"),
            aStudentGroup(2L, Year.FIRST, "Group1", "1"),
            TIMESLOT1,
            ROOM1);
    Lesson nonConflictingLesson =
        aLesson(
            3L,
            "Subject3",
            SEMINAR,
            aTeacher(3L, "Teacher3"),
            aStudentGroup(3L, Year.FIRST, "Group1", "1"),
            TIMESLOT2,
            ROOM1);
    Lesson conflictingLesson2 =
        aLesson(
            4L,
            "Subject4",
            SEMINAR,
            aTeacher(4L, "Teacher4"),
            aStudentGroup(4L, Year.FIRST, "Group1", "1"),
            TIMESLOT2,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::roomConflictUniversity)
        .given(firstLesson, conflictingLesson, nonConflictingLesson, conflictingLesson2)
        .penalizesBy(2);
  }

  @Test
  void seminarGroupedInTheSameTimeslot() {

    StudentGroup studentGroup = aStudentGroup(1L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup2 = aStudentGroup(2L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup4 = aStudentGroup(4L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup3 = aStudentGroup(3L, Year.FIRST, "Group2", "2G");

    Room room = aRoom(1L, "sala1", 60L);
    Room room1 = aRoom(2L, "sala2", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT3,
            room);
    Lesson thirdTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup4,
            TIMESLOT3,
            room);
    Lesson fourthTuesdayLesson =
        aLesson(
            4L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup4,
            TIMESLOT3,
            room);
    Lesson fifthTuesdayLesson =
        aLesson(
            5L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT3,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::seminarsGroupedInTheSameTimeslot)
        .given(
            firstTuesdayLesson,
            secondTuesdayLesson,
            thirdTuesdayLesson,
            fourthTuesdayLesson,
            fifthTuesdayLesson)
        .penalizesBy(1);
  }

  @Test
  void studentGroupConflict() {

    StudentGroup conflictingGroup = aStudentGroup(1L, "Group1");

    Lesson firstLesson =
        aLesson(
            1L, "Subject1", aTeacher(1L, "Teacher1"), conflictingGroup, TIMESLOT1, ROOM1);
    Lesson conflictingLesson =
        aLesson(
            2L, "Subject2", aTeacher(2L, "Teacher2"), conflictingGroup, TIMESLOT1, ROOM2);
    Lesson nonConflictingLesson =
        aLesson(
            3L,
            "Subject3",
            aTeacher(3L, "Teacher3"),
            aStudentGroup(3L, "Group3"),
            TIMESLOT2,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::studentGroupConflict)
        .given(firstLesson, conflictingLesson, nonConflictingLesson)
        .penalizesBy(1);
  }

  @Test
  void studentGroupSubjectVariety() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    String repeatedSubject = "Subject1";

    Lesson mondayLesson =
        aLesson(
            1L, repeatedSubject, aTeacher(1L, "Teacher1"), studentGroup, TIMESLOT1, ROOM1);
    Lesson firstTuesdayLesson =
        aLesson(
            2L, repeatedSubject, aTeacher(2L, "Teacher2"), studentGroup, TIMESLOT2, ROOM1);
    Lesson secondTuesdayLesson =
        aLesson(
            3L, repeatedSubject, aTeacher(3L, "Teacher3"), studentGroup, TIMESLOT3, ROOM1);
    Lesson thirdTuesdayLessonWithDifferentSubject =
        aLesson(
            4L, "Subject2", aTeacher(4L, "Teacher4"), studentGroup, TIMESLOT4, ROOM1);
    Lesson lessonInAnotherGroup =
        aLesson(
            5L,
            repeatedSubject,
            aTeacher(5L, "Teacher5"),
            aStudentGroup(2L, "Group2"),
            TIMESLOT1,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::studentGroupSubjectVariety)
        .given(
            mondayLesson,
            firstTuesdayLesson,
            secondTuesdayLesson,
            thirdTuesdayLessonWithDifferentSubject,
            lessonInAnotherGroup)
        .penalizesBy(1);
  }

  @Test
  void studentGroupedInTheSameRoom() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    StudentGroup studentGroup2 = aStudentGroup(2L, "Group1");
    StudentGroup studentGroup3 = aStudentGroup(3L, "Group2");

    Room room = aRoom(1L, "sala1", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            COURSE,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT2,
            room);
    Lesson thirdTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT3,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::courseStudentsGroupedInTheSameRoom)
        .given(firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLesson)
        .penalizesBy(0);
  }

  @Test
  void studentLaboratoryGroupedInTheSameRoom() {

    StudentGroup studentGroup = aStudentGroup(1L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup2 = aStudentGroup(2L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup4 = aStudentGroup(4L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup3 = aStudentGroup(3L, Year.FIRST, "Group2", "2G");

    Room room = aRoom(1L, "sala1", 60L);
    Room room1 = aRoom(2L, "sala2", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT2,
            room1);
    Lesson fourthTuesdayLesson =
        aLesson(
            4L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup4,
            TIMESLOT2,
            room);
    Lesson thirdTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT3,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::labsStudentsGroupedInTheSameRoom)
        .given(firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLesson, fourthTuesdayLesson)
        .penalizesBy(0);
  }

  @Test
  void studentSeminarGroupedInTheSameRoom() {

    StudentGroup studentGroup = aStudentGroup(1L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup2 = aStudentGroup(2L, Year.FIRST, "Group1", "1G");
    StudentGroup studentGroup3 = aStudentGroup(3L, Year.FIRST, "Group2", "2G");

    Room room = aRoom(1L, "sala1", 60L);

    Lesson firstTuesdayLesson =
        aLesson(
            1L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT2,
            room);
    Lesson secondTuesdayLesson =
        aLesson(
            2L,
            "subject1",
            SEMINAR,
            aTeacher(1L, "Teacher1"),
            studentGroup2,
            TIMESLOT2,
            room);
    Lesson thirdTuesdayLesson =
        aLesson(
            3L,
            "subject1",
            LABORATORY,
            aTeacher(1L, "Teacher1"),
            studentGroup3,
            TIMESLOT3,
            room);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::seminarStudentsGroupedInTheSameRoom)
        .given(firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLesson)
        .penalizesBy(0);
  }

  @Test
  void teacherConflict() {

    Teacher conflictingTeacher = aTeacher(1L, "Teacher1");

    Lesson firstLesson =
        aLesson(
            1L,
            "Subject1",
            conflictingTeacher,
            aStudentGroup(1L, "Group1"),
            TIMESLOT1,
            ROOM1);
    Lesson conflictingLesson =
        aLesson(
            2L,
            "Subject2",
            conflictingTeacher,
            aStudentGroup(2L, "Group2"),
            TIMESLOT1,
            ROOM2);
    Lesson nonConflictingLesson =
        aLesson(
            3L,
            "Subject3",
            aTeacher(2L, "Teacher2"),
            aStudentGroup(3L, "Group3"),
            TIMESLOT2,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::teacherConflict)
        .given(firstLesson, conflictingLesson, nonConflictingLesson)
        .penalizesBy(1);
  }

  @Test
  void teacherConflictUniversity() {

    // Todo: test more cases
    Teacher conflictingTeacher = aTeacher(1L, "Teacher1");

    Lesson firstLesson =
        aLesson(
            1L,
            "Subject1",
            SEMINAR,
            conflictingTeacher,
            aStudentGroup(1L, Year.FIRST, "Group1", "1A"),
            TIMESLOT1,
            ROOM1);
    Lesson conflictingLesson =
        aLesson(
            2L,
            "Subject1",
            SEMINAR,
            conflictingTeacher,
            aStudentGroup(2L, Year.FIRST, "Group1", "1A"),
            TIMESLOT1,
            ROOM1);
    Lesson nonConflictingLesson =
        aLesson(
            3L,
            "Subject3",
            COURSE,
            aTeacher(2L, "Teacher2"),
            aStudentGroup(3L, "Group3"),
            TIMESLOT2,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::teacherConflictUniversity)
        .given(firstLesson, conflictingLesson, nonConflictingLesson)
        .penalizesBy(0);
  }

  @Test
  void teacherPreferences() {

    // we will use teacher's preferences as the teacher's availability
    StudentGroup studentGroup = aStudentGroup(1L, "Group1");
    StudentGroup studentGroup2 = aStudentGroup(2L, "Group1");
    String repeatedSubject = "Subject1";

    Lesson mondayLesson =
        aLesson(
            1L,
            repeatedSubject,
            aTeacher(1L, "Teacher1"),
            studentGroup,
            TIMESLOT1,
            ROOM1);
    Lesson firstTuesdayLesson =
        aLesson(
            2L,
            repeatedSubject,
            aTeacher(2L, "Teacher2", TIMESLOT3),
            studentGroup,
            TIMESLOT2,
            ROOM1);
    Lesson secondTuesdayLesson =
        aLesson(
            3L,
            repeatedSubject,
            aTeacher(3L, "Teacher3", TIMESLOT3, TIMESLOT2),
            studentGroup,
            TIMESLOT3,
            ROOM1);
    Lesson thirdTuesdayLesson =
        aLesson(
            4L,
            repeatedSubject,
            aTeacher(3L, "Teacher3", TIMESLOT3, TIMESLOT2),
            studentGroup2,
            TIMESLOT2,
            ROOM1);
    Lesson fourthTuesdayLesson =
        aLesson(
            5L,
            repeatedSubject,
            aTeacher(3L, "Teacher3", TIMESLOT3, TIMESLOT2),
            studentGroup2,
            TIMESLOT3,
            ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::maximizePreferredTimeslotAssignments)
        .given(
            mondayLesson,
            firstTuesdayLesson,
            secondTuesdayLesson,
            thirdTuesdayLesson,
            fourthTuesdayLesson)
        .penalizesBy(1);
  }

  @Test
  void teacherRoomStability() {

    Teacher teacher = aTeacher(1L, "Teacher1");

    Lesson lessonInFirstRoom =
        aLesson(1L, "Subject1", teacher, aStudentGroup(1L, "Group1"), TIMESLOT1, ROOM1);
    Lesson lessonInSameRoom =
        aLesson(2L, "Subject2", teacher, aStudentGroup(2L, "Group2"), TIMESLOT1, ROOM1);
    Lesson lessonInDifferentRoom =
        aLesson(3L, "Subject3", teacher, aStudentGroup(3L, "Group3"), TIMESLOT2, ROOM2);
    Lesson lesson2InDifferentRoom =
        aLesson(4L, "Subject3", teacher, aStudentGroup(4L, "Group3"), TIMESLOT2, ROOM2);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::teacherRoomStability)
        .given(lessonInFirstRoom, lessonInDifferentRoom, lessonInSameRoom, lesson2InDifferentRoom)
        .rewardsWith(0);
  }

  @Test
  void teacherTimeEfficiency() {

    Teacher teacher = aTeacher(1L, "Teacher1");

    Lesson singleLessonOnMonday =
        aLesson(1L, "Subject1", teacher, aStudentGroup(1L, "Group1"), TIMESLOT1, ROOM1);
    Lesson firstTuesdayLesson =
        aLesson(2L, "Subject2", teacher, aStudentGroup(2L, "Group2"), TIMESLOT2, ROOM1);
    Lesson secondTuesdayLesson =
        aLesson(3L, "Subject3", teacher, aStudentGroup(3L, "Group3"), TIMESLOT3, ROOM1);
    Lesson thirdTuesdayLessonWithGap =
        aLesson(4L, "Subject4", teacher, aStudentGroup(4L, "Group4"), TIMESLOT4, ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
        .given(
            singleLessonOnMonday,
            firstTuesdayLesson,
            secondTuesdayLesson,
            thirdTuesdayLessonWithGap)
        .rewardsWith(1);
  }

  @Test
  void tooMuchGap() {

    StudentGroup studentGroup = aStudentGroup(1L, "Group1");

    Lesson firstTuesdayLesson =
        aLesson(
            2L, "subject2", aTeacher(2L, "Teacher2"), studentGroup, TIMESLOT2, ROOM1);
    Lesson secondTuesdayLesson =
        aLesson(
            3L, "subject1", aTeacher(1L, "Teacher1"), studentGroup, TIMESLOT3, ROOM1);
    Lesson thirdTuesdayLesson =
        aLesson(
            4L, "subject2", aTeacher(2L, "Teacher2"), studentGroup, TIMESLOT4, ROOM1);
    //        Lesson fifthTuesdayLesson = aLesson(5, "subject3", aTeacher(3L, "Teacher3",
    // null), studentGroup, TIMESLOT6, ROOM1);
    Lesson sixthTuesdayLesson =
        aLesson(
            6L, "subject4", aTeacher(3L, "Teacher3"), studentGroup, TIMESLOT7, ROOM1);

    constraintVerifier
        .verifyThat(TimetableConstraintProvider::gapsLongerThan4Hours)
        .given(firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLesson, sixthTuesdayLesson)
        .penalizesBy(1);
  }
}
