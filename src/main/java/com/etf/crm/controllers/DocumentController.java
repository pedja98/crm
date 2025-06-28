package com.etf.crm.controllers;

import com.etf.crm.dtos.DocumentUploadDto;
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
    public ResponseEntity<List<Document>> getDocumentsByContract(@PathVariable Long contractId) {
        List<Document> documents = documentService.getDocumentsByContractId(contractId);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(@RequestBody DocumentUploadDto uploadDto) {
        try {
            Document document = documentService.uploadDocument(uploadDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            byte[] documentBytes = documentService.downloadDocument(documentId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(documentBytes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> softDeleteDocument(
            @PathVariable Long documentId,
            @RequestParam("modifiedBy") Long modifiedById) {

        try {
            documentService.softDeleteDocument(documentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}