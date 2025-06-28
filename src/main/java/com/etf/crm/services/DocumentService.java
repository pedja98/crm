package com.etf.crm.services;

import com.etf.crm.dtos.DocumentUploadDto;
import com.etf.crm.entities.Contract;
import com.etf.crm.entities.Document;
import com.etf.crm.entities.User;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.ContractRepository;
import com.etf.crm.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ContractRepository contractRepository;

    public List<Document> getDocumentsByContractId(Long contractId) {
        return documentRepository.findByContractIdAndDeletedFalse(contractId);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public Document uploadDocument(DocumentUploadDto uploadDto) {
        Contract contract = contractRepository.findById(uploadDto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + uploadDto.getContractId()));
        try {
            Base64.getDecoder().decode(uploadDto.getFileContent());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 file content");
        }

        Document document = Document.builder()
                .name(uploadDto.getDocumentName())
                .documentContent(uploadDto.getFileContent())
                .contract(contract)
                .createdBy(SetCurrentUserFilter.getCurrentUser())
                .deleted(false)
                .build();

        return documentRepository.save(document);
    }

    public byte[] downloadDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        return Base64.getDecoder().decode(document.getDocumentContent());
    }

    public void softDeleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        document.setDeleted(true);
        document.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllNotDeleted();
    }
}