package org.dlsulscs.arw.publication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.dlsulscs.arw.organization.model.Organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "org_pubs")
public class Publications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String main_pub_url;
    private String fee_pub_url;
    private String logo_url;
    private String sub_logo_url;
    private String org_vid_url;

    @OneToOne
    @JoinColumn(name = "org_id", unique = true)
    @JsonBackReference
    private Organization organization;
}
