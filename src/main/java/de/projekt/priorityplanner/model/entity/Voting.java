package de.projekt.priorityplanner.model.entity;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@IdClass(VotingID.class)
@Table(name="voting")
public class Voting {
    @Id
    @Column(name = "user")
    int userNo;
    @Id
    @Column(name = "feature")
    int featureNo;

    @Column(name = "boost")
    int boost;
    @Column(name = "rip")
    int rip;
    @Column(name = "time")
    int time;
}
