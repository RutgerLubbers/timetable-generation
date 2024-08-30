package com.patrick.timetableappbackend.utils;

import static com.patrick.timetableappbackend.model.ConstraintWeight.HARD;
import static com.patrick.timetableappbackend.model.ConstraintWeight.MEDIUM;
import static com.patrick.timetableappbackend.model.ConstraintWeight.SOFT;
import static com.patrick.timetableappbackend.model.Year.FIRST;
import static java.lang.String.format;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import com.patrick.timetableappbackend.model.Constraint;
import com.patrick.timetableappbackend.model.ConstraintWeight;
import com.patrick.timetableappbackend.model.Lesson;
import com.patrick.timetableappbackend.model.LessonType;
import com.patrick.timetableappbackend.model.Room;
import com.patrick.timetableappbackend.model.SemiGroup;
import com.patrick.timetableappbackend.model.StudentGroup;
import com.patrick.timetableappbackend.model.Teacher;
import com.patrick.timetableappbackend.model.Timeslot;
import com.patrick.timetableappbackend.model.Year;
import com.patrick.timetableappbackend.repository.ConstraintRepo;
import com.patrick.timetableappbackend.repository.LessonRepo;
import com.patrick.timetableappbackend.repository.RoomRepo;
import com.patrick.timetableappbackend.repository.StudentGroupRepo;
import com.patrick.timetableappbackend.repository.TeacherRepo;
import com.patrick.timetableappbackend.repository.TimeslotRepo;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = "timetable.create-test-data",
    name = "dutch",
    havingValue = "true",
    matchIfMissing = false)
public class DutchTestData implements ApplicationRunner {

  /*
   * Dutch secondary school system.
   * When children start secondary school (middelbare school) at age 12, children enter one of three
   * different streams for their secondary education. The different streams represent different
   * educational paths, based on a student’s academic level and interests:
   *
   * VMBO (preparatory secondary vocational education)
   * The VMBO is a four-year vocationally-orientated stream focussed on practical knowledge, which
   * leads to vocational training (MBO). It has two qualification levels and students complete the
   * track at the age of 16.
   *
   * HAVO (senior general secondary education)
   * The HAVO is a five-year middle stream that prepares students to study higher professional
   * education at universities of applied sciences (hogescholen), where they can follow a bachelor’s
   * degree in applied sciences (HBO). Students complete the HAVO around the age of 17.
   *
   * VWO (university preparatory education)
   * The VWO is a six-year education stream with a focus on theoretical knowledge, that prepares
   * students to follow a bachelor’s degree (WO) at a research university. Students study the VWO at
   * schools known as Atheneum and Gymnasium and complete the stream around the age of 18.
   *
   * -----
   * The test data generated here is for a secondary school, mimicking a school in Maastricht.
   * Initial focus to get the VWO stream in place and obtain some likely planning results.
   *
   */

  private static final List<Year> YEARS = List.of(FIRST);

  private static final CourseHours DUTCH = new CourseHours("Dutch language", everyYear(4));
  private static final CourseHours LATIN = new CourseHours("Latin language", everyYear(2));
  private static final CourseHours GREEK = new CourseHours("Greek language", everyYear(2));
  private static final CourseHours ENGELS = new CourseHours("English language", everyYear(2));
  private static final CourseHours FRANS = new CourseHours("French language", everyYear(2));
  private static final CourseHours GERMAN = new CourseHours("German language", everyYear(0, 2));
  private static final CourseHours GEOGRAPHY = new CourseHours("Geography", everyYear(2));
  private static final CourseHours HISTORY = new CourseHours("History", everyYear(2));
  private static final CourseHours BIOLOGY = new CourseHours("Biology", everyYear(0, 2));
  private static final CourseHours PHYSICS_AND_CHEMISTRY =
      new CourseHours("Physics and Chemistry", everyYear(2));
  private static final CourseHours DRAWING = new CourseHours("Drawing", everyYear(2));
  private static final CourseHours MATHEMATICS = new CourseHours("Mathematics", everyYear(4));
  private static final CourseHours MUSIC = new CourseHours("Music", everyYear(1));
  private static final CourseHours DRAMA = new CourseHours("Drama", everyYear(2));
  private static final CourseHours GYM = new CourseHours("Gymnastics/Sports", everyYear(2));
  private static final CourseHours MENTOR = new CourseHours("Mentor Lesson", everyYear(1));
  private static final CourseHours ARTS = new CourseHours("Arts", everyYear(1));

