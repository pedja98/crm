package com.etf.crm.controllers;

import com.etf.crm.dtos.ContactDto;
import com.etf.crm.dtos.MessageResponse;
import com.etf.crm.entities.Contact;
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
    public ResponseEntity<MessageResponse> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok(new MessageResponse(contactService.saveContact(contact)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateContact(@PathVariable Long id, @RequestBody Contact contactDetails) {
        return ResponseEntity.ok(new MessageResponse(contactService.updateContact(id, contactDetails)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteContact(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageResponse(contactService.deleteContact(id)));
    }
}
