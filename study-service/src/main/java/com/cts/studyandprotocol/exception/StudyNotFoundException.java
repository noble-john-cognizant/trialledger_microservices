package com.cts.studyandprotocol.exception;

public class StudyNotFoundException extends RuntimeException {
    public StudyNotFoundException(Long studyId) {
        super("Study not found with ID: " + studyId);
    }
}
