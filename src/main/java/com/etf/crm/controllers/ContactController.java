package com.etf.crm.controllers;

import com.etf.crm.entities.Contact;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok(contactService.saveContact(contact));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contactDetails) {
        return ResponseEntity.ok(contactService.updateContact(id, contactDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contact> partialUpdateContact(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        updates.forEach((fieldName, fieldValue) -> {
            contactService.partialUpdateContact(id, fieldName, fieldValue);
        });
        return ResponseEntity.ok(contactService.getContactById(id));
    }
}
