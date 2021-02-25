package de.projekt.priorityplanner.model.repository;

import de.projekt.priorityplanner.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUserNo(int userid);
    List<User> findByRoomNo(int roomNo);
}
