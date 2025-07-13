package com.etf.crm.schedulers;

import com.etf.crm.entities.Company;
import com.etf.crm.enums.CompanyStatus;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyStatusScheduler {

    private final CompanyRepository companyRepository;
    private final ContractRepository contractRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateCompaniesWithExpiredContracts() {
        List<Company> activeCompanies = companyRepository.findAllByStatusAndDeletedFalse(CompanyStatus.ACTIVE);

        for (Company company : activeCompanies) {
            contractRepository.findLastSignedContractByCompanyId(company.getId()).ifPresent(contract -> {
                LocalDate expiryDate = contract.getDateSigned().plusMonths(contract.getContractObligation());
                if (LocalDate.now().isAfter(expiryDate)) {
                    company.setStatus(CompanyStatus.INACTIVE);
                    companyRepository.save(company);
                    log.info("Company '{}' (ID: {}) marked as INACTIVE due to expired contract.", company.getName(), company.getId());
                }
            });
        }
    }
}