  private List<CourseHours> curriculum =
      List.of(
          DUTCH,
          LATIN,
          GREEK,
          ENGELS,
          FRANS,
          GERMAN,
          GEOGRAPHY,
          HISTORY,
          BIOLOGY,
          PHYSICS_AND_CHEMISTRY,
          DRAWING,
          MATHEMATICS,
          MUSIC,
          DRAMA,
          GYM,
          MENTOR,
          ARTS);

  private final ConstraintRepo constraintRepo;
  private final LessonRepo lessonRepository;
  private final RoomRepo roomRepo;
  private final StudentGroupRepo studentGroupRepo;
  private final TeacherRepo teacherRepo;
  private final TimeslotRepo timeslotRepo;

  private Map<String, List<StudentGroup>> studentGroups = new HashMap<>();
  private Map<String, List<Teacher>> teachers = new HashMap<>();

  public DutchTestData(
      ConstraintRepo constraintRepo,
      LessonRepo lessonRepository,
      RoomRepo roomRepo,
      StudentGroupRepo studentGroupRepo,
      TeacherRepo teacherRepo,
      TimeslotRepo timeslotRepo) {
    this.constraintRepo = constraintRepo;
    this.lessonRepository = lessonRepository;
    this.roomRepo = roomRepo;
    this.studentGroupRepo = studentGroupRepo;
    this.teacherRepo = teacherRepo;
    this.timeslotRepo = timeslotRepo;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    constraintRepo.deleteAll();
    lessonRepository.deleteAll();
    roomRepo.deleteAll();
    studentGroupRepo.deleteAll();
    teacherRepo.deleteAll();
    timeslotRepo.deleteAll();

    // create timeslots
    createWeekSchedule();

    // create  student groups (classes)
    createStudents();

    // create teachers
    createTeachers();

    // create lessons
    createLessons();

    // create rooms
    createRooms();

    // constraints
    createConstraints();
  }

  private void createConstraint(String name, ConstraintWeight weight) {
    Constraint constraintModel =
        Constraint.builder().description(name).weight(weight).build();

    constraintRepo.save(constraintModel);
  }

  private void createConstraints() {
    createConstraint("roomConflict", HARD);
    createConstraint("teacherConflict", HARD);
    createConstraint("studentGroupConflict", HARD);
    createConstraint("capacityRoomConflict", HARD);
    //    createConstraint("courseStudentsGroupedInTheSameRoom", HARD);
    //    createConstraint("seminarStudentsGroupedInTheSameRoom", HARD);
    //    createConstraint("labsStudentsGroupedInTheSameRoom", HARD);
    createConstraint("roomConflictUniversity", HARD);
    createConstraint("teacherConflictUniversity", HARD);
    createConstraint("overlappingTimeslot", HARD);
    //    createConstraint("sportLessonInSportRoom", HARD);
    createConstraint("lessonDurationConflict", HARD);

    // medium
    createConstraint("maximumCoursesForStudents", MEDIUM);
    createConstraint("maximmumCoursesTeached", MEDIUM);
    createConstraint("maximizePreferredTimeslotAssignments", MEDIUM);
    createConstraint("coursesGroupedInTheSameTimeslot", MEDIUM);
    createConstraint("seminarsGroupedInTheSameTimeslot", MEDIUM);

    // soft
    createConstraint("teacherRoomStability", SOFT);
    createConstraint("teacherTimeEfficiency", SOFT);
    createConstraint("studentGroupVariety", SOFT);
    createConstraint("gapsLongerThan4Hours", SOFT);
    createConstraint("labsGroupedInTheSameTimeslot", SOFT);
    createConstraint("coursesInTheSameBuilding", SOFT);
    createConstraint("labAfterSeminar", SOFT);
  }

  private void createDaySchedule(DayOfWeek dayOfWeek) {
    Duration duration = Duration.parse("PT45m");
    Duration micro = Duration.parse("PT5m");
    LocalTime startTime = LocalTime.parse("08:30:00");

    // 1
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
    // 5min
    startTime = startTime.plus(micro);
    // 2
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);

    // break
    startTime = startTime.plus(Duration.parse("PT20m"));

