package com.acme.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for translating & mapping unhandled exceptions.
 */
@ControllerAdvice
@Component
public class GlobalExceptionHandler {

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(MethodArgumentNotValidException exception) {
    return error(exception.getBindingResult().getFieldErrors()
      .stream()
      .map(FieldError::getDefaultMessage)
      .collect(Collectors.toList()));
  }


  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(ConstraintViolationException exception) {
    return error(exception.getConstraintViolations()
      .stream()
      .map(constraintViolation -> constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
      .collect(Collectors.toList()));
  }

  private Map error(Object message) {
    return Collections.singletonMap("error", message);
  }


  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map handle(InvalidParkingSpaceIdException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(MethodArgumentTypeMismatchException exception) {
    return error("Query parameters don't match with the predefined values!");
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(MissingServletRequestParameterException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(InvalidParkingSpaceStatusException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(InvalidParkingRequestException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(ParkingAlreadyCompletedException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map handle(UnsupportedOperationException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map handle(InvalidParkingIdException exception) {
    return error(exception.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map handle(Exception exception) {
    return error("Congratulations, you found a bug in our system!");
  }
}