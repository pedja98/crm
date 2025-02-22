package com.etf.crm.services;

import com.etf.crm.dtos.ContactDto;
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
        this.contactRepository.save(contact);
        return CONTACT_CREATED;
    }

    public ContactDto getContactById(Long id) {
        return this.contactRepository.findContactDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
    }

    public List<Contact> getAllContacts() {
        return this.contactRepository.findAllByDeletedFalse();
    }

    public String deleteContact(Long id) {
        Contact contact = this.contactRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
        contact.setDeleted(true);
        this.contactRepository.save(contact);
        return CONTACT_DELETED;
    }

    public String updateContact(Long id, Contact contact) {
        return CONTACT_UPDATED;
    }
}
