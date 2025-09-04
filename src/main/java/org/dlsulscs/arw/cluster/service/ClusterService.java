package org.dlsulscs.arw.cluster.service;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.cluster.repository.ClusterRepository;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClusterService {
    private final ClusterRepository clusterRepository;

    @Autowired
    public ClusterService(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    public List<Cluster> getAllClusters() {
        return clusterRepository.findAll();
    }

    // NOTE: this doesn't return null because of orElseThrow
    // - removing orElseThrow would require to return Optional<Cluster>
    public Cluster getClusterById(Integer id) {
        return clusterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found with id: " + id));
    }

    public Cluster getClusterByName(String name) {
        return clusterRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster not found with name: " + name));
    }

    public Cluster createCluster(Cluster cluster) {
        return clusterRepository.save(cluster);
    }

    public Cluster updateCluster(Integer id, Cluster clusterDetails) {
        Cluster cluster = getClusterById(id);
        cluster.setName(clusterDetails.getName());
        cluster.setDescription(clusterDetails.getDescription());
        return clusterRepository.save(cluster);
    }

    public void deleteCluster(Integer id) {
        Cluster cluster = getClusterById(id);
        clusterRepository.delete(cluster);
    }

}
