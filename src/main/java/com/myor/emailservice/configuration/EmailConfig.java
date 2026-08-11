package com.myor.emailservice.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
	@Autowired
    private Environment env;

    @Bean(name = "primaryMailSender")
    @Primary
    public JavaMailSender primaryMailSender() {
        return buildMailSender(
                env.getProperty("spring.mail.host"),
                env.getProperty("spring.mail.port"),
                env.getProperty("spring.mail.username"),
                env.getProperty("spring.mail.password"));
    }

    @Bean(name = "fallbackMailSender")
    public JavaMailSender fallbackMailSender() {
        return buildMailSender(
                env.getProperty("spring.mail.fallback.host", "smtp.gmail.com"),
                env.getProperty("spring.mail.fallback.port", "465"),
                env.getProperty("spring.mail.fallback.username"),
                env.getProperty("spring.mail.fallback.password"));
    }

    private JavaMailSender buildMailSender(String host, String port, String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(Integer.valueOf(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.smtp.ssl.trust", host);

        javaMailProperties.put("mail.smtp.socketFactory.port", port);
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.smtp.socketFactory.fallback", "false");

        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }
}
