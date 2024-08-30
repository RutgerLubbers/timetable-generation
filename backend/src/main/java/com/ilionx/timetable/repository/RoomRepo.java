package com.ilionx.timetable.repository;

import com.ilionx.timetable.model.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Room, Long> {

  List<Room> findAllByOrderByIdAsc();
}
