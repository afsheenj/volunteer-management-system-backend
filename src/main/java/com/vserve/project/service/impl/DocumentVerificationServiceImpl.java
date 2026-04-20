package com.vserve.project.service.impl;

import com.vserve.project.dto.user.DocumentResponseDto;
import com.vserve.project.dto.user.DocumentStatusUpdateDto;
import com.vserve.project.dto.user.DocumentUploadRequestDto;
import com.vserve.project.entity.DocumentVerification;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.enums.DocumentType;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.DocumentVerificationRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.DocumentVerificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    private final DocumentVerificationRepository documentVerificationRepository;
    private final UserRepository userRepository;

    public DocumentVerificationServiceImpl(DocumentVerificationRepository documentVerificationRepository, UserRepository userRepository) {
        this.documentVerificationRepository = documentVerificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String documentUpload(DocumentUploadRequestDto dto) {
        User user = userRepository.findById(dto.userId()).orElse(null);
        if(user==null)
            throw new BusinessException("User doesn't exist");

        DocumentVerification documentVerification = mapToUploadDocument(user,dto);
        documentVerificationRepository.save(documentVerification);

        return "Document uploaded successfully";
    }

    private DocumentVerification mapToUploadDocument(User user, DocumentUploadRequestDto dto) {
        DocumentVerification documentVerification = new DocumentVerification();
        documentVerification.setUser(user);
        documentVerification.setDocumentType(dto.documentType());
        documentVerification.setDocumentUrl(dto.documentUrl());
        documentVerification.setDocumentStatus(DocumentStatus.SUBMITTED);
        documentVerification.setUploadedAt(LocalDateTime.now());
        return documentVerification;
    }

    @Override
    public String documentStatus(Long id, DocumentStatusUpdateDto dto) {

        DocumentVerification documentVerification =
                documentVerificationRepository.findById(id)
                        .orElseThrow(() -> new BusinessException("Document not found"));

        if (dto.documentStatus() == DocumentStatus.SUBMITTED) {
            throw new BusinessException("Invalid status update");
        }
        if (documentVerification.getDocumentStatus() == DocumentStatus.APPROVED) {
            throw new BusinessException("Approved document cannot be modified");
        }
        documentVerification.setDocumentStatus(dto.documentStatus());
        documentVerification.setReviewedAt(LocalDateTime.now());

        User user = userRepository.findById(
                documentVerification.getUser().getId()
        ).orElseThrow(() -> new BusinessException("User not found"));

        if (dto.documentStatus() == DocumentStatus.APPROVED) {
            user.setStatus(AccountStatus.ACTIVE);
        }
        if (dto.documentStatus() == DocumentStatus.REJECTED) {
            user.setStatus(AccountStatus.PENDING_VERIFICATION);
        }
        documentVerificationRepository.save(documentVerification);
        userRepository.save(user);
        return "Document status updated successfully";
    }


    @Override
    public Page<DocumentResponseDto> getAllDocuments(
            String status,
            String documentType,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order) {

        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        DocumentStatus documentStatus = null;
        DocumentType docType = null;


        if (status != null && !status.isBlank()) {
            try {
                documentStatus = DocumentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "Invalid document status. Allowed values: " +
                                Arrays.toString(DocumentStatus.values())
                );
            }
        }

        if (documentType != null && !documentType.isBlank()) {
            try {
                docType = DocumentType.valueOf(documentType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "Invalid document type. Allowed values: " +
                                Arrays.toString(DocumentType.values())
                );
            }
        }

        Page<DocumentVerification> page;

        if (documentStatus != null && docType != null) {

            page = documentVerificationRepository
                    .findByDocumentStatusAndDocumentType(
                            documentStatus,
                            docType,
                            pageable);

        } else if (documentStatus != null) {

            page = documentVerificationRepository
                    .findByDocumentStatus(documentStatus, pageable);

        } else if (docType != null) {

            page = documentVerificationRepository
                    .findByDocumentType(docType, pageable);

        } else {

            page = documentVerificationRepository.findAll(pageable);
        }

        return page.map(this::mapToResponseDto);
    }

    @Override
    public DocumentResponseDto getDocumentByUserId(Long userId) {

        return documentVerificationRepository
                .findByUserId(userId)
                .map(this::mapToResponseDto)
                .orElse(null);
    }


    private DocumentResponseDto mapToResponseDto(DocumentVerification doc) {

        return new DocumentResponseDto(
                doc.getId(),
                doc.getUser().getId(),
                doc.getUser().getUsername(),
                doc.getUser().getEmail(),
                doc.getDocumentType(),
                doc.getDocumentStatus(),
                doc.getDocumentUrl(),
                doc.getUploadedAt(),
                doc.getReviewedAt()
        );
    }
}
