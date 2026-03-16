package com.cheatsheet.quiz.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTopicStats {

    @Id
    private Long id;
    private String topic;
    private int correct;
    private int incorrect;
    private double mastery;
    private LocalDateTime lastSeen;

}
