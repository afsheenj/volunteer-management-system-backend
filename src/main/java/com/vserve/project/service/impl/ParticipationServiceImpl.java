package com.vserve.project.service.impl;

import com.vserve.project.dto.ParticipationResponseDto;
import com.vserve.project.entity.OrganizationParticipation;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.UserParticipation;
import com.vserve.project.enums.ParticipationStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationParticipationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserParticipationRepository;
import com.vserve.project.service.ParticipationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class ParticipationServiceImpl implements ParticipationService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final OrganizationParticipationRepository organizationParticipationRepository;
    private final UserParticipationRepository userParticipationRepository;

    public ParticipationServiceImpl(ServiceRequestRepository serviceRequestRepository, OrganizationParticipationRepository organizationParticipationRepository, UserParticipationRepository userParticipationRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
        this.userParticipationRepository = userParticipationRepository;
    }

//    @Override
//    public ParticipationResponseDto approve(Long serviceId, Long participantId, String participantType) {
//
//        if (participantType.equalsIgnoreCase("USER")) {
//
//            UserParticipation p =
//                    userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId);
//
//            if (p == null)
//                throw new BusinessException("User participation not found");
//
//            p.setStatus(ParticipationStatus.APPROVED);
//            userParticipationRepository.save(p);
//
//            return mapUserToDto(p);
//        }
//
//        else if (participantType.equalsIgnoreCase("ORGANIZATION")) {
//
//            OrganizationParticipation p =
//                    organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId);
//
//            if (p == null)
//                throw new BusinessException("Organization participation not found");
//
//            p.setStatus(ParticipationStatus.APPROVED);
//            organizationParticipationRepository.save(p);
//
//            return mapOrgToDto(p);
//        }
//
//        throw new BusinessException("Invalid participant type");
//    }
@Override
public ParticipationResponseDto approve(Long serviceId, Long participantId, String participantType) {
    ServiceRequest serviceRequest = serviceRequestRepository
            .findById(serviceId)
            .orElseThrow(() -> new BusinessException("Service does not exist"));

    if (participantType.equalsIgnoreCase("USER")) {

        UserParticipation p = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId);
        if (p == null) throw new BusinessException("User participation not found");

        p.setStatus(ParticipationStatus.APPROVED);
        userParticipationRepository.save(p);

    } else if (participantType.equalsIgnoreCase("ORGANIZATION")) {

        OrganizationParticipation p = organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId);
        if (p == null) throw new BusinessException("Organization participation not found");

        p.setStatus(ParticipationStatus.APPROVED);
        organizationParticipationRepository.save(p);

    } else {
        throw new BusinessException("Invalid participant type");
    }

    // Update approved count
    long approvedUsers = userParticipationRepository.countByServiceRequest_IdAndStatus(serviceId, ParticipationStatus.APPROVED);
    long approvedOrgs = organizationParticipationRepository.countByServiceRequest_IdAndStatus(serviceId, ParticipationStatus.APPROVED);
    serviceRequest.setApprovedCount((int) (approvedUsers + approvedOrgs));
    serviceRequestRepository.save(serviceRequest);

    // Return the updated participation DTO
    if (participantType.equalsIgnoreCase("USER")) {
        return mapUserToDto(userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId));
    } else {
        return mapOrgToDto(organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId));
    }
}
//    @Override
//    public ParticipationResponseDto reject(Long serviceId, Long participantId, String participantType) {
//
//        if (participantType.equalsIgnoreCase("USER")) {
//
//            UserParticipation p =
//                    userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId);
//
//            if (p == null)
//                throw new BusinessException("User participation not found");
//
//            p.setStatus(ParticipationStatus.REJECTED);
//            userParticipationRepository.save(p);
//
//            return mapUserToDto(p);
//        }
//
//        else if (participantType.equalsIgnoreCase("ORGANIZATION")) {
//
//            OrganizationParticipation p =
//                    organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId);
//
//            if (p == null)
//                throw new BusinessException("Organization participation not found");
//
//            p.setStatus(ParticipationStatus.REJECTED);
//            organizationParticipationRepository.save(p);
//
//            return mapOrgToDto(p);
//        }
//
//        throw new BusinessException("Invalid participant type");
//    }

    @Override
    public ParticipationResponseDto reject(Long serviceId, Long participantId, String participantType) {
        ServiceRequest serviceRequest = serviceRequestRepository
                .findById(serviceId)
                .orElseThrow(() -> new BusinessException("Service does not exist"));

        if (participantType.equalsIgnoreCase("USER")) {
            UserParticipation p = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId);
            if (p == null) throw new BusinessException("User participation not found");

            p.setStatus(ParticipationStatus.REJECTED);
            userParticipationRepository.save(p);

        } else if (participantType.equalsIgnoreCase("ORGANIZATION")) {
            OrganizationParticipation p = organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId);
            if (p == null) throw new BusinessException("Organization participation not found");

            p.setStatus(ParticipationStatus.REJECTED);
            organizationParticipationRepository.save(p);

        } else {
            throw new BusinessException("Invalid participant type");
        }

        // Update approved count after rejection
        long approvedUsers = userParticipationRepository.countByServiceRequest_IdAndStatus(serviceId, ParticipationStatus.APPROVED);
        long approvedOrgs = organizationParticipationRepository.countByServiceRequest_IdAndStatus(serviceId, ParticipationStatus.APPROVED);
        serviceRequest.setApprovedCount((int) (approvedUsers + approvedOrgs));

        // Optional: decrease registeredCount if you want rejection to free a slot
        long registeredUsers = userParticipationRepository.countByServiceRequest_Id(serviceId);
        long registeredOrgs = organizationParticipationRepository.countByServiceRequest_Id(serviceId);
        serviceRequest.setRegisteredCount((int) (registeredUsers + registeredOrgs));

        // Set status OPEN if previously FULL
        if (serviceRequest.getStatus().name().equalsIgnoreCase("FULL")) {
            serviceRequest.setStatus(serviceRequest.getRegisteredCount() < serviceRequest.getMaxVolunteers() ? serviceRequest.getStatus().OPEN : serviceRequest.getStatus());
        }

        serviceRequestRepository.save(serviceRequest);

        // Return the updated participation DTO
        if (participantType.equalsIgnoreCase("USER")) {
            return mapUserToDto(userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId, participantId));
        } else {
            return mapOrgToDto(organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(serviceId, participantId));
        }
    }

    private ParticipationResponseDto mapUserToDto(UserParticipation p) {

        return new ParticipationResponseDto(
                p.getId(),
                p.getServiceRequest().getId(),
                p.getServiceRequest().getTitle(),
                p.getUser().getId(),
                p.getUser().getUsername(),
                "USER",
                p.getStatus().name(),
                p.getAppliedAt(),
                null
        );
    }

    private ParticipationResponseDto mapOrgToDto(OrganizationParticipation p) {

        return new ParticipationResponseDto(
                p.getId(),
                p.getServiceRequest().getId(),
                p.getServiceRequest().getTitle(),
                p.getOrganization().getId(),
                p.getOrganization().getOrgName(),
                "ORGANIZATION",
                p.getStatus().name(),
                p.getAppliedAt(),
                p.getMemberCount()
        );
    }

    @Override
    public Page<ParticipationResponseDto> getParticipants(
            Long serviceId,
            String status,
            String participantType,
            Integer pageNumber,
            Integer pageSize) {

        ServiceRequest serviceRequest = serviceRequestRepository
                .findById(serviceId)
                .orElseThrow(() -> new BusinessException("Service does not exist"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<UserParticipation> userPage =
                userParticipationRepository.findByServiceRequest_Id(serviceId, pageable);

        Page<OrganizationParticipation> orgPage =
                organizationParticipationRepository.findByServiceRequest_Id(serviceId, pageable);

        List<ParticipationResponseDto> participants = new ArrayList<>();

        // Map User Participants
        for (UserParticipation userParticipation : userPage.getContent()) {

            participants.add(
                    new ParticipationResponseDto(
                            userParticipation.getId(),
                            serviceRequest.getId(),
                            serviceRequest.getTitle(),
                            userParticipation.getUser().getId(),
                            userParticipation.getUser().getUsername(),
                            "USER",
                            userParticipation.getStatus().name(),
                            userParticipation.getAppliedAt(),
                            null
                    )
            );
        }

        // Map Organization Participants
        for (OrganizationParticipation orgParticipation : orgPage.getContent()) {

            participants.add(
                    new ParticipationResponseDto(
                            orgParticipation.getId(),
                            serviceRequest.getId(),
                            serviceRequest.getTitle(),
                            orgParticipation.getOrganization().getId(),
                            orgParticipation.getOrganization().getOrgName(),
                            "ORGANIZATION",
                            orgParticipation.getStatus().name(),
                            orgParticipation.getAppliedAt(),
                            orgParticipation.getMemberCount()
                    )
            );
        }

        // Sort by appliedAt
        participants.sort(Comparator.comparing(ParticipationResponseDto::appliedAt));

        return new PageImpl<>(
                participants,
                pageable,
                participants.size()
        );
    }
}
