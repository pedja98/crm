package com.etf.crm.schedulers;

import com.etf.crm.entities.Company;
import com.etf.crm.entities.Contract;
import com.etf.crm.entities.User;
import com.etf.crm.enums.CompanyStatus;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryNotificationScheduler {

    private final CompanyRepository companyRepository;
    private final ContractRepository contractRepository;
    private final JavaMailSender mailSender;

    // Run every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void notifyAboutContractsExpiringInOneMonth() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusMonths(1);

        List<Company> activeCompanies = companyRepository.findAllByStatusAndDeletedFalse(CompanyStatus.ACTIVE);

        for (Company company : activeCompanies) {
            contractRepository.findLastSignedContractByCompanyId(company.getId()).ifPresent(contract -> {
                if (contract.getDateSigned() == null || contract.getContractObligation() == null) {
                    return;
                }

                LocalDate expiryDate = contract.getDateSigned().plusMonths(contract.getContractObligation());
                if (expiryDate.equals(targetDate)) {
                    notifyUsers(company, contract, expiryDate);
                }
            });
        }
    }

    private void notifyUsers(Company company, Contract contract, LocalDate expiryDate) {
        Set<User> recipients = new HashSet<>();
        if (company.getAssignedTo() != null) recipients.add(company.getAssignedTo());
        if (company.getTemporaryAssignedTo() != null) recipients.add(company.getTemporaryAssignedTo());

        for (User user : recipients) {
            sendNotificationEmail(user, contract, expiryDate);
        }
    }

    private void sendNotificationEmail(User user, Contract contract, LocalDate expiryDate) {
        String subject = "Ugovor za kompaniju " + contract.getCompany().getName() + " uskoro ističe";
        String body = String.format("""
                        Poštovani %s %s,
                        
                        Ugovor "%s" sa kompanijom "%s" ističe za mesec dana, tačnije dana: %s.
                        
                        Molimo Vas da preduzmete potrebne radnje.
                        
                        Srdačan pozdrav,
                        CRM sistem
                        """,
                user.getFirstName(),
                user.getLastName(),
                contract.getName(),
                contract.getCompany().getName(),
                expiryDate
        );

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Mail sent to user {}.", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send mail to user {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
