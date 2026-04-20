package com.vserve.project.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vserve.project.dto.CommonServiceRequestResponseDto;
import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.User;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.NotificationService;
import com.vserve.project.service.UserServiceRequestService;
import com.vserve.project.util.AccountValidator;
import com.vserve.project.util.EmbeddingClient;
import com.vserve.project.util.SimilarityUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceRequestServiceImpl implements UserServiceRequestService {
    private final UserRepository userRepository;
    private final ServiceRequestRepository requestRepository;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;
    private final AccountValidator accountValidator;
    private final JavaMailSender javaMailSender;
    private final NotificationService notificationService;

    public UserServiceRequestServiceImpl(
            UserRepository userRepository,
            ServiceRequestRepository requestRepository,
            EmbeddingClient embeddingClient,
            ObjectMapper objectMapper, AccountValidator accountValidator, JavaMailSender javaMailSender, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.embeddingClient = embeddingClient;
        this.objectMapper = objectMapper;
        this.accountValidator = accountValidator;
        this.javaMailSender = javaMailSender;
        this.notificationService = notificationService;
    }


    @Transactional
    @Override
    public String createRequest(Long userId, CreateServiceRequestDto dto) {
        accountValidator.validateActiveUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        if (user.getRole().equals("VOLUNTEER")) {
//            throw new RuntimeException("Volunteers cannot create requests");
//        }

        if(dto.minVolunteers()>=dto.maxVolunteers())
            throw new BusinessException("Maximum value of volunteers should be greater than minimum volunteers");

        String text = dto.title() + " " + dto.category() + " " + dto.description();

        List<Double> vector = embeddingClient.getEmbedding(text);

        ServiceRequest request = new ServiceRequest();
        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setLandmark(dto.landmark());
        request.setCity(dto.city());
        request.setState(dto.state());
        request.setStatus(RequestStatus.OPEN);
        request.setRequestedBy(user);
        request.setOrganization(null);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequestType(dto.requestType());
        request.setServiceDate(dto.serviceDate());
        request.setServiceStartTime(dto.serviceStartTime());
        request.setServiceEndTime(dto.serviceEndTime());
        request.setMinVolunteers(dto.minVolunteers());
        request.setMaxVolunteers(dto.maxVolunteers());

        try {
            request.setEmbeddingVector(objectMapper.writeValueAsString(vector));
        } catch (Exception e) {
            throw new RuntimeException("Embedding conversion failed");
        }
      requestRepository.save(request);

        notificationService.sendNotificationEmails(request);

        return "User request created successfully";
    }

    @Override
    public Page<ServiceRequestResponseDto> getUserRequests(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return requestRepository.findByRequestedById(userId, pageable)
                .map(this::mapToDto);
    }

    private ServiceRequestResponseDto mapToDto(ServiceRequest r) {
        return new ServiceRequestResponseDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getCategory(),
                r.getRequestType(),
                r.getLandmark(),
                r.getState(),
                r.getCity(),
                r.getStatus().name(),
                r.getServiceDate(),
                r.getServiceStartTime(),
                r.getMinVolunteers(),
                r.getMaxVolunteers(),
                r.getRegisteredCount(),
                r.getApprovedCount()


        );


    }

    private ServiceRequestEditDto mapToEditDto(ServiceRequest r) {
        return new ServiceRequestEditDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getCategory(),
                r.getRequestType(),
                r.getLandmark(),
                r.getState(),
                r.getCity(),
                r.getStatus().name(),
                r.getServiceDate(),
                r.getServiceStartTime(),
                r.getServiceEndTime(),
                r.getMinVolunteers(),
                r.getMaxVolunteers(),
                r.getRegisteredCount(),
                r.getApprovedCount()


        );


    }

    @Override
    public String updateUserRequest(Long userId, Long requestId, CreateServiceRequestDto dto) {

        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));


        if (request.getRequestedBy() == null ||
                !request.getRequestedBy().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this request");
        }

        if (request.getOrganization() != null) {
            throw new RuntimeException("This request belongs to an organization");
        }

        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setLandmark(dto.landmark());
        request.setState(dto.state());
        request.setCity(dto.city());
        request.setRequestType(dto.requestType());
        request.setServiceDate(dto.serviceDate());
        request.setServiceStartTime(dto.serviceStartTime());
        request.setServiceEndTime(dto.serviceEndTime());
        request.setMinVolunteers(dto.minVolunteers());
        request.setMaxVolunteers(dto.maxVolunteers());

        if (dto.minVolunteers() != null && dto.maxVolunteers() != null) {
            if (dto.minVolunteers() > dto.maxVolunteers()) {
                throw new RuntimeException("Min volunteers cannot exceed max volunteers");
            }
        }

        requestRepository.save(request);

        return "User request updated successfully";
    }

    @Override
    public String deleteUserRequest(Long userId, Long requestId) {

        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getRequestedBy() == null ||
                !request.getRequestedBy().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this request");
        }

        if (request.getOrganization() != null) {
            throw new RuntimeException("This request belongs to an organization");
        }

        requestRepository.delete(request);

        return "User request deleted successfully";
    }

    @Override
    public Page<ServiceRequestResponseDto> getRequestsForVolunteer(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Fetch only OPEN requests not created by this volunteer
        return requestRepository.findByStatusAndRequestedByIdNot(RequestStatus.OPEN, userId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<CommonServiceRequestResponseDto> aiSearch(
            int page,
            int size,
            String query,
            String location,
            String serviceType,
            LocalDate date) {

        boolean isQueryEmpty = (query == null || query.trim().isEmpty());

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // DB FILTER FIRST
        Page<ServiceRequest> pageResult =
                requestRepository.searchRequests(location, serviceType, date, pageable);

        List<ServiceRequest> services = pageResult.getContent();

        // ✅ IF QUERY EMPTY → RETURN DIRECTLY (NO AI)
        if (isQueryEmpty) {
            List<CommonServiceRequestResponseDto> content = services.stream()
                    .map(this::mapToCommonDto)
                    .toList();

            return new PageImpl<>(content, pageable, pageResult.getTotalElements());
        }

        // ✅ AI SEARCH
        List<Double> queryVector = embeddingClient.getEmbedding(query);

        double SIMILARITY_THRESHOLD = 0.05;

        List<ServiceRequest> filtered = services.stream()

                .filter(service -> service.getEmbeddingVector() != null)

                .map(service -> {
                    List<Double> serviceVector = parseVector(service.getEmbeddingVector());

                    if (serviceVector == null || serviceVector.isEmpty()) return null;

                    double similarity =
                            SimilarityUtil.cosineSimilarity(queryVector, serviceVector);

                    return Map.entry(service, similarity);
                })

                .filter(Objects::nonNull)
                .filter(entry -> entry.getValue() >= SIMILARITY_THRESHOLD)

                // SORT BY SIMILARITY + LATEST
                .sorted((a, b) -> {
                    int simCompare = Double.compare(b.getValue(), a.getValue());
                    if (simCompare != 0) return simCompare;

                    return b.getKey().getCreatedAt()
                            .compareTo(a.getKey().getCreatedAt());
                })

                .map(Map.Entry::getKey)
                .toList();

        // MANUAL PAGINATION
        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());

        List<CommonServiceRequestResponseDto> content = filtered.subList(start, end)
                .stream()
                .map(this::mapToCommonDto)
                .toList();

        return new PageImpl<>(content, pageable, filtered.size());
    }

    private Page<CommonServiceRequestResponseDto> paginate(
            List<ServiceRequest> list, int page, int size) {

        int start = Math.min(page * size, list.size());
        int end = Math.min(start + size, list.size());

        List<CommonServiceRequestResponseDto> content =
                list.subList(start, end)
                        .stream()
                        .map(this::mapToCommonDto)
                        .toList();

        return new PageImpl<>(content, PageRequest.of(page, size), list.size());
    }

    private CommonServiceRequestResponseDto mapToCommonDto(ServiceRequest r) {

        String type;
        Long createdById;
        String postedBy;
        String location;

        if (r.getRequestedBy() != null) {
            type = "USER";
            createdById = r.getRequestedBy().getId();
            postedBy = r.getRequestedBy().getUsername();
            location = r.getState() + "," + r.getCity();
        } else {
            type = "ORGANIZATION";
            createdById = r.getOrganization().getId();

            postedBy = r.getOrganization().getOrgName();
            location = r.getState() + "," + r.getCity();
        }
        return new CommonServiceRequestResponseDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getCategory(),
                r.getRequestType(),
                r.getLandmark(),
                location,
                r.getStatus().name(),
                r.getServiceDate(),
                r.getServiceStartTime(),
                r.getServiceEndTime(),
                r.getMinVolunteers(),
                r.getMaxVolunteers(),
                r.getRegisteredCount(),
                type,
                createdById,
                postedBy
        );
    }

    public ServiceRequestEditDto getRequestById(Long requestId) {
        ServiceRequest serviceRequest = requestRepository.findById(requestId).orElseThrow(
                () -> new RuntimeException("Service not found")
        );

        return mapToEditDto(serviceRequest);
    }

    @Override
    public String cancelRequest(Long id){
        ServiceRequest request=requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if(request.getStatus() == RequestStatus.COMPLETED)
            throw new BusinessException("Cannot be cancelled");

        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessException("Already Cancelled");
        }
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
        return "Service Request Cancelled Successfully";
    }


    private List<Double> parseVector(String json) {

        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
