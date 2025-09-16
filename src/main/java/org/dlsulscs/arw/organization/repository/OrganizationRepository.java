package org.dlsulscs.arw.organization.repository;

import org.dlsulscs.arw.organization.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    // Assume that names are UNIQUE
    @Query("SELECT o FROM Organization o WHERE lower(o.name) = lower(:name)")
    Optional<Organization> findByName(@Param("name") String name);

    @Query("SELECT o FROM Organization o WHERE lower(o.shortName) = lower(:shortName)")
    Optional<Organization> findByShortName(@Param("shortName") String shortName);

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

    @Query(value = "SELECT * FROM orgs ORDER BY md5(id::text || :seed)",
        countQuery = "SELECT count(*) FROM orgs",
        nativeQuery = true)
    Page<Organization> findAllWithSeed(Pageable pageable, @Param("seed") String seed);

    @Query(value = "SELECT o.* FROM orgs o JOIN clusters c ON o.cluster_id = c.id WHERE lower(c.name) = lower(:clusterName) ORDER BY md5(o.id::text || :seed)",
        countQuery = "SELECT count(o.id) FROM orgs o JOIN clusters c ON o.cluster_id = c.id WHERE lower(c.name) = lower(:clusterName)",
        nativeQuery = true)
    Page<Organization> findAllByClusterNameWithSeed(@Param("clusterName") String clusterName, Pageable pageable, @Param("seed") String seed);

    @Query("SELECT o FROM Organization o WHERE o.shortName IN :shortNames")
    List<Organization> findByShortNameIn(@Param("shortNames") List<String> shortNames);

    @Query(value = "SELECT * FROM orgs ORDER BY CASE WHEN lower(short_name) IN (:prioritized) THEN 0 ELSE 1 END, md5(id::text || :seed)",
        countQuery = "SELECT count(*) FROM orgs",
        nativeQuery = true)
    Page<Organization> findAllWithSeedAndPrioritized(Pageable pageable, @Param("seed") String seed, @Param("prioritized") List<String> prioritized);

    @Query(value = "SELECT o.* FROM orgs o JOIN clusters c ON o.cluster_id = c.id WHERE lower(c.name) = lower(:clusterName) ORDER BY CASE WHEN lower(o.short_name) IN (:prioritized) THEN 0 ELSE 1 END, md5(o.id::text || :seed)",
        countQuery = "SELECT count(o.id) FROM orgs o JOIN clusters c ON o.cluster_id = c.id WHERE lower(c.name) = lower(:clusterName)",
        nativeQuery = true)
    Page<Organization> findAllByClusterNameWithSeedAndPrioritized(@Param("clusterName") String clusterName, Pageable pageable, @Param("seed") String seed, @Param("prioritized") List<String> prioritized);

}
