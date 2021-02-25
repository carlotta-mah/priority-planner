package de.projekt.priorityplanner.model.repository;

import de.projekt.priorityplanner.model.entity.Voting;
import de.projekt.priorityplanner.model.entity.VotingID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VotingRepo extends JpaRepository<Voting, VotingID> {
    @Query(value = "SELECT m FROM Voting m WHERE m.userNo = ?1 AND m.featureNo = ?2")
    Voting findById(int userNo, int featureNo);

//    @Query(value = "SELECT m FROM Voting m WHERE m.userNo = ?1 AND m.featureNo = ?2")
//    Voting findByRoom()
}
