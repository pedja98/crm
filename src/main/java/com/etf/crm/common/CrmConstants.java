package com.etf.crm.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrmConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorCodes {
        public static String USER_NOT_FOUND = "userNotFound";

        public static String WRONG_PASSWORD = "wrongPassword";

        public static String COMPANY_NOT_FOUND = "companyNotFound";

        public static String CUSTOMER_SESSION_NOT_FOUND = "customerSessionNotFound";

        public static String CONTACT_NOT_FOUND = "contactNotFound";

        public static String RELATION_NOT_FOUND = "relationNotFound";

        public static String OPPORTUNITY_NOT_FOUND = "opportunityNotFound";

        public static String OFFER_NOT_FOUND = "offerNotFound";

        public static String CONTRACT_NOT_FOUND = "contractNotFound";

        public static String USERNAME_ALREADY_TAKEN = "usernameAlreadyTaken";

        public static String EMAIL_ALREADY_TAKEN = "emailAlreadyTaken";
    }

}
