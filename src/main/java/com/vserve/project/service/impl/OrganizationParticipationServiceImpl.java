package com.vserve.project.service.impl;

import com.vserve.project.dto.organization.OrganizationAttendanceDto;
import com.vserve.project.dto.organization.OrganizationParticipationDto;
import com.vserve.project.dto.organization.OrganizationParticipationResponseDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.OrganizationParticipation;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.enums.ParticipationStatus;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationParticipationRepository;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.service.OrganizationParticipationService;
import com.vserve.project.util.AccountValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrganizationParticipationServiceImpl implements OrganizationParticipationService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final OrganizationParticipationRepository organizationParticipationRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountValidator accountValidator;


    public OrganizationParticipationServiceImpl(ServiceRequestRepository serviceRequestRepository, OrganizationParticipationRepository organizationParticipationRepository, OrganizationRepository organizationRepository, AccountValidator accountValidator) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
        this.organizationRepository = organizationRepository;
        this.accountValidator = accountValidator;
    }

    @Override
    public OrganizationParticipationResponseDto register(
            OrganizationParticipationDto dto) {

        accountValidator.validateActiveOrganization(dto.organizationId());

        ServiceRequest serviceRequest =
                serviceRequestRepository.findById(dto.serviceId())
                        .orElseThrow(() ->
                                new BusinessException("Service request not found"));

        if (!serviceRequest.getStatus().equals(RequestStatus.OPEN))
            throw new BusinessException("Service request is not open");

        Organization organization =
                organizationRepository.findById(dto.organizationId())
                        .orElseThrow(() ->
                                new BusinessException("Organization not found"));
        Integer organizationMember = organizationRepository.findMemberCount(dto.organizationId());
        if (organizationMember == 0)
            throw new BusinessException("Add members in your organization to participate in requests");
        if(dto.memberCount() > organizationMember)
            throw new BusinessException("You have only " +organizationMember+ " members");

        if(serviceRequest.getOrganization() != null &&
                serviceRequest.getOrganization().getId().equals(organization.getId()))
            throw new BusinessException("This request was posted you , cannot register");

        List<OrganizationParticipation> conflicts =
                organizationParticipationRepository.findConflictingRegistrations(
                        organization.getId(),
                        serviceRequest.getServiceDate(),
                        serviceRequest.getServiceStartTime()
                );

        if (organizationParticipationRepository
                .existsByServiceRequest_IdAndOrganization_Id(
                        serviceRequest.getId(),
                        organization.getId()))
            throw new BusinessException("Already registered");

        if (!conflicts.isEmpty()) {
            throw new BusinessException(
                    "Cannot register. Organization already registered " +
                            "for another service on same date and time."
            );
        }

        if (dto.memberCount() == null || dto.memberCount() <= 0)
            throw new BusinessException("Member count must be greater than 0");

        int availableSlots =
                serviceRequest.getMaxVolunteers()
                        - serviceRequest.getRegisteredCount();

        if (dto.memberCount() > availableSlots)
            throw new BusinessException(
                    "Only " + availableSlots + " slots available");


        OrganizationParticipation participation =
                mapToParticipation(serviceRequest, organization, dto.memberCount());

        organizationParticipationRepository.save(participation);

        serviceRequest.setRegisteredCount(
                serviceRequest.getRegisteredCount() + dto.memberCount());

        if (serviceRequest.getRegisteredCount()
                >= serviceRequest.getMaxVolunteers())
            serviceRequest.setStatus(RequestStatus.FULL);

        serviceRequestRepository.save(serviceRequest);

        return mapToResponseDto(participation);
    }

    @Override
    public OrganizationParticipationResponseDto approve(OrganizationParticipationDto dto) {

        OrganizationParticipation participation =
                organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(dto.serviceId(),dto.organizationId());

        if (participation==null)
            throw new BusinessException("Participation doesnt exist");

        participation.setStatus(ParticipationStatus.APPROVED);
        participation.setApprovedAt(LocalDateTime.now());

        organizationParticipationRepository.save(participation);

        return mapToResponseDto(participation);
    }

    @Override
    public OrganizationParticipationResponseDto reject(OrganizationParticipationDto dto) {

        OrganizationParticipation participation =
                organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(dto.serviceId(),dto.organizationId());

        if (participation==null)
            throw new BusinessException("Participation doesnt exist");

        participation.setStatus(ParticipationStatus.REJECTED);

        organizationParticipationRepository.save(participation);

        ServiceRequest serviceRequest = participation.getServiceRequest();

        if(serviceRequest.getMaxVolunteers().equals(serviceRequest.getRegisteredCount()) )
            serviceRequest.setStatus(RequestStatus.OPEN);


        serviceRequest.setRegisteredCount(serviceRequest.getRegisteredCount()-participation.getMemberCount());
        serviceRequestRepository.save(serviceRequest);

        return mapToResponseDto(participation);
    }

    @Override
    public OrganizationParticipationResponseDto withdraw(OrganizationParticipationDto dto) {

        OrganizationParticipation participation =
                organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(dto.serviceId(),dto.organizationId());

        if (participation==null)
            throw new BusinessException("Participation doesnt exist");

        ServiceRequest service = participation.getServiceRequest();
        LocalDateTime serviceDateTime =
                LocalDateTime.of(
                        participation.getServiceRequest().getServiceDate(),
                        participation.getServiceRequest().getServiceStartTime()
                );

        if (serviceDateTime.minusDays(2).isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot withdraw within 48 hours");
        }
        if (participation.getStatus() != ParticipationStatus.APPROVED
                && participation.getStatus() != ParticipationStatus.REQUESTED)
            throw new BusinessException("Cannot withdraw in current status");


        if(service.getRegisteredCount() - participation.getMemberCount() < service.getMinVolunteers())
            throw new BusinessException("Can be withdrawed only when it crosses the min volunteer limit");

        participation.setStatus(ParticipationStatus.WITHDRAWN);
        participation.setWithdrawalTime(LocalDateTime.now());
        participation.setReliabilityScoreImpact(0.0);


        service.setRegisteredCount(service.getRegisteredCount() - participation.getMemberCount());

        organizationParticipationRepository.save(participation);
        serviceRequestRepository.save(service);

        return mapToResponseDto(participation);
    }

    @Override
    public OrganizationParticipationResponseDto markAttendance(
            OrganizationAttendanceDto dto) {

        OrganizationParticipation participation =
                organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(dto.serviceId(),dto.organizationId());

        if (participation==null)
            throw new BusinessException("Participation doesnt exist");

        if (!participation.getServiceRequest().getStatus()
                .equals(RequestStatus.COMPLETED))
            throw new BusinessException("Service not completed");

        if (dto.attended()){
            participation.setStatus(ParticipationStatus.ATTENDED);
            participation.setReliabilityScoreImpact(1.0);
        }
        else {
            participation.setStatus(ParticipationStatus.NO_SHOW);
            participation.setReliabilityScoreImpact(-1.5);
        }

        participation.setAttendanceMarkedAt(LocalDateTime.now());

        organizationParticipationRepository.save(participation);

        return mapToResponseDto(participation);
    }

    @Override
    public OrganizationParticipationResponseDto complete(OrganizationParticipationDto dto) {

        OrganizationParticipation participation =
                organizationParticipationRepository.findByServiceRequest_IdAndOrganization_Id(dto.serviceId(),dto.organizationId());

        if (participation==null)
            throw new BusinessException("Participation doesnt exist");

        if (participation.getStatus() != ParticipationStatus.ATTENDED)
            throw new BusinessException("Cannot complete");

        participation.setStatus(ParticipationStatus.COMPLETED);

        organizationParticipationRepository.save(participation);

        return mapToResponseDto(participation);
    }

    @Override
    public Page<OrganizationParticipationResponseDto>
    getByServiceRequest(
            Long serviceId,
            String status,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order) {

        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<OrganizationParticipation> page;

        if (status != null && !status.isBlank()) {

            ParticipationStatus participationStatus;

            try {
                participationStatus =
                        ParticipationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "Invalid status. Allowed values: " +
                                Arrays.toString(ParticipationStatus.values())
                );
            }

            page = organizationParticipationRepository
                    .findByServiceRequest_IdAndStatus(
                            serviceId,
                            participationStatus,
                            pageable);

        } else {

            page = organizationParticipationRepository
                    .findByServiceRequest_Id(serviceId, pageable);
        }

        return page.map(this::mapToResponseDto);
    }

    private OrganizationParticipationResponseDto mapToResponseDto(OrganizationParticipation organizationParticipation) {
        return new OrganizationParticipationResponseDto(
                organizationParticipation.getId(),
                organizationParticipation.getServiceRequest().getId(),
                organizationParticipation.getServiceRequest().getTitle(),
                organizationParticipation.getOrganization().getId(),
                organizationParticipation.getOrganization().getOrgName(),
                organizationParticipation.getStatus().name(),
                organizationParticipation.getAppliedAt(),
                organizationParticipation.getMemberCount()
        );
    }

    private OrganizationParticipation mapToParticipation(
            ServiceRequest serviceRequest,
            Organization organization, Integer memberCount) {

        OrganizationParticipation participation =
                new OrganizationParticipation();

        participation.setServiceRequest(serviceRequest);
        participation.setOrganization(organization);
        participation.setCreatedAt(LocalDateTime.now());
        participation.setMemberCount(memberCount);
        participation.setAppliedAt(LocalDateTime.now());
        participation.setStatus(ParticipationStatus.REQUESTED);
        participation.setUpdatedAt(LocalDateTime.now());

        return participation;
    }

    @Override
    public Page<OrganizationParticipationResponseDto> getByOrganization(
            Long orgId,
            Integer pageNumber,
            Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<OrganizationParticipation> page =
                organizationParticipationRepository.findByOrganizationId(orgId, pageable);

        return page.map(this::mapToResponseDto);
    }

}
