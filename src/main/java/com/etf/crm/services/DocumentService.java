package com.etf.crm.services;

import com.etf.crm.dtos.DocumentDto;
import com.etf.crm.dtos.DocumentUploadDto;
import com.etf.crm.entities.Contract;
import com.etf.crm.entities.Document;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.ContractRepository;
import com.etf.crm.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.SuccessCodes.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ContractRepository contractRepository;

    public List<DocumentDto> getDocumentsByContractId(Long contractId) {
        return documentRepository.findDocumentDtosByContractIdAndDeletedFalse(contractId);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(DOCUMENT_NOT_FOUND));
    }

    public String uploadDocument(DocumentUploadDto uploadDto) {
        Contract contract = contractRepository.findById(uploadDto.getContractId())
                .orElseThrow(() -> new RuntimeException(CONTRACT_NOT_FOUND));

        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(uploadDto.getFileContent());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 file content");
        }

        Document document = Document.builder()
                .name(uploadDto.getFileName())
                .documentContent(fileBytes)
                .contract(contract)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        documentRepository.save(document);
        return DOCUMENT_UPLOADED;
    }

    public String getDocumentContent(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return Base64.getEncoder().encodeToString(document.getDocumentContent());
    }

    public byte[] downloadDocument(Long documentId) {
        Document document = getDocumentById(documentId);

        if (document == null) {
            throw new RuntimeException("Document not found");
        }

        if (document.getDeleted()) {
            throw new RuntimeException("Document has been deleted");
        }

        byte[] content = document.getDocumentContent();

        if (content == null || content.length == 0) {
            throw new RuntimeException("Document content is empty");
        }

        return content;
    }

    public MediaType getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "doc", "docx" -> MediaType.valueOf("application/msword");
            case "txt" -> MediaType.TEXT_PLAIN;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public void deleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        document.setDeleted(true);
        document.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        documentRepository.save(document);
    }
}