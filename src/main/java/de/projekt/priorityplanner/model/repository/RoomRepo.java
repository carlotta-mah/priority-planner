package de.projekt.priorityplanner.model.repository;

import de.projekt.priorityplanner.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepo extends JpaRepository<Room, Integer> {
    Room findByRoomNo(int roomid);
}
