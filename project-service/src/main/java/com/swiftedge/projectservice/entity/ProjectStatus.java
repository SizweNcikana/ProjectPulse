package com.swiftedge.projectservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "project_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_status_seq")
    @SequenceGenerator(name = "project_status_seq", sequenceName = "project_status_sequence", allocationSize = 1)

    private Long id;
    private String status;

    @OneToMany(mappedBy = "status")
    private List<ProjectEntity> projects;
}
