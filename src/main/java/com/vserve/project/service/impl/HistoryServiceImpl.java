//package com.vserve.project.service.impl;
//
//import com.vserve.project.dto.user.UserHistoryDto;
//import com.vserve.project.dto.organization.OrganizationHistoryDto;
//import com.vserve.project.entity.*;
//import com.vserve.project.repository.*;
//import com.vserve.project.service.HistoryService;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class HistoryServiceImpl implements HistoryService {
//
//    private final ServiceRequestRepository serviceRequestRepository;
//    private final UserParticipationRepository userParticipationRepository;
//    private final OrganizationParticipationRepository organizationParticipationRepository;
//    private final FeedbackRepository feedbackRepository;
//
//    public HistoryServiceImpl(ServiceRequestRepository serviceRequestRepository,
//                              UserParticipationRepository userParticipationRepository,
//                              OrganizationParticipationRepository organizationParticipationRepository,
//                              FeedbackRepository feedbackRepository) {
//        this.serviceRequestRepository = serviceRequestRepository;
//        this.userParticipationRepository = userParticipationRepository;
//        this.organizationParticipationRepository = organizationParticipationRepository;
//        this.feedbackRepository = feedbackRepository;
//    }
//
//    @Override
//    public List<UserHistoryDto> getUserHistory(Long userId) {
//
//        List<UserHistoryDto> historyList = new ArrayList<>();
//
//        // Services posted by user
//        List<ServiceRequest> postedServices =
//                serviceRequestRepository.findByRequestedBy_Id(userId);
//
//        for (ServiceRequest sr : postedServices) {
//
//            historyList.add(new UserHistoryDto(
//                    sr.getId(),
//                    sr.getTitle(),
//                    sr.getDescription(),
//                    sr.getCategory().name(),
//                    sr.getLandmark(),
//                    sr.getRequestType(),
//                    sr.getStatus().name(),
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    sr.getCreatedAt()
//            ));
//        }
//
//        // Services where user participated
//        List<UserParticipation> participations = userParticipationRepository.findByUser_Id(userId);
//
//        for (UserParticipation up : participations) {
//
//            ServiceRequest sr = up.getServiceRequest();
//
//            Optional<Feedback> feedback =
//                    feedbackRepository.findByUserParticipation(up);
//
//            Integer rating = null;
//            String comment = null;
//
//            if (feedback.isPresent()) {
//                rating = feedback.get().getRating();
//                comment = feedback.get().getComment();
//            }
//
//            historyList.add(new UserHistoryDto(
//                    sr.getId(),
//                    sr.getTitle(),
//                    sr.getDescription(),
//                    sr.getCategory().name(),
//                    sr.getLandmark(),
//                    sr.getRequestType(),
//                    sr.getStatus().name(),
//                    up.getStatus().name(),
//                    up.getFeedbackGiven(),
//                    rating,
//                    comment,
//                    up.getAppliedAt(),
//                    sr.getCreatedAt()
//            ));
//        }
//
//        return historyList;
//    }
//
////    @Override
////    public List<OrganizationHistoryDto> getOrganizationHistory(Long organizationId) {
////
////        List<OrganizationHistoryDto> historyList = new ArrayList<>();
////
////        // Services posted by organization
////        List<ServiceRequest> postedServices =
////                serviceRequestRepository.findByOrganizationId(organizationId, null).getContent();
////
////        for (ServiceRequest sr : postedServices) {
////
////            historyList.add(new OrganizationHistoryDto(
////                    sr.getId(),
////                    sr.getTitle(),
////                    sr.getDescription(),
////                    sr.getCategory().name(),
////                    sr.getLandmark(),
////                    sr.getRequestType(),
////                    sr.getStatus().name(),
////                    null,
////                    null,
////                    null,
////                    null,
////                    null,
////                    null,
////                    null,
////                    sr.getCreatedAt()
////            ));
////        }
////
////        // Organization participated services
////        List<OrganizationParticipation> orgParticipations = organizationParticipationRepository.findByOrganization_Id(organizationId);
////
////        for (OrganizationParticipation op : orgParticipations) {
////
////            ServiceRequest sr = op.getServiceRequest();
////
////            Optional<Feedback> feedback =
////                    feedbackRepository.findByOrganizationParticipation(op);
////
////            Integer rating = null;
////            String comment = null;
////
////            if (feedback.isPresent()) {
////                rating = feedback.get().getRating();
////                comment = feedback.get().getComment();
////            }
////
////            historyList.add(new OrganizationHistoryDto(
////                    sr.getId(),
////                    sr.getTitle(),
////                    sr.getDescription(),
////                    sr.getCategory().name(),
////                    sr.getLandmark(),
////                    sr.getRequestType(),
////                    sr.getStatus().name(),
////                    null,
////                    null,
////                    op.getStatus().name(),
////                    op.getFeedbackGiven(),
////                    rating,
////                    comment,
////                    op.getAppliedAt(),
////                    sr.getCreatedAt()
////            ));
////        }
////
////        // Organization members participation
////        List<UserParticipation> memberParticipations = userParticipationRepository.findAll();
////
////        for (UserParticipation up : memberParticipations) {
////
////            if (up.getOrganization() != null &&
////                    up.getOrganization().getId().equals(organizationId)) {
////
////                ServiceRequest sr = up.getServiceRequest();
////
////                Optional<Feedback> feedback =
////                        feedbackRepository.findByUserParticipation(up);
////
////                Integer rating = null;
////                String comment = null;
////
////                if (feedback.isPresent()) {
////                    rating = feedback.get().getRating();
////                    comment = feedback.get().getComment();
////                }
////
////                historyList.add(new OrganizationHistoryDto(
////                        sr.getId(),
////                        sr.getTitle(),
////                        sr.getDescription(),
////                        sr.getCategory().name(),
////                        sr.getLandmark(),
////                        sr.getRequestType(),
////                        sr.getStatus().name(),
////                        up.getUser().getId(),
////                        up.getUser().getUsername(),
////                        up.getStatus().name(),
////                        up.getFeedbackGiven(),
////                        rating,
////                        comment,
////                        up.getAppliedAt(),
////                        sr.getCreatedAt()
////                ));
////            }
////        }
////        return historyList;
////    }
//
//    @Override
//    public List<OrganizationHistoryDto> getOrganizationHistory(Long organizationId) {
//        List<OrganizationHistoryDto> historyList = new ArrayList<>();
//
//        // 1. POSTED: Only services created by this Organization
//        List<ServiceRequest> postedServices =
//                serviceRequestRepository.findByOrganizationId(organizationId, null).getContent();
//
//        for (ServiceRequest sr : postedServices) {
//            historyList.add(new OrganizationHistoryDto(
//                    sr.getId(), sr.getTitle(), sr.getDescription(), sr.getCategory().name(),
//                    sr.getLandmark(), sr.getRequestType(), sr.getStatus().name(),
//                    null, null, null, null, null, null, null, sr.getCreatedAt()
//            ));
//        }
//
//        // 2. JOINED: Only services where this Organization is a participant/partner
//        List<OrganizationParticipation> orgParticipations =
//                organizationParticipationRepository.findByOrganization_Id(organizationId);
//
//        for (OrganizationParticipation op : orgParticipations) {
//            ServiceRequest sr = op.getServiceRequest();
//
//            // ADD THIS LOGIC TO FIX THE ERROR:
//            Optional<Feedback> feedback = feedbackRepository.findByOrganizationParticipation(op);
//
//            Integer rating = null;
//            String comment = null;
//
//            if (feedback.isPresent()) {
//                rating = feedback.get().getRating();
//                comment = feedback.get().getComment();
//            }
//
//            historyList.add(new OrganizationHistoryDto(
//                    sr.getId(),
//                    sr.getTitle(),
//                    sr.getDescription(),
//                    sr.getCategory().name(),
//                    sr.getLandmark(),
//                    sr.getRequestType(),
//                    sr.getStatus().name(),
//                    null,
//                    null,
//                    op.getStatus().name(),
//                    op.getFeedbackGiven(),
//                    rating,   // Now defined!
//                    comment,  // Now defined!
//                    op.getAppliedAt(),
//                    sr.getCreatedAt()
//            ));
//        }
//
//        return historyList;
//    }
//
//}








