package com.ilionx.timetable.repository;

import com.ilionx.timetable.model.StudentGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupRepo extends JpaRepository<StudentGroup, Long> {

  List<StudentGroup> findAllByOrderByIdAsc();
}
