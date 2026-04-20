package com.vserve.project.service;

import com.vserve.project.dto.user.DocumentResponseDto;
import com.vserve.project.dto.user.DocumentStatusUpdateDto;
import com.vserve.project.dto.user.DocumentUploadRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DocumentVerificationService {
    String documentUpload(DocumentUploadRequestDto dto);
    String documentStatus(Long id,DocumentStatusUpdateDto dto);
    Page<DocumentResponseDto> getAllDocuments(
            String status,
            String documentType,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order);


    DocumentResponseDto getDocumentByUserId(Long userId);
}
