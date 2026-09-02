package com.jsp.foodorderingapplication.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jsp.foodorderingapplication.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ContactVerificationException.class)
	public ResponseEntity<ResponseStructure<String>> handleContactVerificationException(
			ContactVerificationException exception) {
		ResponseStructure<String> res = new ResponseStructure<>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ResponseStructure<String>> handleDataIntegrityException(
			DataIntegrityViolationException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.CONFLICT.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(NoRecordAvailableException.class)
	public ResponseEntity<ResponseStructure<String>> handleNoRecordAvailableException(
			NoRecordAvailableException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IdNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleIdNotFoundException(IdNotFoundException exception) {

		ResponseStructure<String> res = new ResponseStructure<>();

		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(InvalidFieldException.class)
	public ResponseEntity<ResponseStructure<String>> handleInvalidFieldException(InvalidFieldException exception) {
		
		ResponseStructure<String> res = new ResponseStructure<>();
		
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");
		
		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ResponseStructure<String>> handleInvalidDataException(InvalidDataException exception) {
		
		ResponseStructure<String> res = new ResponseStructure<>();
		
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setMessage(exception.getMessage());
		res.setData("Failure");
		
		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}
}
