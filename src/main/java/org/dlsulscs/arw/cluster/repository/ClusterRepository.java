package org.dlsulscs.arw.cluster.repository;

import java.util.Optional;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Integer> {
    @Query("SELECT c FROM Cluster c WHERE lower(c.name) = lower(:name)")
    Optional<Cluster> findByName(@Param("name") String name);
}
