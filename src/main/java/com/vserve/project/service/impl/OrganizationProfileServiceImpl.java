package com.vserve.project.service.impl;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.dto.organization.OrganizationProfileDto;
import com.vserve.project.dto.organization.UpdateOrganizationDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.OrganizationAddress;
import com.vserve.project.entity.User;
import com.vserve.project.repository.OrganizationAddressRepository;
import com.vserve.project.repository.OrganizationParticipationRepository;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service

public class OrganizationProfileServiceImpl implements OrganizationProfileService {

    OrganizationRepository organizationRepository;
    OrganizationAddressRepository organizationAddressRepository;
    UserRepository userRepository;
    OrganizationParticipationRepository organizationParticipationRepository;

    public OrganizationProfileServiceImpl(OrganizationRepository organizationRepository, OrganizationAddressRepository organizationAddressRepository, UserRepository userRepository, OrganizationParticipationRepository organizationParticipationRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationAddressRepository = organizationAddressRepository;
        this.userRepository = userRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
    }

    @Override
    public OrganizationProfileDto getOrganizationProfile(Long orgId) {

        Organization org = organizationRepository.findById(orgId).orElse(null);

        if (org == null) {
            throw new ResourceAccessException("Organization not found");
        }

        long memberCount = userRepository.countByOrganizationId(orgId);
        Double score = organizationParticipationRepository.findScore(orgId);

        return new OrganizationProfileDto(
                org.getId(),
                org.getOrgName(),
                org.getEmail(),
                org.getPhone(),
                memberCount,
                org.getDescription(),
                score
        );
    }

    @Override
    public String updateOrganizationProfile(Long orgId, UpdateOrganizationDto dto){

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (dto.organizationName() != null) {
            org.setOrgName(dto.organizationName());
        }

        if (dto.email() != null) {
            org.setEmail(dto.email());
        }

        if (dto.phone() != null) {
            org.setPhone(dto.phone());
        }

        if(dto.description() != null)
            org.setDescription(dto.description());

        organizationRepository.save(org);

        return "Organization profile updated successfully";

    }

    @Override
    public OrganizationAddressRequestDto getOrganizationAddress(Long organizationId) {

        OrganizationAddress address =
                organizationAddressRepository.findByOrganizationId(organizationId);

        if (address == null) {
            throw new RuntimeException("Address not found for this organization");
        }

        return new OrganizationAddressRequestDto(
                organizationId,
                address.getState(),
                address.getCity()
        );
    }
    @Override
    public List<MemberDto> getOrganizationMembers(Long organizationId) {

        List<User> users = userRepository.findByOrganizationId(organizationId);

        if (users.isEmpty()) {
            throw new RuntimeException("No members found for this organization");
        }

        return users.stream()
                .map(user -> new MemberDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone()
                ))
                .toList();
    }
}
