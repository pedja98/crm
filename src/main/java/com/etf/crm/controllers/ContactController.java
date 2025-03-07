package com.etf.crm.controllers;

import com.etf.crm.dtos.ContactDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.dtos.SaveContactDto;
import com.etf.crm.enums.ContactDocumentType;
import com.etf.crm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<MessageResponse> createContact(@RequestBody SaveContactDto contact) {
        return ResponseEntity.ok(new MessageResponse(contactService.createContact(contact)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @GetMapping
    public ResponseEntity<List<ContactDto>> getAllContacts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, value = "documentType") List<ContactDocumentType> documentTypes,
            @RequestParam(required = false) String documentId
    ) {
        return ResponseEntity.ok(contactService.getAllFilteredAndSortedContacts(sortBy, sortOrder, firstName, lastName, email, phone, documentTypes, documentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateContact(@PathVariable Long id, @RequestBody SaveContactDto contactDetails) {
        return ResponseEntity.ok(new MessageResponse(contactService.updateContact(id, contactDetails)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteContact(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(contactService.deleteContact(id)));
    }
}
