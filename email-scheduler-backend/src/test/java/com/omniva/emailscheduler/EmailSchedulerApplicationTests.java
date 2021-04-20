package com.omniva.emailscheduler;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

@TestConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailSchedularApplicationTests {

	@Bean
	public Scheduler getScheduler() throws SchedulerException {
		return StdSchedulerFactory.getDefaultScheduler();
	}

	@Bean
	public JavaMailSender mailSender() {
		final JavaMailSenderImpl sender = new MockMailSender();
		return sender;
	}

	private class MockMailSender extends JavaMailSenderImpl {
		@Override
		public void send(final MimeMessagePreparator mimeMessagePreparator) throws MailException {
			final MimeMessage mimeMessage = createMimeMessage();
			try {
				mimeMessagePreparator.prepare(mimeMessage);
				final String content = (String) mimeMessage.getContent();
				final Properties javaMailProperties = getJavaMailProperties();
				javaMailProperties.setProperty("mailContent", content);
			} catch (final Exception e) {
				throw new MailPreparationException(e);
			}
		}
	}

}
