package org.dlsulscs.arw.organization.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.publication.model.Publications;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "orgs")
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String shortName;
    private String about;
    private String fee;
    private String gformsUrl;
    private String facebookUrl;
    private String mission;
    private String vision;
    private String tagline;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;

    @OneToOne(mappedBy = "organization")
    @JsonManagedReference
    private Publications publications;
}