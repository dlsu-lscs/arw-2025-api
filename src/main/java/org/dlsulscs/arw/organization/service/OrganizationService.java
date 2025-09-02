package org.dlsulscs.arw.organization.service;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.cluster.service.ClusterService;
import org.dlsulscs.arw.college.model.College;
import org.dlsulscs.arw.college.service.CollegeService;
import org.dlsulscs.arw.organization.dto.OrganizationUpdateRequestDto;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Organization> getAllOrganizations() {
        return this.organizationRepository.findAll();
    }

    public Organization getOrganizationById(Integer id) {
        return this.organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));
    }

    public Organization getOrganizationByName(String name) {
        return this.organizationRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Organization not found with name: " + name));
    }

    public List<Organization> getOrganizationByCluster(String clusterName) {
        return organizationRepository.findOrganizationByClusterName(clusterName);
    }

    public Organization createOrganization(OrganizationUpdateRequestDto orgDto) {
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

    public Organization patchOrganization(Integer id, OrganizationUpdateRequestDto partialUpdate) {
        Organization existingOrg = getOrganizationById(id);

        if (partialUpdate.name() != null) {
            existingOrg.setName(partialUpdate.name());
        }
        if (partialUpdate.shortName() != null) {
            existingOrg.setShortName(partialUpdate.shortName());
        }
        if (partialUpdate.shortName() != null) {
            existingOrg.setAbout(partialUpdate.shortName());
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
