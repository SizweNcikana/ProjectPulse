package com.swiftedge.projectservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(nullable = false, unique = true)
    private Long projectId;

    @Column(nullable = false, unique = true)
    private String projectName;

    private LocalDate startDate;
    private Integer duration; //Duration in months

    @Column(length = 500)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    private ProjectStatus status;
}
