package org.dlsulscs.arw.organization.service;

import org.dlsulscs.arw.cluster.model.Cluster;
import org.dlsulscs.arw.cluster.service.ClusterService;
import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.organization.dto.OrganizationCreateUpdateRequestDto;
import org.dlsulscs.arw.organization.dto.OrganizationFeeGformsUpdateRequestDto;
import org.dlsulscs.arw.organization.dto.OrganizationBulkUpdateDto;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ClusterService clusterService;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, ClusterService clusterService) {
        this.organizationRepository = organizationRepository;
        this.clusterService = clusterService;
    }

    /**
     * Retrieves a paginated list of organizations, optionally filtered by cluster
     * name,
     * and ordered randomly using a seed for consistent pagination.
     *
     * <p>
     * This method is designed to support "See More" or infinite scroll pagination
     * on the frontend.
     * For this to work correctly, the frontend must:
     * <ol>
     * <li>Generate a single unique {@code seed} value when the user first visits
     * the page.</li>
     * <li>Store this {@code seed} in its state.</li>
     * <li>Send the <strong>same</strong> {@code seed} with every subsequent request
     * for the next page.</li>
     * <li>Increment the {@code page} parameter for each "See More" click (e.g.,
     * page=0, page=1, page=2...).</li>
     * </ol>
     * If no seed is provided, a new random one is generated for each call. This
     * will break page consistency,
     * resulting in a different random order on every request.
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
     */
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

    public Organization getOrganizationByShortName(String shortName) {
        return this.organizationRepository.findByShortName(shortName)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with short name: " + shortName));
    }

    public Organization createOrganization(OrganizationCreateUpdateRequestDto orgDto) {
        Organization newOrg = createOrganizationFromDto(orgDto);
        return organizationRepository.save(newOrg);
    }

    public List<Organization> createOrganizations(List<OrganizationCreateUpdateRequestDto> orgDtos) {
        List<Organization> newOrgs = orgDtos.stream().map(this::createOrganizationFromDto).collect(Collectors.toList());
        return organizationRepository.saveAll(newOrgs);
    }

    private Organization createOrganizationFromDto(OrganizationCreateUpdateRequestDto orgDto) {
        Cluster cluster = clusterService.getClusterByName(orgDto.clusterName());

        Organization newOrg = new Organization();
        newOrg.setName(orgDto.name());
        newOrg.setShortName(orgDto.shortName());
        newOrg.setAbout(orgDto.about());
        newOrg.setFee(orgDto.fee());
        newOrg.setGformsUrl(orgDto.gformsUrl());
        newOrg.setFacebookUrl(orgDto.facebookUrl());
        newOrg.setMission(orgDto.mission());
        newOrg.setVision(orgDto.vision());
        newOrg.setTagline(orgDto.tagline());
        newOrg.setCluster(cluster);

        return newOrg;
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

        return organizationRepository.save(existingOrg);
    }

    public void deleteOrganization(Integer id) {
        Organization org = getOrganizationById(id);
        organizationRepository.delete(org);
    }

    public Organization patchOrganizationByShortName(String shortName, OrganizationFeeGformsUpdateRequestDto partialUpdate) {
        Organization existingOrg = getOrganizationByShortName(shortName);

        if (partialUpdate.fee() != null) {
            existingOrg.setFee(partialUpdate.fee());
        }
        if (partialUpdate.gformsUrl() != null) {
            existingOrg.setGformsUrl(partialUpdate.gformsUrl());
        }

        return organizationRepository.save(existingOrg);
    }

    @Transactional
    public List<Organization> bulkUpdateOrganizations(List<OrganizationBulkUpdateDto> updateDtos) {
        List<Organization> updatedOrgs = updateDtos.stream().map(dto -> {
            Organization org = getOrganizationByShortName(dto.shortName());
            if (dto.fee() != null) {
                org.setFee(dto.fee());
            }
            if (dto.gformsUrl() != null) {
                org.setGformsUrl(dto.gformsUrl());
            }
            return org;
        }).collect(Collectors.toList());

        return organizationRepository.saveAll(updatedOrgs);
    }

}
