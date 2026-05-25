package com.cts.adverseevent.exception;

public class StudyNotFoundException extends RuntimeException {
    public StudyNotFoundException(Long studyId) {
        super("Study not found with ID: " + studyId);
    }
}
