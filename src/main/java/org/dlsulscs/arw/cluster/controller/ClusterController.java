package org.dlsulscs.arw.cluster.controller;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.cluster.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clusters")
public class ClusterController {

    private final ClusterService clusterService;

    @Autowired
    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GetMapping
    public List<Cluster> getAllClusters() {
        return clusterService.getAllClusters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cluster> getClusterById(@PathVariable Integer id) {
        Cluster cluster = clusterService.getClusterById(id);
        return ResponseEntity.ok(cluster);
    }

    @PostMapping
    public ResponseEntity<Cluster> createCluster(@RequestBody Cluster cluster) {
        Cluster createdCluster = clusterService.createCluster(cluster);
        return new ResponseEntity<>(createdCluster, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cluster> updateCluster(@PathVariable Integer id, @RequestBody Cluster clusterDetails) {
        Cluster updatedCluster = clusterService.updateCluster(id, clusterDetails);
        return ResponseEntity.ok(updatedCluster);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCluster(@PathVariable Integer id) {
        clusterService.deleteCluster(id);
        return ResponseEntity.noContent().build();
    }
}
