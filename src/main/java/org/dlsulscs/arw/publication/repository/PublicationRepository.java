package org.dlsulscs.arw.publication.repository;

import java.util.Optional;

import org.dlsulscs.arw.publication.model.Publications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publications, Integer> {
    @Query("SELECT p FROM Publications p WHERE lower(p.organization.name) = lower(:orgName)")
    Optional<Publications> findPubsByOrgName(@Param("orgName") String orgName);
    // or can be Publications findByOrganization_Name(String name);
}
