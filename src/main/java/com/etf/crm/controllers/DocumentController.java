package com.etf.crm.controllers;

import com.etf.crm.dtos.DocumentDto;
import com.etf.crm.dtos.DocumentUploadDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.entities.Document;
import com.etf.crm.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(documentService.getDocumentsByContractId(contractId));
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadDocument(@RequestBody DocumentUploadDto uploadDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(documentService.uploadDocument(uploadDto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            byte[] documentBytes = documentService.downloadDocument(documentId);

            String fileName = document.getName();
            MediaType contentType = documentService.getContentType(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(contentType);
            headers.setContentLength(documentBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documentBytes);

        } catch (RuntimeException e) {
            System.err.println("Download error for document " + documentId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error downloading document: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}