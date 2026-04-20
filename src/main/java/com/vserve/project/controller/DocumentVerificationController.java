package com.vserve.project.controller;


import com.vserve.project.dto.user.DocumentResponseDto;
import com.vserve.project.dto.user.DocumentStatusUpdateDto;
import com.vserve.project.dto.user.DocumentUploadRequestDto;
import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.DocumentVerificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentVerificationController {
   private final DocumentVerificationService documentVerificationService;

    public DocumentVerificationController(DocumentVerificationService documentVerificationService) {
        this.documentVerificationService = documentVerificationService;
    }

    @PostMapping("/user/upload-documents")
    public ResponseEntity<ApiResponse<String>> uploadDocuments(
            @RequestBody DocumentUploadRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("Document uploaded", documentVerificationService.documentUpload(dto))
        );
    }

    @PutMapping("/admin/documents-update/{id}")
    public ResponseEntity<ApiResponse<String>> updateStatus(
            @PathVariable(name = "id") Long id, @RequestBody DocumentStatusUpdateDto dto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("Document status updated", documentVerificationService.documentStatus(id,dto))
        );
    }

    @GetMapping("/user/documents/{userId}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getUserDocument(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "User document fetched successfully",
                        documentVerificationService.getDocumentByUserId(userId)
                )
        );
    }

    @GetMapping("/admin/documents")
    public ResponseEntity<ApiResponse<Page<DocumentResponseDto>>> getAllDocuments(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "documentType", required = false) String documentType,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "uploadedAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "desc") String order
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Documents fetched successfully",
                        documentVerificationService.getAllDocuments(
                                status,
                                documentType,
                                pageNumber,
                                pageSize,
                                sortBy,
                                order
                        )
                )
        );
    }

}
