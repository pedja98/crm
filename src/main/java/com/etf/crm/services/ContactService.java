package com.etf.crm.services;

import com.etf.crm.entities.Contact;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.reflect.Field;

import static com.etf.crm.common.CrmConstants.ErrorCodes.CONTACT_NOT_FOUND;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact saveContact(Contact contact) {
        return this.contactRepository.save(contact);
    }

    public Contact getContactById(Long id) {
        return this.contactRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
    }

    public List<Contact> getAllContacts() {
        return this.contactRepository.findAllByDeletedFalse();
    }

    public void deleteContact(Long id) {
        Contact existingContact = this.getContactById(id);
        existingContact.setDeleted(true);
        this.contactRepository.save(existingContact);
    }

    public Contact updateContact(Long id, Contact contactDetails) {
        Contact existingContact = this.getContactById(id);
        existingContact.setFirstName(contactDetails.getFirstName());
        existingContact.setLastName(contactDetails.getLastName());
        existingContact.setVerificationEmail(contactDetails.getVerificationEmail());
        existingContact.setPhone(contactDetails.getPhone());
        existingContact.setDocumentType(contactDetails.getDocumentType());
        existingContact.setDocumentId(contactDetails.getDocumentId());
        existingContact.setModifiedBy(contactDetails.getModifiedBy());
        return this.contactRepository.save(existingContact);
    }

    public void partialUpdateContact(Long id, String fieldName, Object fieldValue) {
        Contact existingContact = this.getContactById(id);
        try {
            Field field = Contact.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(existingContact, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
        this.contactRepository.save(existingContact);
    }
}
