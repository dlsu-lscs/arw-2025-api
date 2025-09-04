package org.dlsulscs.arw.organization.repository;

import org.dlsulscs.arw.organization.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    // Assume that names are UNIQUE
    @Query("SELECT o FROM Organization o WHERE lower(o.name) = lower(:name)")
    Optional<Organization> findByName(@Param("name") String name);

    @Query("SELECT o FROM Organization o WHERE lower(o.cluster.name) = lower(:clusterName)")
    Page<Organization> findOrganizationByClusterName(@Param("clusterName") String clusterName, Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE " +
            "lower(o.name) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(o.shortName) LIKE lower(concat('%', :query, '%')) OR " +
            "lower(o.cluster.name) LIKE lower(concat('%', :query, '%'))")
    Page<Organization> search(@Param("query") String query, Pageable pageable);

    Page<Organization> findAll(Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE lower(o.cluster.name) = lower(:clusterName)")
    Page<Organization> findAllByClusterName(@Param("clusterName") String clusterName, Pageable pageable);

}
