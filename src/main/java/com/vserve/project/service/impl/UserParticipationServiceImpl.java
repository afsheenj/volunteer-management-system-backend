package com.vserve.project.service.impl;

import com.vserve.project.dto.user.UserAttendanceDto;
import com.vserve.project.dto.user.UserParticipationDto;
import com.vserve.project.dto.user.UserParticipationResponseDto;
import com.vserve.project.entity.UserParticipation;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.User;
import com.vserve.project.enums.ParticipationStatus;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.UserParticipationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.UserParticipationService;
import com.vserve.project.util.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class UserParticipationServiceImpl implements UserParticipationService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserParticipationRepository userParticipationRepository;
    private final UserRepository userRepository;
    private final AccountValidator accountValidator;

    public UserParticipationServiceImpl(ServiceRequestRepository serviceRequestRepository,
                                        UserParticipationRepository userParticipationRepository,
                                        UserRepository userRepository, AccountValidator accountValidator) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userParticipationRepository = userParticipationRepository;
        this.userRepository = userRepository;
        this.accountValidator = accountValidator;
    }


    @Override
    public UserParticipationResponseDto register(UserParticipationDto dto) {

        accountValidator.validateActiveUser(dto.userId());
        ServiceRequest service = serviceRequestRepository
                .findById(dto.serviceId())
                .orElseThrow(() -> new BusinessException("Service Request not found"));
        System.out.println("ServiceRequest: " + service);
        System.out.println("RequestedBy: " + service.getRequestedBy());
        if (!service.getStatus().equals(RequestStatus.OPEN))
            throw new BusinessException("Service not open");

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new BusinessException("User not found"));


        if (service.getRequestedBy() != null &&
                service.getRequestedBy().getId().equals(user.getId()))
                    throw new BusinessException("Cannot be registered");


        List<UserParticipation> conflicts =
                userParticipationRepository.findUserConflicts(
                        user.getId(),
                        service.getServiceDate(),
                        service.getServiceStartTime()
                );

        if (userParticipationRepository.existsByServiceRequest_IdAndUser_Id(
                service.getId(), user.getId()))
            throw new BusinessException("Already registered");

        if (!conflicts.isEmpty()) {
            throw new BusinessException(
                    "Cannot register. User already registered " +
                            "for another service on same date and time."
            );
        }

        if (service.getRegisteredCount() >= service.getMaxVolunteers())
            throw new BusinessException("Service full");

        UserParticipation userParticipation = mapToParticipation(service, user);

        userParticipationRepository.save(userParticipation);

        service.setRegisteredCount(service.getRegisteredCount() + 1);

        if (service.getRegisteredCount().equals(service.getMaxVolunteers()))
            service.setStatus(RequestStatus.FULL);

        serviceRequestRepository.save(service);

        return mapToResponseDto(userParticipation);
    }

    private UserParticipation mapToParticipation(ServiceRequest service, User user) {
        UserParticipation userParticipation = new UserParticipation();

        userParticipation.setServiceRequest(service);
        userParticipation.setUser(user);
        userParticipation.setStatus(ParticipationStatus.REQUESTED);
        userParticipation.setAppliedAt(LocalDateTime.now());
        userParticipation.setCreatedAt(LocalDateTime.now());
        userParticipation.setUpdatedAt(LocalDateTime.now());

        return userParticipation;
    }

    @Override
    public UserParticipationResponseDto approve(Long serviceId, Long userId) {

        UserParticipation userParticipation = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId,userId);

        if (userParticipation==null)
            throw new BusinessException("Participation doesnt exist");

        userParticipation.setStatus(ParticipationStatus.APPROVED);
        userParticipation.setApprovedAt(LocalDateTime.now());

        userParticipationRepository.save(userParticipation);


        serviceRequestRepository.updateApprovedCountNo(serviceId);

        return mapToResponseDto(userParticipation);
    }


    @Override
    public UserParticipationResponseDto reject(Long serviceId, Long userId) {

        UserParticipation userParticipation = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId,userId);

        if (userParticipation==null)
            throw new BusinessException("Participation doesnt exist");

        userParticipation.setStatus(ParticipationStatus.REJECTED);
        userParticipationRepository.save(userParticipation);

        ServiceRequest serviceRequest = userParticipation.getServiceRequest();

        if(serviceRequest.getMaxVolunteers().equals(serviceRequest.getRegisteredCount()) )
            serviceRequest.setStatus(RequestStatus.OPEN);

        serviceRequest.setRegisteredCount(serviceRequest.getRegisteredCount()-1);
        serviceRequestRepository.save(serviceRequest);

        return mapToResponseDto(userParticipation);
    }


    @Override
    public UserParticipationResponseDto withdraw(Long serviceId, Long userId) {

        UserParticipation userParticipation = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId,userId);

        if (userParticipation==null)
            throw new BusinessException("Participation doesnt exist");

        ServiceRequest service = userParticipation.getServiceRequest();

        LocalDateTime serviceDateTime =
                LocalDateTime.of(
                        userParticipation.getServiceRequest().getServiceDate(),
                        userParticipation.getServiceRequest().getServiceStartTime()
                );

        if (serviceDateTime.minusDays(2).isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot withdraw within 48 hours");
        }

        if (userParticipation.getStatus() != ParticipationStatus.APPROVED
                && userParticipation.getStatus() != ParticipationStatus.REQUESTED) {
            throw new BusinessException("Cannot withdraw in current status");
        }

        if(service.getRegisteredCount() - 1 < service.getMinVolunteers())
            throw new BusinessException("Can be withdrawed only when it crosses the min volunteer limit");

        userParticipation.setStatus(ParticipationStatus.WITHDRAWN);
        userParticipation.setWithdrawalTime(LocalDateTime.now());
        userParticipation.setReliabilityScoreImpact(0.0);


        service.setRegisteredCount(service.getRegisteredCount() - 1);

        userParticipationRepository.save(userParticipation);
        serviceRequestRepository.save(service);

        return mapToResponseDto(userParticipation);
    }


    @Override
    public UserParticipationResponseDto markAttendance(Long serviceId, Long userId, UserAttendanceDto dto) {

        UserParticipation userParticipation = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId,userId);

        if (userParticipation==null)
            throw new BusinessException("Participation doesnt exist");

        if (!userParticipation.getServiceRequest().getStatus()
                .equals(RequestStatus.COMPLETED))
            throw new BusinessException("Service IS NOT COMPLETED");

        if (dto.attended()) {
            userParticipation.setStatus(ParticipationStatus.ATTENDED);
            userParticipation.setReliabilityScoreImpact(1.0);
        } else {
            userParticipation.setStatus(ParticipationStatus.NO_SHOW);
            userParticipation.setReliabilityScoreImpact(-1.5);
        }

        userParticipation.setAttendanceMarkedAt(LocalDateTime.now());

        userParticipationRepository.save(userParticipation);

        return mapToResponseDto(userParticipation);
    }


    @Override
    public UserParticipationResponseDto complete(Long serviceId, Long userId) {

        UserParticipation userParticipation = userParticipationRepository.findByServiceRequest_IdAndUser_Id(serviceId,userId);

        if (userParticipation==null)
            throw new BusinessException("Participation doesnt exist");

        if (userParticipation.getStatus() != ParticipationStatus.ATTENDED)
            throw new BusinessException("Cannot complete");

        userParticipation.setStatus(ParticipationStatus.COMPLETED);
        userParticipationRepository.save(userParticipation);

        return mapToResponseDto(userParticipation);
    }


    @Override
    public Page<UserParticipationResponseDto>
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

        Page<UserParticipation> page;

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

            page = userParticipationRepository
                    .findByServiceRequest_IdAndStatus(
                            serviceId,
                            participationStatus,
                            pageable);

        } else {

            page = userParticipationRepository
                    .findByServiceRequest_Id(serviceId, pageable);
        }

        return page.map(this::mapToResponseDto);
    }


    private UserParticipationResponseDto mapToResponseDto(
            UserParticipation userParticipation
    ) {
        return new UserParticipationResponseDto(
                userParticipation.getId(),
                userParticipation.getServiceRequest().getId(),
                userParticipation.getServiceRequest().getTitle(),
                userParticipation.getUser().getId(),
                userParticipation.getUser().getUsername(),
                userParticipation.getStatus(),
                userParticipation.getAppliedAt()
        );
    }

    @Override
    public Page<UserParticipationResponseDto> getByUser(
            Long userId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order
    ) {

        Sort sort = order.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<UserParticipation> page =
                userParticipationRepository.findByUserId(userId, pageable);

        return page.map(this::mapToDto);
    }
    private UserParticipationResponseDto mapToDto(UserParticipation p) {

        return new UserParticipationResponseDto(
                p.getId(),
                p.getServiceRequest().getId(),
                p.getServiceRequest().getTitle(),
                p.getUser().getId(),
                p.getUser().getUsername(),
                p.getStatus(),
                p.getAppliedAt()
        );
    }
}