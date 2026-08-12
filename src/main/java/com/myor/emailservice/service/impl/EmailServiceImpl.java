package com.myor.emailservice.service.impl;

import java.util.Arrays;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.myor.emailservice.entity.Email;
import com.myor.emailservice.entity.EmailLog;
import com.myor.emailservice.repository.EmailLogRepository;
import com.myor.emailservice.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	@Qualifier("primaryMailSender")
	JavaMailSender mailSender;

	@Autowired
	@Qualifier("fallbackMailSender")
	JavaMailSender fallbackMailSender;

	@Autowired
	EmailLogRepository emailLogRepository;

	@Value("${spring.mail.fallback.username:}")
	private String fallbackUsername;

	@Value("${email.throttle.delay-ms:5000}")
	private long throttleDelayMs;

	@Async("emailTaskExecutor")
	@Override
	public void sendEmail(Email email) {
		try {
			String status;
			String errorMessage = null;
			try {
				doSend(mailSender, email);
				status = EmailLog.STATUS_SENT_PRIMARY;
				logger.info("Email sent to: {} (primary account)", email.getTo());
			} catch (Exception primaryError) {
				if (fallbackUsername == null || fallbackUsername.trim().isEmpty()) {
					status = EmailLog.STATUS_FAILED;
					errorMessage = "Primary: " + primaryError.getMessage() + " (no fallback account configured)";
					logger.error("Failed to send email to: {} and no fallback account is configured. Error: {}",
							email.getTo(), primaryError.getMessage(), primaryError);
				} else {
					logger.warn(
							"Primary account failed to send email to: {}. Retrying with fallback account. Error: {}",
							email.getTo(), primaryError.getMessage());
					try {
						doSend(fallbackMailSender, email);
						status = EmailLog.STATUS_SENT_FALLBACK;
						errorMessage = "Primary: " + primaryError.getMessage();
						logger.info("Email sent to: {} (fallback account)", email.getTo());
					} catch (Exception fallbackError) {
						status = EmailLog.STATUS_FAILED;
						errorMessage = "Primary: " + primaryError.getMessage()
								+ " | Fallback: " + fallbackError.getMessage();
						logger.error("Failed to send email to: {} with both primary and fallback accounts. Error: {}",
								email.getTo(), fallbackError.getMessage(), fallbackError);
					}
				}
			}
			saveLog(email, status, errorMessage);
		} finally {
			throttle();
		}
	}

	private void doSend(JavaMailSender sender, Email email) throws Exception {
		MimeMessage mimeMessage = sender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
		mimeMessage.setContent(email.getMessage(), "text/html");
		mimeMessageHelper.setSubject(email.getSubject());
		mimeMessageHelper.setFrom(new InternetAddress(email.getFrom(), "SnOPiX Reminder System"));
		if (email.getCc() != null
				&& !email.getCc().trim().isEmpty()) {

			mimeMessageHelper.setCc(
					InternetAddress.parse(
							normalizeAddresses(
									email.getCc())));
		}

		if (email.getBcc() != null
				&& !email.getBcc().trim().isEmpty()) {

			mimeMessageHelper.setBcc(
					InternetAddress.parse(
							normalizeAddresses(
									email.getBcc())));
		}
		mimeMessageHelper.setTo(InternetAddress.parse(email.getTo()));
		mimeMessageHelper.setText(email.getMessage());

		logger.info(
        "[EMAIL SERVICE] TO={} CC={} BCC={} SUBJECT={}",
        Arrays.toString(
                mimeMessage.getRecipients(
                        Message.RecipientType.TO)),
        Arrays.toString(
                mimeMessage.getRecipients(
                        Message.RecipientType.CC)),
        Arrays.toString(
                mimeMessage.getRecipients(
                        Message.RecipientType.BCC)),
        email.getSubject()
);

sender.send(mimeMessageHelper.getMimeMessage());
				
		sender.send(mimeMessageHelper.getMimeMessage());
	}

	private void saveLog(Email email, String status, String errorMessage) {
		try {
			emailLogRepository.save(new EmailLog(email, status, errorMessage));
		} catch (Exception e) {
			// a database problem must never break email sending, so only log it
			logger.error("Failed to save email log for: {}. Error: {}", email.getTo(), e.getMessage(), e);
		}
	}

	private String normalizeAddresses(
			String addresses) {

		if (addresses == null) {
			return "";
		}

		return addresses.replace(';', ',');
	}

	private void throttle() {
		if (throttleDelayMs <= 0) {
			return;
		}
		try {
			Thread.sleep(throttleDelayMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
