package de.projekt.priorityplanner.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="rooms")
public class Room {

    @Id
    @Column(name = "roomid")
    int roomNo;

}