    // 3
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
    // 5min
    startTime = startTime.plus(micro);
    // 4
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);

    // break
    startTime = startTime.plus(Duration.parse("PT30m"));

    // 5
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
    // 5min
    startTime = startTime.plus(micro);
    // 6
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);

    startTime = startTime.plus(Duration.parse("PT15m"));

    // 7
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
    // 5min
    startTime = startTime.plus(micro);
    // 8
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
    // 5min
    startTime = startTime.plus(micro);
    // 9
    createTimeslot(dayOfWeek, startTime, duration);
    startTime = startTime.plus(duration);
  }

  private void createLessons() {
    for (Year year : YEARS) {
      createLessons("VWO", year);
    }
  }

  private void createLessons(String level, Year year) {
    for (CourseHours courseHours : curriculum) {
      createLessons(courseHours, level, year);
    }
  }

  private void createLessons(CourseHours courseHours, String level, Year year) {
    int hours = courseHours.hours().get(year.ordinal());
    if (hours == 0) {
      return;
    }

    String subject = courseHours.subject();
    String levelYear = getLevelYear(level, year);

    int groupNr = 0;
    List<StudentGroup> groups = studentGroups.get(levelYear);
    List<Teacher> subjectTeachers = teachers.get(subject);

    for (StudentGroup studentGroup : groups) {
      Teacher teacher = subjectTeachers.get(groupNr % subjectTeachers.size());

      for (int i = 0; i < hours; i++) {
        Lesson lesson =
            Lesson.builder()
                .duration(45)
                .lessonType(LessonType.COURSE)
                .subject(subject)
                .studentGroup(studentGroup)
                .teacher(teacher)
                .build();
        lessonRepository.save(lesson);
      }

      groupNr++;
    }
  }

  private void createRoom(String name) {
    Room room = Room.builder().building("building 1").name(name).capacity(30L).build();

    roomRepo.save(room);
  }

  private void createRooms() {
    for (int floor = 1; floor <= 3; floor++) {
      for (int room = 10; room <= 20; room = room + 2) {
        createRoom(format("%s-%s", floor, room));
      }
    }
  }

  private void createStudentGroup(String level, Year year, String name, long numberOfStudents) {
    String levelYear = getLevelYear(level, year);
    String className = format("%s %s", levelYear, name);
    StudentGroup studentGroup =
        StudentGroup.builder()
            .name(className)
            .numberOfStudents(numberOfStudents)
            .studentGroup(className)
            .semiGroup(SemiGroup.SEMI_GROUP0)
            .year(year)
            .build();
    studentGroupRepo.save(studentGroup);

    studentGroups.computeIfAbsent(levelYear, k -> new ArrayList<>()).add(studentGroup);
  }

  private static String getLevelYear(String level, Year year) {
    return format("%s%s", level, 1 + year.ordinal());
  }

  private void createStudents() {
    for (Year year : YEARS) {
      createStudentGroup("VWO", year, "a", 30L);
      createStudentGroup("VWO", year, "b", 30L);
      createStudentGroup("VWO", year, "c", 30L);
      createStudentGroup("VWO", year, "d", 30L);
    }
  }

  private void createTeacher(CourseHours courseHours, String name) {
    Teacher teacher = Teacher.builder().name(name).build();
    teacherRepo.save(teacher);

    String subject = courseHours.subject();
    teachers.computeIfAbsent(subject, k -> new ArrayList<>()).add(teacher);
  }

  private void createTeachers() {
    for (CourseHours courseHours : curriculum) {
      for (int i = 0; i < 2; i++) {
        createTeacher(courseHours, format("T(%s) %s", i, courseHours.subject()));
      }
    }
  }

  private void createTimeslot(DayOfWeek dayOfWeek, LocalTime startTime, Duration duration) {
    Timeslot timeslot =
        Timeslot.builder()
            .dayOfWeek(dayOfWeek)
            .startTime(startTime)
            .endTime(startTime.plus(duration))
            .build();
    timeslotRepo.save(timeslot);
  }

  private void createWeekSchedule() {
    createDaySchedule(MONDAY);
    createDaySchedule(TUESDAY);
    createDaySchedule(WEDNESDAY);
    createDaySchedule(THURSDAY);
    createDaySchedule(FRIDAY);
  }

  record CourseHours(String subject, List<Integer> hours) {}

  private static List<Integer> everyYear(int hours) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      result.add(hours);
    }
    return result;
  }

  private static List<Integer> everyYear(int first, int hours) {
    List<Integer> result = new ArrayList<>();
    result.add(first);
    for (int i = 0; i < 5; i++) {
      result.add(hours);
    }
    return result;
  }
}
