package es.damarur.task.manager.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "timestamp", "status", "message" })
public class ApiErrorDTO {

	private String timestamp;
	private HttpStatus status;
	private String message;
	private String code;

	public ApiErrorDTO() {
		super();
		this.timestamp = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
	}

	public ApiErrorDTO(final HttpStatus status, final String message, final String code) {
		this();
		this.status = status;
		this.message = message != null ? message : "";
		this.code = code != null ? code : "";
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public void setStatus(final HttpStatus status) {
		this.status = status;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}