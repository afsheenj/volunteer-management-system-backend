package com.vserve.project.service;

import com.vserve.project.dto.CommonServiceRequestResponseDto;
import org.springframework.data.domain.Page;

public interface PostedRequestService {
    Page<CommonServiceRequestResponseDto> getAllRequests(int page, int size);

    void deleteRequest(Long id);
}
