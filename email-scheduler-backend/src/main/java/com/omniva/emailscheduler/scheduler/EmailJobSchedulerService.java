package com.omniva.emailscheduler.scheduler;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 
 * @author msoni The EmailJobSchedulerService is subclass of QuartzJobBean.
 *
 */
@Component
public class EmailJobSchedulerService extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(EmailJobSchedulerService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String userName;

	/**
	 * Execute the actual job. The job data map will already have been applied as
	 * bean property values by execute.
	 * 
	 */
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("subject");
		String body = jobDataMap.getString("body");
		String recipientEmail = jobDataMap.getString("email");

		sendMail(userName, recipientEmail, subject, body);
	}

	/**
	 * Send the actual email to user using mail-sender class send method
	 * @param fromEmail
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	private void sendMail(String fromEmail, String toEmail, String subject, String body) {
		try {
			logger.info("Sending Email to {}", toEmail);
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
			messageHelper.setFrom(fromEmail);
			messageHelper.setTo(toEmail);

			mailSender.send(message);
		} catch (MessagingException ex) {
			logger.error("Failed to send email to {}", toEmail);
		}
	}
}