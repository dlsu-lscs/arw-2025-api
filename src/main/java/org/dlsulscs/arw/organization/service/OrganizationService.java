package org.dlsulscs.arw.organization.service;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.cluster.service.ClusterService;
import org.dlsulscs.arw.college.model.College;
import org.dlsulscs.arw.college.service.CollegeService;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.organization.dto.OrganizationCreateUpdateRequestDto;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ClusterService clusterService;
    private final CollegeService collegeService;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, ClusterService clusterService,
            CollegeService collegeService) {
        this.organizationRepository = organizationRepository;
        this.clusterService = clusterService;
        this.collegeService = collegeService;
    }

    /**
     * Retrieves a paginated list of organizations, optionally filtered by cluster
     * name,
     * and ordered randomly using a seed for consistent pagination.
     *
     * <p>
     * For consistent pagination, the same seed value must be provided for each
     * subsequent request.
     * If no seed is provided, a new random seed is generated, which will result in
     * a different
     * random order on every request and break page consistency.
     * </p>
     *
     * @param clusterName the name of the cluster to filter organizations by
     *                    (optional)
     * @param page        the page number to retrieve (zero-based)
     * @param pageSize    the number of organizations per page
     * @param seed        the seed for random ordering; must be the same for each
     *                    paginated request to maintain order consistency
     * @return a page of organizations, filtered and randomly ordered by the
     *         provided seed
     **/
    public Page<Organization> getOrganizations(String clusterName, Integer page, Integer pageSize, String seed) {
        String effectiveSeed = (seed != null && !seed.isEmpty()) ? seed : UUID.randomUUID().toString();
        Pageable pageable = PageRequest.of(page, pageSize);
        if (clusterName != null && !clusterName.isEmpty()) {
            return organizationRepository.findAllByClusterNameWithSeed(clusterName, pageable, effectiveSeed);
        }
        return this.organizationRepository.findAllWithSeed(pageable, effectiveSeed);
    }

    public Page<Organization> searchOrganizations(String query, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return organizationRepository.search(query, pageable);
    }

    public Organization getOrganizationById(Integer id) {
        return this.organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    public Organization getOrganizationByName(String name) {
        return this.organizationRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with name: " + name));
    }

    public Organization createOrganization(OrganizationCreateUpdateRequestDto orgDto) {
        Cluster cluster = clusterService.getClusterByName(orgDto.clusterName());
        College college = collegeService.getCollegeByName(orgDto.collegeName());

        Organization newOrg = new Organization();
        newOrg.setName(orgDto.name());
        newOrg.setShortName(orgDto.shortName());
        newOrg.setAbout(orgDto.about());
        newOrg.setFee(orgDto.fee());
        newOrg.setBundleFee(orgDto.bundleFee());
        newOrg.setGformsUrl(orgDto.gformsUrl());
        newOrg.setFacebookUrl(orgDto.facebookUrl());
        newOrg.setMission(orgDto.mission());
        newOrg.setVision(orgDto.vision());
        newOrg.setTagline(orgDto.tagline());
        newOrg.setCluster(cluster);
        newOrg.setCollege(college);

        return organizationRepository.save(newOrg);
    }

    public Organization patchOrganization(Integer id, OrganizationCreateUpdateRequestDto partialUpdate) {
        Organization existingOrg = getOrganizationById(id);

        if (partialUpdate.name() != null) {
            existingOrg.setName(partialUpdate.name());
        }
        if (partialUpdate.shortName() != null) {
            existingOrg.setShortName(partialUpdate.shortName());
        }
        if (partialUpdate.about() != null) {
            existingOrg.setAbout(partialUpdate.about());
        }
        if (partialUpdate.fee() != null) {
            existingOrg.setFee(partialUpdate.fee());
        }
        if (partialUpdate.bundleFee() != null) {
            existingOrg.setBundleFee(partialUpdate.bundleFee());
        }
        if (partialUpdate.gformsUrl() != null) {
            existingOrg.setGformsUrl(partialUpdate.gformsUrl());
        }
        if (partialUpdate.facebookUrl() != null) {
            existingOrg.setFacebookUrl(partialUpdate.facebookUrl());
        }
        if (partialUpdate.mission() != null) {
            existingOrg.setMission(partialUpdate.mission());
        }
        if (partialUpdate.vision() != null) {
            existingOrg.setVision(partialUpdate.vision());
        }
        if (partialUpdate.tagline() != null) {
            existingOrg.setTagline(partialUpdate.tagline());
        }
        if (partialUpdate.clusterName() != null) {
            Cluster cluster = clusterService.getClusterByName(partialUpdate.clusterName());
            existingOrg.setCluster(cluster);
        }
        if (partialUpdate.collegeName() != null) {
            College college = collegeService.getCollegeByName(partialUpdate.collegeName());
            existingOrg.setCollege(college);
        }

        return organizationRepository.save(existingOrg);
    }

    public void deleteOrganization(Integer id) {
        Organization org = getOrganizationById(id);
        organizationRepository.delete(org);
    }

}
