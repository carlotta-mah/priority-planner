package de.projekt.priorityplanner.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VotingID implements Serializable {
    private int userNo;
    private int featureNo;
}