package com.vserve.project.service.impl;

import com.vserve.project.dto.ParticipationResponseDto;
import com.vserve.project.dto.user.UserHistoryDto;
import com.vserve.project.dto.organization.OrganizationHistoryDto;
import com.vserve.project.entity.*;
import com.vserve.project.repository.*;
import com.vserve.project.service.HistoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserParticipationRepository userParticipationRepository;
    private final OrganizationParticipationRepository organizationParticipationRepository;
    private final FeedbackRepository feedbackRepository;

    public HistoryServiceImpl(ServiceRequestRepository serviceRequestRepository,
                              UserParticipationRepository userParticipationRepository,
                              OrganizationParticipationRepository organizationParticipationRepository,
                              FeedbackRepository feedbackRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userParticipationRepository = userParticipationRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Helper method to fetch all participants (Users + Organizations) for any given service.
     */
    private List<ParticipationResponseDto> getParticipantsForService(Long serviceId) {
        List<ParticipationResponseDto> participants = new ArrayList<>();

        // 1. Add Individual User Participants
        userParticipationRepository.findByServiceRequest_Id(serviceId).forEach(up ->
                participants.add(new ParticipationResponseDto(
                        up.getId(), serviceId, null, up.getUser().getId(),
                        up.getUser().getUsername(), "USER", up.getStatus().name(), up.getAppliedAt(),null))
        );

        // 2. Add Organization Participants
        organizationParticipationRepository.findByServiceRequest_Id(serviceId).forEach(op ->
                participants.add(new ParticipationResponseDto(
                        op.getId(), serviceId, null, op.getOrganization().getId(),
                        op.getOrganization().getOrgName(), "ORGANIZATION", op.getStatus().name(), op.getAppliedAt(),op.getMemberCount()))
        );

        return participants;
    }

    @Override
    public List<UserHistoryDto> getUserHistory(Long userId) {
        List<UserHistoryDto> historyList = new ArrayList<>();

        // A. Services posted by this user
        List<ServiceRequest> postedServices = serviceRequestRepository.findByRequestedBy_Id(userId);
        for (ServiceRequest sr : postedServices) {
            historyList.add(new UserHistoryDto(
                    sr.getId(), sr.getTitle(), sr.getDescription(), sr.getCategory().name(),
                    sr.getLandmark(), sr.getRequestType(), sr.getStatus().name(),
                    null, null, null, null, null, sr.getCreatedAt(),
                    getParticipantsForService(sr.getId())
            ));
        }

        // B. Services where this user volunteered
        List<UserParticipation> participations = userParticipationRepository.findByUser_Id(userId);
        for (UserParticipation up : participations) {
            ServiceRequest sr = up.getServiceRequest();
            Optional<Feedback> feedback = feedbackRepository.findByUserParticipation(up);

            historyList.add(new UserHistoryDto(
                    sr.getId(), sr.getTitle(), sr.getDescription(), sr.getCategory().name(),
                    sr.getLandmark(), sr.getRequestType(), sr.getStatus().name(),
                    up.getStatus().name(), up.getFeedbackGiven(),
                    feedback.map(Feedback::getRating).orElse(null),
                    feedback.map(Feedback::getComment).orElse(null),
                    up.getAppliedAt(), sr.getCreatedAt(),
                    getParticipantsForService(sr.getId())
            ));
        }
        return historyList;
    }

    @Override
    public List<OrganizationHistoryDto> getOrganizationHistory(Long organizationId) {
        List<OrganizationHistoryDto> historyList = new ArrayList<>();

        // A. Services posted by this organization
        List<ServiceRequest> postedServices = serviceRequestRepository.findByOrganizationId(organizationId, null).getContent();
        for (ServiceRequest sr : postedServices) {
            historyList.add(new OrganizationHistoryDto(
                    sr.getId(), sr.getTitle(), sr.getDescription(), sr.getCategory().name(),
                    sr.getLandmark(), sr.getRequestType(), sr.getStatus().name(),
                    null, null, null, null, null, null, null, sr.getCreatedAt(),
                    getParticipantsForService(sr.getId())
            ));
        }

        // B. Services where this organization participated as a group
        List<OrganizationParticipation> orgParticipations = organizationParticipationRepository.findByOrganization_Id(organizationId);
        for (OrganizationParticipation op : orgParticipations) {
            ServiceRequest sr = op.getServiceRequest();
            Optional<Feedback> feedback = feedbackRepository.findByOrganizationParticipation(op);

            historyList.add(new OrganizationHistoryDto(
                    sr.getId(), sr.getTitle(), sr.getDescription(), sr.getCategory().name(),
                    sr.getLandmark(), sr.getRequestType(), sr.getStatus().name(),
                    null, null, op.getStatus().name(), op.getFeedbackGiven(),
                    feedback.map(Feedback::getRating).orElse(null),
                    feedback.map(Feedback::getComment).orElse(null),
                    op.getAppliedAt(), sr.getCreatedAt(),
                    getParticipantsForService(sr.getId())
            ));
        }
        return historyList;
    }
}