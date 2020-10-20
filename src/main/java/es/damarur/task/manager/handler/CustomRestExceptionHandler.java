package es.damarur.task.manager.handler;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import es.damarur.task.manager.dto.ApiErrorDTO;
import es.damarur.task.manager.dto.ValidationErrorDTO;
import es.damarur.task.manager.exception.BusinessException;
import es.damarur.task.manager.exception.TaskNotFoundException;
import es.damarur.task.manager.util.TMConstant;

/**
 * The Class CustomRestExceptionHandler.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messages;

	/**
	 * Handle all.
	 *
	 * @param ex      the ex
	 * @param request the request
	 * @return the response entity
	 */
	@ExceptionHandler({ IllegalStateException.class, Exception.class })
	public ResponseEntity<Object> handleGeneral(final Exception ex, final WebRequest request) {
		return handleAll(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ BusinessException.class })
	public ResponseEntity<Object> handleBusiness(final BusinessException ex, final WebRequest request) {
		return handleAll(ex, HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ TaskNotFoundException.class })
	public ResponseEntity<Object> handleTaskNotFound(final TaskNotFoundException ex, final WebRequest request) {
		return handleAll(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
		List<ValidationErrorDTO> apiErrors = new ArrayList<>();
		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			String value = (violation.getInvalidValue() == null ? null : violation.getInvalidValue().toString());
			apiErrors
					.add(new ValidationErrorDTO(violation.getPropertyPath().toString(), value, violation.getMessage()));
		}
		return ResponseEntity.badRequest().body(apiErrors);
	}

	private ResponseEntity<Object> handleAll(final Exception ex, HttpStatus status) {
		String message = messages.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
		boolean defaultMessage = false;
		if (StringUtils.isBlank(message) || StringUtils.equalsIgnoreCase(message, ex.getMessage())) {
			message = messages.getMessage(TMConstant.ERROR_NOT_CONTROLED, null, LocaleContextHolder.getLocale());
			defaultMessage = true;
		}
		final ApiErrorDTO apiError = new ApiErrorDTO(status, message,
				defaultMessage ? TMConstant.ERROR_NOT_CONTROLED : ex.getMessage());
		return new ResponseEntity<>(apiError, creatContentTypeJsonHeader(), apiError.getStatus());
	}

	/**
	 * Creat content type json header.
	 *
	 * @return the http headers
	 */
	private HttpHeaders creatContentTypeJsonHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * Handle method argument not valid.
	 *
	 * @param ex      the ex
	 * @param headers the headers
	 * @param status  the status
	 * @param request the request
	 * @return the response entity
	 */
	// 400
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		List<ValidationErrorDTO> apiErrors = new ArrayList<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			String message = messages.getMessage(fieldError.getDefaultMessage(), null, LocaleContextHolder.getLocale());
			apiErrors.add(new ValidationErrorDTO(fieldError.getField(), fieldError.getRejectedValue().toString(), message));
		}
		return ResponseEntity.badRequest().body(apiErrors);
	}

	/**
	 * Handle missing servlet request parameter.
	 *
	 * @param ex      the ex
	 * @param headers the headers
	 * @param status  the status
	 * @param request the request
	 * @return the response entity
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		return handleAll(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle missing servlet request part.
	 *
	 * @param ex      the ex
	 * @param headers the headers
	 * @param status  the status
	 * @param request the request
	 * @return the response entity
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return handleAll(ex, HttpStatus.BAD_REQUEST);
	}

}
