package de.projekt.priorityplanner.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "userid")
    int userNo;
    @Column(name = "username")
    String userName;
    @Column(name = "room")
    int featureRoom;
}
