package com.myor.emailservice.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myor.emailservice.entity.Email;
import com.myor.emailservice.service.EmailService;

@RestController
@RequestMapping(path = "/email-service")
public class EmailRestController {
	@Autowired
	EmailService emailService;
	
	@PostMapping(path="", consumes="application/json", produces="application/json")
	public void SendEmail(@RequestBody Email email){
		emailService.sendEmail(email);
	}

}
