package com.myor.emailservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email_log")
public class EmailLog {

	public static final String STATUS_SENT_PRIMARY = "SENT_PRIMARY";
	public static final String STATUS_SENT_FALLBACK = "SENT_FALLBACK";
	public static final String STATUS_FAILED = "FAILED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "from_address", columnDefinition = "TEXT")
	private String fromAddress;

	@Column(name = "to_address", columnDefinition = "TEXT")
	private String toAddress;

	private String subject;

	@Column(columnDefinition = "TEXT")
	private String message;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public EmailLog() {
	}

	public EmailLog(Email email, String status, String errorMessage) {
		this.fromAddress = email.getFrom();
		this.toAddress = email.getTo();
		this.subject = email.getSubject();
		this.message = email.getMessage();
		this.status = status;
		this.errorMessage = errorMessage;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
