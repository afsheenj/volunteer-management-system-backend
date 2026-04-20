package com.vserve.project.service.impl;

import com.vserve.project.dto.CommonServiceRequestResponseDto;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.service.PostedRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostedRequestServiceImpl implements PostedRequestService {
    private final ServiceRequestRepository requestRepository;

    public PostedRequestServiceImpl(ServiceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    @Override
    public Page<CommonServiceRequestResponseDto> getAllRequests(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ServiceRequest> requestPage = requestRepository.findAll(pageable);

        return requestPage.map(this::mapToDto);
    }

    @Override
    public void deleteRequest(Long id) {

        ServiceRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        requestRepository.delete(request);
    }

    private CommonServiceRequestResponseDto mapToDto(ServiceRequest r) {

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
}
