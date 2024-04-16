package com.crmmarketingdigitalback2024.commons.constants.response;

public interface IResponse {
    String OPERATION_SUCCESS = "Operation successful";
    String OPERATION_FAIL = "Operation failed";
    String CREATE_SUCCESS = "OK successfully created";
    String CREATE_FAIL = "Not successfully created";
    String UPDATE_SUCCESS = "OK successfully updated";
    String UPDATE_FAIL = "Not updated correctly";
    String DELETE_SUCCESS = "OK was deleted correctly";
    String DELETE_FAIL = "Not correctly deleted";
    String NOT_FOUND = "Operation failed. not required";
    String DOCUMENT_FAIL = "Could not convert document: ";
    String INTERNAL_SERVER_ERROR = "Internal Server error. Unexpected system error.";
    String INTERNAL_SERVER = "Internal server error.";
}
