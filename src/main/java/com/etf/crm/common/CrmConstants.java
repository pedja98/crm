package com.etf.crm.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrmConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorCodes {
        public static String USER_NOT_FOUND = "userNotFound";

        public static String NO_USERS_FOUND = "noUsersFound";

        public static String WRONG_PASSWORD = "wrongPassword";

        public static String COMPANY_NOT_FOUND = "companyNotFound";

        public static String PASSWORD_NOT_CHANGED = "passwordNotChanged";

        public static String INVALID_PASSWORD_FORMAT = "invalidPasswordFormat";

        public static String CUSTOMER_SESSION_NOT_FOUND = "customerSessionNotFound";

        public static String CONTACT_NOT_FOUND = "contactNotFound";

        public static String RELATION_NOT_FOUND = "relationNotFound";

        public static String OPPORTUNITY_NOT_FOUND = "opportunityNotFound";

        public static String SHOP_NOT_FOUND = "shopNotFound";

        public static String OFFER_NOT_FOUND = "offerNotFound";

        public static String CONTRACT_NOT_FOUND = "contractNotFound";

        public static String USERNAME_ALREADY_TAKEN = "usernameAlreadyTaken";

        public static String EMAIL_ALREADY_TAKEN = "emailAlreadyTaken";

        public static String CAN_NOT_INSERT_EMPTY_VALUE = "canNotInsertEmptyValue";

        public static String ENTITY_UPDATE_ERROR = "entityUpdateError";

        public static String ILLEGAL_SORT_PARAMETER = "illegalSortParameter";

        public static String REGION_ALREADY_EXISTS = "regionAlreadyExists";

        public static String REGION_NOT_FOUND = "regionNotFound";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SuccessCodes {
        public static String USER_UPDATED = "userUpdated";

        public static String USER_CREATED = "userCreated";

        public static String PASSWORD_CHANGED = "passwordChanged";

        public static String REGION_CREATED = "regionCreated";

        public static String USER_DELETED = "userDeleted";

        public static String REGION_DELETED = "regionDeleted";

        public static String REGION_UPDATED = "regionUpdated";

        public static String COMPANY_CREATED = "companyCreated";

        public static String COMPANY_UPDATED = "companyUpdated";

        public static String CONTACT_CREATED = "contactCreated";

        public static String CONTACT_UPDATED = "contactUpdated";

        public static String CONTACT_DELETED = "contactDeleted";
    }
}
