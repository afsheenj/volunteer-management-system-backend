package com.vserve.project.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.service.NotificationService;
import com.vserve.project.service.OrganizationServiceRequestService;
import com.vserve.project.util.AccountValidator;
import com.vserve.project.util.EmbeddingClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrganizationServiceRequestServiceImpl implements OrganizationServiceRequestService {
    private final ServiceRequestRepository requestRepository;
    private final OrganizationRepository organizationRepository;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;
    private final AccountValidator accountValidator;
    private final NotificationService notificationService;

    public OrganizationServiceRequestServiceImpl(
            ServiceRequestRepository requestRepository,
            OrganizationRepository organizationRepository,
            EmbeddingClient embeddingClient,
            ObjectMapper objectMapper, AccountValidator accountValidator, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.organizationRepository = organizationRepository;
        this.embeddingClient = embeddingClient;
        this.objectMapper = objectMapper;
        this.accountValidator = accountValidator;
        this.notificationService = notificationService;
    }

    @Override
    public String createRequest(Long orgId, CreateServiceRequestDto dto) {

        accountValidator.validateActiveOrganization(orgId);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Integer members = organizationRepository.findMemberCount(orgId);

        if(members == 0){
            throw new BusinessException("Add members to your organization");
        }

        if(dto.minVolunteers() >= dto.maxVolunteers())
            throw new BusinessException("Maximum value of volunteers should be greater than minimum volunteers");

        String text = dto.title() + " " + dto.category() + " " + dto.description();

        List<Double> vector = embeddingClient.getEmbedding(text);

        ServiceRequest request = new ServiceRequest();
        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setLandmark(dto.landmark());
        request.setStatus(RequestStatus.OPEN);
        request.setRequestedBy(null);
        request.setOrganization(org);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequestType(dto.requestType());
        request.setServiceDate(dto.serviceDate());
        request.setServiceStartTime(dto.serviceStartTime());
        request.setServiceEndTime(dto.serviceEndTime());
        request.setCity(dto.city());
        request.setState(dto.state());
        request.setMinVolunteers(dto.minVolunteers());
        request.setMaxVolunteers(dto.maxVolunteers());

        try {
            request.setEmbeddingVector(objectMapper.writeValueAsString(vector));
        } catch (Exception e) {
            throw new RuntimeException("Embedding conversion failed");
        }

        requestRepository.save(request);

        notificationService.sendNotificationEmails(request);

        return "Organization request created successfully";
    }

    @Override
    public Page<ServiceRequestResponseDto> getOrgRequests(Long orgId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return requestRepository.findByOrganizationId(orgId, pageable)
                .map(this::mapToDto);
    }

    private ServiceRequestResponseDto mapToDto(ServiceRequest r) {
        String city = (r.getCity()==null)? "-" : r.getCity();
        String state = (r.getState()==null)? "-": r.getState();
        return new ServiceRequestResponseDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getCategory(),
                r.getRequestType(),
                r.getLandmark(),
                state,
                city,
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
        String city = (r.getCity()==null)? "-" : r.getCity();
        String state = (r.getState()==null)? "-": r.getState();
        return new ServiceRequestEditDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getCategory(),
                r.getRequestType(),
                r.getLandmark(),
                state,
                city,
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
    public String updateOrganizationRequest(Long orgId, Long requestId, CreateServiceRequestDto dto) {

        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getOrganization() == null ||
                !request.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Organization not authorized to update this request");
        }


        if (request.getRequestedBy() != null) {
            throw new RuntimeException("This request belongs to a user");
        }

        request.setTitle(dto.title());
        request.setDescription(dto.description());
        request.setCategory(dto.category());
        request.setLandmark(dto.landmark());
        request.setRequestType(dto.requestType());
        request.setServiceDate(dto.serviceDate());
        request.setServiceStartTime(dto.serviceStartTime());
        request.setServiceEndTime(dto.serviceEndTime());
        request.setCity(dto.city());
        request.setState(dto.state());
        request.setMinVolunteers(dto.minVolunteers());
        request.setMaxVolunteers(dto.maxVolunteers());
        if (dto.minVolunteers() != null && dto.maxVolunteers() != null) {
            if (dto.minVolunteers() > dto.maxVolunteers()) {
                throw new RuntimeException("Min volunteers cannot exceed max volunteers");
            }
        }

        requestRepository.save(request);

        return "Organization request updated successfully";
    }

    @Override
    public String deleteOrganizationRequest(Long orgId, Long requestId) {

        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getOrganization() == null ||
                !request.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Organization not authorized to delete this request");
        }

        if (request.getRequestedBy() != null) {
            throw new RuntimeException("This request belongs to a user");
        }

        requestRepository.delete(request);

        return "Organization request deleted successfully";
    }

    @Override
    public String cancelRequest(Long orgId ,Long id){
        ServiceRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException ("Request not Found"));
        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new RuntimeException("Completed request cannot be cancelled");
        }
        if (request.getStatus() == RequestStatus.CANCELLED) {
            return "Already Cancelled";
        }
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
        return "Request Cancelled Successfully";
    }

    @Override
    public ServiceRequestEditDto getRequestDetails(Long orgId, Long requestId) {

        ServiceRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (!req.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }
        return mapToEditDto(req);
    }

}
