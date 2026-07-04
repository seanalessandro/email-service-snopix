package com.myor.emailservice.service.impl;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.myor.emailservice.entity.Email;
import com.myor.emailservice.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	JavaMailSender mailSender;
	
	@Override
	public void sendEmail(Email email) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessage.setContent(email.getMessage(), "text/html");
			mimeMessageHelper.setSubject(email.getSubject());
			mimeMessageHelper.setFrom(new InternetAddress(email.getFrom(), "SnOPiX Reminder System"));
			mimeMessageHelper.setTo(InternetAddress.parse(email.getTo()));
			mimeMessageHelper.setText(email.getMessage());
			mailSender.send(mimeMessageHelper.getMimeMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		
	}
	
}
