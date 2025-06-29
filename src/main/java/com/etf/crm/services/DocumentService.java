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
    private final FileStorageService fileStorageService;

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

        // Store file in file system
        String filePath = fileStorageService.storeFile(fileBytes, uploadDto.getFileName(), uploadDto.getContractId());

        // Determine content type
        String contentType = uploadDto.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = getContentType(uploadDto.getFileName()).toString();
        }

        // Save document metadata to database
        Document document = Document.builder()
                .name(uploadDto.getFileName())
                .filePath(filePath)
                .contentType(contentType)
                .fileSize((long) fileBytes.length)
                .contract(contract)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        documentRepository.save(document);
        return DOCUMENT_UPLOADED;
    }

    public String getDocumentContent(Long documentId) {
        Document document = getDocumentById(documentId);

        if (!fileStorageService.fileExists(document.getFilePath())) {
            throw new RuntimeException("Document file not found on disk");
        }

        byte[] fileBytes = fileStorageService.loadFile(document.getFilePath());
        return Base64.getEncoder().encodeToString(fileBytes);
    }


    public MediaType getContentType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "doc", "docx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "txt" -> MediaType.TEXT_PLAIN;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "xlsx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "xls" -> MediaType.valueOf("application/vnd.ms-excel");
            case "ppt", "pptx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public void deleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);

        document.setDeleted(true);
        document.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        documentRepository.save(document);
        // fileStorageService.deleteFile(document.getFilePath());
    }

    public void permanentlyDeleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);

        // Delete physical file
        fileStorageService.deleteFile(document.getFilePath());

        // Delete from database
        documentRepository.delete(document);
    }
}