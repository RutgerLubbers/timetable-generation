package com.ilionx.timetable.repository;

import com.ilionx.timetable.model.Timeslot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepo
    extends JpaRepository<Timeslot, Long> {

  List<Timeslot> findAllByOrderByIdAsc();
}
