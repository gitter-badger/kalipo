package org.kalipo.web;

import org.apache.commons.lang3.StringUtils;
import org.kalipo.web.rest.KalipoRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handle business/technical exceptions to pass normalized message bodies
 * A brief message describing the error condition
 • A longer description with information on how to fix the error condition, if
 applicable
 • An identifier for the error
 • A link to learn more about the error condition, with tips on how to resolve it

 <error xmlns:atom="http://www.w3.org/2005/Atom">
 <message>Account limit exceeded. We cannot complete the transfer due to insufficient funds in your accounts</message>
 <error-id>321-553-495</error-id>
 <account-from>urn:example:account:1234</account-from>
 <account-to>urn:example:account:5678</account-to>
 <atom:link href="http://example.org/account/1234" rel="http://example.org/rels/transfer/from"/>
 <atom:link href="http://example.org/account/5678" rel="http://example.org/rels/transfer/to"/>
 </error>
 * 
 * Created by damoeb on 17.09.14.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    public static class ExceptionSummary {
        private int errorCode;
        private Object resource;
        private String errorMessage;

        public ExceptionSummary(String message) {
            this.errorMessage = message;
        }

        public ExceptionSummary(KalipoRequestException exception) {
            this.errorMessage = exception.getMessage();
            this.errorCode = exception.getErrorCode().getCode();
            this.resource = exception.getResource();
        }

        public Object getResource() {
            return resource;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    @ExceptionHandler(KalipoRequestException.class)
    private ResponseEntity<ExceptionSummary> handleKalipoException(KalipoRequestException exception) {
        return new ResponseEntity<>(new ExceptionSummary(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<ExceptionSummary> handleConstraintViolationException(ConstraintViolationException exception) {

        List<String> errors = new LinkedList<>();
        exception.getConstraintViolations().forEach(violation -> errors.add(violation.getMessage()));

        return new ResponseEntity<>(new ExceptionSummary(StringUtils.join(errors, ", ")), HttpStatus.BAD_REQUEST);
    }
}
