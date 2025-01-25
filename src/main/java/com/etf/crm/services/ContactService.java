package com.etf.crm.services;

import com.etf.crm.entities.Contact;
import com.etf.crm.exceptions.DuplicateItemException;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public String saveContact(Contact contact) {
        this.checkDuplicateEmail(contact.getEmail());
        this.contactRepository.save(contact);
        return CONTACT_CREATED;
    }

    public Contact getContactById(Long id) {
        return this.contactRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
    }

    public List<Contact> getAllContacts() {
        return this.contactRepository.findAllByDeletedFalse();
    }

    public String deleteContact(Long id) {
        Contact existingContact = this.getContactById(id);
        existingContact.setDeleted(true);
        this.contactRepository.save(existingContact);
        return CONTACT_DELETED;
    }

    public String updateContact(Long id, Contact contact) {
        Contact existingContact = this.getContactById(id);

        if (!existingContact.getEmail().equals(contact.getEmail())) {
            this.checkDuplicateEmail(contact.getEmail(), id);
        }

        existingContact.setFirstName(contact.getFirstName());
        existingContact.setLastName(contact.getLastName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setPhone(contact.getPhone());
        existingContact.setDocumentType(contact.getDocumentType());
        existingContact.setDocumentId(contact.getDocumentId());
        existingContact.setModifiedBy(contact.getModifiedBy());
        this.contactRepository.save(existingContact);
        return CONTACT_UPDATED;
    }

    private void checkDuplicateEmail(String email, Long id) {
        if (this.contactRepository.findByEmailAndDeletedFalse(email)
                .filter(contact -> !contact.getId().equals(id)).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }
    }

    private void checkDuplicateEmail(String email) {
        if (this.contactRepository.findByEmailAndDeletedFalse(email).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_TAKEN);
        }
    }
}
