package de.projekt.priorityplanner.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="features")
public class Feature {
    @Id
    @Column(name = "featureid")
    int featureNo;
    @Column(name = "name")
    String featureName;
    @Column(name = "description")
    String featureDescription;
    @Column(name = "roomid")
    int roomNo;
}
