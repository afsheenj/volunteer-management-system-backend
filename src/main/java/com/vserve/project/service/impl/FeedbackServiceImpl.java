package com.vserve.project.service.impl;

import com.vserve.project.dto.CreateFeedbackDto;
import com.vserve.project.dto.FeedbackResponseDto;
import com.vserve.project.entity.*;
import com.vserve.project.enums.ParticipationStatus;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.*;
import com.vserve.project.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserParticipationRepository userParticipationRepository;
    private final OrganizationParticipationRepository organizationParticipationRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            UserParticipationRepository userParticipationRepository,
            OrganizationParticipationRepository organizationParticipationRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository) {

        this.feedbackRepository = feedbackRepository;
        this.userParticipationRepository = userParticipationRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public FeedbackResponseDto createFeedback(CreateFeedbackDto dto) {

        Feedback feedback = new Feedback();

        if (dto.userParticipationId() != null) {
            UserParticipation participation = userParticipationRepository
                    .findById(dto.userParticipationId())
                    .orElseThrow(() -> new BusinessException("User participation not found"));

            if(Boolean.TRUE.equals(participation.getFeedbackGiven())){
                throw new BusinessException("Feedback already given for this participation");
            }

            if (participation.getStatus() != ParticipationStatus.ATTENDED) {
                throw new BusinessException("Cannot give feedback before participation is completed");
            }

            if (participation.getServiceRequest().getStatus() != RequestStatus.COMPLETED) {
                throw new BusinessException("Cannot give feedback before service is completed");
            }

            feedback.setUserParticipation(participation);
            participation.setFeedbackGiven(true);
            userParticipationRepository.save(participation);
        }

        if (dto.organizationParticipationId() != null) {
            OrganizationParticipation participation = organizationParticipationRepository
                    .findById(dto.organizationParticipationId())
                    .orElseThrow(() -> new BusinessException("Organization participation not found"));

            if(Boolean.TRUE.equals(participation.getFeedbackGiven())){
                throw new BusinessException("Feedback already given for this participation");
            }

            if (participation.getStatus() != ParticipationStatus.ATTENDED) {
                throw new BusinessException("Cannot give feedback before participation is completed");
            }

            if (participation.getServiceRequest().getStatus() != RequestStatus.COMPLETED) {
                throw new BusinessException("Cannot give feedback before service is completed");
            }

            feedback.setOrganizationParticipation(participation);
            participation.setFeedbackGiven(true);
            organizationParticipationRepository.save(participation); // persist feedbackGiven update
        }

        if (dto.givenByUserId() != null) {
            User user = userRepository.findById(dto.givenByUserId())
                    .orElseThrow(() -> new BusinessException("User not found"));
            feedback.setGivenByUser(user);
        }

        if (dto.givenByOrganizationId() != null) {
            Organization org = organizationRepository.findById(dto.givenByOrganizationId())
                    .orElseThrow(() -> new BusinessException("Organization not found"));
            feedback.setGivenByOrganization(org);
        }

        // --- Rating & Comment ---
        feedback.setRating(dto.rating());
        feedback.setComment(dto.comment());
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);

        return mapToDto(feedback);
    }

    @Override
    public List<FeedbackResponseDto> getByUserParticipation(Long id) {
        return feedbackRepository.findByUserParticipation_Id(id)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<FeedbackResponseDto> getByOrganizationParticipation(Long id) {
        return feedbackRepository.findByOrganizationParticipation_Id(id)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<FeedbackResponseDto> getByGivenByUser(Long userId) {
        return feedbackRepository.findByGivenByUser_Id(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<FeedbackResponseDto> getByGivenByOrganization(Long orgId) {
        return feedbackRepository.findByGivenByOrganization_Id(orgId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private FeedbackResponseDto mapToDto(Feedback feedback) {

        Long serviceId = null;
        if(feedback.getUserParticipation()!=null){
            serviceId = feedback.getUserParticipation().getServiceRequest().getId();
        }
        if(feedback.getOrganizationParticipation()!=null){
            serviceId = feedback.getOrganizationParticipation().getServiceRequest().getId();
        }

        String givenBy = null;
        if(feedback.getGivenByUser()!=null){
            givenBy = feedback.getGivenByUser().getUsername();
        }
        if(feedback.getGivenByOrganization()!=null){
            givenBy = feedback.getGivenByOrganization().getOrgName();
        }

        return new FeedbackResponseDto(
                feedback.getId(),
                serviceId,
                feedback.getUserParticipation()!=null ? feedback.getUserParticipation().getId() : null,
                feedback.getOrganizationParticipation()!=null ? feedback.getOrganizationParticipation().getId() : null,
                feedback.getRating(),
                feedback.getComment(),
                givenBy,
                feedback.getCreatedAt()
        );
    }
}