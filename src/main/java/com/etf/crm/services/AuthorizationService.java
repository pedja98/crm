package com.etf.crm.services;

import com.etf.crm.dtos.CompanyDto;
import com.etf.crm.entities.Company;
import com.etf.crm.entities.User;
import com.etf.crm.enums.UserType;
import com.etf.crm.exceptions.ItemNotFoundException;
import com.etf.crm.exceptions.UnauthorizedException;
import com.etf.crm.filters.SetCurrentUserFilter;
import com.etf.crm.repositories.CompanyRepository;
import com.etf.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.etf.crm.common.CrmConstants.ErrorCodes.*;
import static com.etf.crm.common.CrmConstants.ErrorCodes.UNAUTHORIZED;

@Service
public class AuthorizationService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    public void isUserAuthorizedForAction(Long companyId) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();

        CompanyDto companyDto = this.companyRepository.findCompanyDtoByIdAndDeletedFalse(companyId)
                .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));

        if (currentUser.getType().equals(UserType.SALESMAN)
                && !companyDto.getAssignedToId().equals(currentUser.getId())
                && !companyDto.getTemporaryAssignedToId().equals(currentUser.getId())) {
            throw new UnauthorizedException(UNAUTHORIZED);
        } else if (currentUser.getType().equals(UserType.L1_MANAGER)) {
            User assignedTo = this.userRepository.findById(companyDto.getAssignedToId())
                    .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
            if (assignedTo.getShop().getId().equals(currentUser.getShop().getId())) {
                throw new UnauthorizedException(UNAUTHORIZED);
            }
        }
    }

    public <T> List<T> filterByUserAccess(List<T> items, Function<T, Long> companyIdFetcher) {
        User currentUser = SetCurrentUserFilter.getCurrentUser();

        if (currentUser.getType().equals(UserType.SALESMAN)) {
            return items.stream().filter(item -> {
                Long companyId = companyIdFetcher.apply(item);
                CompanyDto company = companyRepository.findCompanyDtoByIdAndDeletedFalse(companyId)
                        .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
                return Objects.equals(company.getAssignedToId(), currentUser.getId())
                        || Objects.equals(company.getTemporaryAssignedToId(), currentUser.getId());
            }).collect(Collectors.toList());
        } else if (currentUser.getType().equals(UserType.L1_MANAGER)) {
            return items.stream().filter(item -> {
                Long companyId = companyIdFetcher.apply(item);
                Company company = companyRepository.findById(companyId)
                        .orElseThrow(() -> new ItemNotFoundException(COMPANY_NOT_FOUND));
                return Objects.equals(company.getAssignedTo().getShop().getId(), currentUser.getShop().getId());
            }).collect(Collectors.toList());
        }

        return items;
    }
}
