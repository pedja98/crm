package com.etf.crm.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrmConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorCodes {
        public static final String COMPANY_ASSIGNED_TO_USER_SHOP_EMPTY = "companyAssignedToUserShopEmpty";

        public static final String UNAUTHORIZED = "unauthorized";

        public static final String COMPANY_TEMPORARY_ASSIGNED_TO_USER_ERROR = "companyTemporaryAssignedToUserError";

        public static final String OFFER_STATUS_TRANSITION_NOT_POSSIBLE = "offerStatusTransitionNotPossible";

        public static final String INVALID_REQUEST = "invalidRequest";

        public static final String CAN_NOT_CREATE_OPPORTUNITY_FOR_COMPANY_IN_THIS_STATUS = "canNotCreateOpportunityForCompanyInThisStatus";

        public static final String INVALID_SESSION_DATE_TIME = "invalidSessionDateTime";

        public static final String THERE_IS_ALREADY_SIGNED_CONTRACT = "thereIsAlreadySignedContract";

        public static final String NOT_EDITABLE = "notEditable";

        public static final String USER_NOT_FOUND = "userNotFound";

        public static final String NO_USERS_FOUND = "noUsersFound";

        public static final String WRONG_PASSWORD = "wrongPassword";

        public static final String COMPANY_NOT_FOUND = "companyNotFound";

        public static final String DOCUMENT_NOT_FOUND = "documentNotFound";

        public static final String PASSWORD_NOT_CHANGED = "passwordNotChanged";

        public static final String INVALID_PASSWORD_FORMAT = "invalidPasswordFormat";

        public static final String CUSTOMER_SESSION_NOT_FOUND = "customerSessionNotFound";

        public static final String CONTACT_NOT_FOUND = "contactNotFound";

        public static final String RELATION_NOT_FOUND = "relationNotFound";

        public static final String OPPORTUNITY_NOT_FOUND = "opportunityNotFound";

        public static final String SHOP_NOT_FOUND = "shopNotFound";

        public static final String OFFER_NOT_FOUND = "offerNotFound";

        public static final String OFFER_UPDATED = "offerUpdated";

        public static final String CONTRACT_NOT_FOUND = "contractNotFound";

        public static final String USERNAME_ALREADY_TAKEN = "usernameAlreadyTaken";

        public static final String EMAIL_ALREADY_TAKEN = "emailAlreadyTaken";

        public static final String TIN_ALREADY_TAKEN = "tinAlreadyTaken";

        public static final String ASSIGNED_TO_SAME_AS_TEMPORARY = "assignedToSameAsTemporary";

        public static final String CAN_NOT_INSERT_EMPTY_VALUE = "canNotInsertEmptyValue";

        public static final String ENTITY_UPDATE_ERROR = "entityUpdateError";

        public static final String ILLEGAL_SORT_PARAMETER = "illegalSortParameter";

        public static final String REGION_ALREADY_EXISTS = "regionAlreadyExists";

        public static final String REGION_NOT_FOUND = "regionNotFound";

        public static final String INVALID_OFFER_STATUS = "invalidOfferStatus";

        public static final String DOCUMENT_NOT_UPLOADED = "documentNotUploaded";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SuccessCodes {
        public static final String CONTRACT_SIGN = "contractSign";

        public static final String CONTRACT_CLOSED = "contractClosed";

        public static final String CONTRACT_VERIFY = "contractVerify";

        public static final String CREATE_CONTRACT = "createContract";

        public static final String DOCUMENT_UPLOADED = "documentUploaded";

        public static  String SHOP_DELETED = "shopDeleted";

        public static  String OPPORTUNITY_CLOSED = "opportunityClosed";

        public static  String SHOP_CREATED = "shopCreated";

        public static final String USER_UPDATED = "userUpdated";

        public static final String USER_CREATED = "userCreated";

        public static final String PASSWORD_CHANGED = "passwordChanged";

        public static final String REGION_CREATED = "regionCreated";

        public static final String USER_DELETED = "userDeleted";

        public static final String REGION_DELETED = "regionDeleted";

        public static final String REGION_UPDATED = "regionUpdated";

        public static final String COMPANY_CREATED = "companyCreated";

        public static final String COMPANY_UPDATED = "companyUpdated";

        public static final String SHOP_UPDATED = "shopUpdated";

        public static final String CONTACT_CREATED = "contactCreated";

        public static final String CONTACT_UPDATED = "contactUpdated";

        public static final String CONTACT_DELETED = "contactDeleted";

        public static final String CUSTOMER_SESSION_CREATED = "customerSessionCreated";

        public static final String CUSTOMER_SESSION_UPDATED = "customerSessionUpdated";

        public static final String RELATION_DELETED = "relationDeleted";

        public static final String RELATION_UPDATED = "relationUpdated";

        public static final String ALL_RELATIONS_CREATED = "allRelationsCreated";

        public static final String OPPORTUNITY_CREATED = "opportunityCreated";
    }
}
