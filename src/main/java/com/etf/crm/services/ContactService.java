package com.etf.crm.services;

import com.etf.crm.dtos.ContactDto;
import com.etf.crm.dtos.SaveContactDto;
import com.etf.crm.entities.Contact;
import com.etf.crm.entities.User;
import com.etf.crm.enums.ContactDocumentType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.etf.crm.common.CrmConstants.SuccessCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.*;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public String createContact(SaveContactDto saveContactData) {
        User createdBy = SetCurrentUserFilter.getCurrentUser();
        Contact contact = Contact.builder()
                .firstName(saveContactData.getFirstName())
                .lastName(saveContactData.getLastName())
                .email(saveContactData.getEmail())
                .phone(saveContactData.getPhone())
                .documentType(saveContactData.getDocumentType())
                .documentId(saveContactData.getDocumentId())
                .createdBy(createdBy)
                .deleted(false)
                .build();

        this.contactRepository.save(contact);
        return CONTACT_CREATED;
    }

    public ContactDto getContactById(Long id) {
        return this.contactRepository.findContactDtoByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
    }

    public List<ContactDto> getAllFilteredAndSortedContacts(String sortBy,
                                                            String sortOrder,
                                                            String firstName,
                                                            String lastName,
                                                            String email,
                                                            String phone,
                                                            List<ContactDocumentType> documentTypes,
                                                            String documentId) {
        List<ContactDto> contacts = this.contactRepository.findAllContactDtoByIdAndDeletedFalse()
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));

        Map<String, Object> filters = new HashMap<>();
        filters.put("firstName", firstName);
        filters.put("lastName", lastName);
        filters.put("email", email);
        filters.put("phone", phone);
        filters.put("documentTypes", documentTypes);
        filters.put("documentId", documentId);

        List<Predicate<ContactDto>> predicates = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();

                    return (Predicate<ContactDto>) contact -> {
                        try {
                            Field field = ContactDto.class.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(contact);

                            if (value instanceof String stringValue) {
                                return fieldValue != null && fieldValue.toString().toLowerCase().contains(stringValue.toLowerCase());
                            } else if (value instanceof List<?> listValue) {
                                return fieldValue != null && listValue.contains(fieldValue);
                            }
                            return false;
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(ILLEGAL_SORT_PARAMETER + ": " + fieldName, e);
                        }
                    };
                })
                .toList();

        for (Predicate<ContactDto> predicate : predicates) {
            contacts = contacts.stream().filter(predicate).toList();
        }

        if (sortBy != null) {
            Comparator<ContactDto> comparator = Comparator.comparing(contact -> {
                try {
                    Field field = ContactDto.class.getDeclaredField(sortBy);
                    field.setAccessible(true);
                    return (Comparable) field.get(contact);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException(ILLEGAL_SORT_PARAMETER + ": " + sortBy, e);
                }
            });

            if ("desc".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            contacts = contacts.stream().sorted(comparator).toList();
        }
        return contacts;
    }

    @Transactional
    public String deleteContact(Long id) {
        Contact contact = this.contactRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));
        contact.setDeleted(true);
        contact.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.contactRepository.save(contact);
        return CONTACT_DELETED;
    }

    @Transactional
    public String updateContact(Long id, SaveContactDto saveContact) {
        Contact contact = this.contactRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ItemNotFoundException(CONTACT_NOT_FOUND));

        contact.setFirstName(saveContact.getFirstName());
        contact.setLastName(saveContact.getLastName());
        contact.setEmail(saveContact.getEmail());
        contact.setPhone(saveContact.getPhone());
        contact.setDocumentType(saveContact.getDocumentType());
        contact.setDocumentId(saveContact.getDocumentId());
        contact.setModifiedBy(SetCurrentUserFilter.getCurrentUser());
        this.contactRepository.save(contact);

        return CONTACT_UPDATED;
    }
}
