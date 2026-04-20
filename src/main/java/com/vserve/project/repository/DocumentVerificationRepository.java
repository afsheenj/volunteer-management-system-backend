package com.vserve.project.repository;

import com.vserve.project.entity.DocumentVerification;
import com.vserve.project.entity.User;
import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;


import java.util.List;
import java.util.Optional;

public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification,Long> {
    boolean existsByUser(User user);

    Page<DocumentVerification> findByDocumentStatus(DocumentStatus documentStatus, Pageable pageable);

    Page<DocumentVerification> findByDocumentStatusAndDocumentType(DocumentStatus documentStatus, DocumentType documentType, Pageable pageable);

    Page<DocumentVerification> findByDocumentType(DocumentType documentType, Pageable pageable);

    @NativeQuery("""
        SELECT *
        FROM document_verifications
        WHERE user_id = :userId
        ORDER BY uploaded_at DESC
        LIMIT 1
    """)
    Optional<DocumentVerification> findByUserId(Long userId);

    long countByDocumentStatus(DocumentStatus status);

    List<DocumentVerification>
    findTop3ByDocumentStatusOrderByUploadedAtDesc(DocumentStatus documentStatus);


    boolean existsByUserIdAndDocumentStatus(Long userId, DocumentStatus documentStatus);

}
