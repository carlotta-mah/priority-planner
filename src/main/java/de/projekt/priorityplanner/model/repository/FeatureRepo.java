package de.projekt.priorityplanner.model.repository;

import de.projekt.priorityplanner.model.entity.Feature;
import de.projekt.priorityplanner.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatureRepo extends JpaRepository<Feature, Integer> {
    Feature findByFeatureNo(int featureid);
    List<Feature> findByRoomNo(int roomNo);
}
