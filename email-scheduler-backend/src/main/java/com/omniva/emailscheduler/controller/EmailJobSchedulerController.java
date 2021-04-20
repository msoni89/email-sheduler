package com.omniva.emailscheduler.controller;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.omniva.emailscheduler.payload.EmailRequest;
import com.omniva.emailscheduler.payload.EmailResponse;
import com.omniva.emailscheduler.scheduler.EmailJobSchedulerService;

@RestController
public class EmailJobSchedulerController {
	private static final Logger logger = LoggerFactory.getLogger(EmailJobSchedulerController.class);

	@Autowired
	private Scheduler scheduler;

	/**
	 * @param emailRequest
	 * @return
	 */
	@PostMapping("/schedule-email")
	public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
		try {
			ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
			if (dateTime.isBefore(ZonedDateTime.now())) {
				EmailResponse emailResponse = new EmailResponse(false, "dateTime must be after current time");
				return ResponseEntity.badRequest().body(emailResponse);
			}

			JobDetail jobDetail = buildJobDetail(emailRequest);
			Trigger trigger = buildJobTrigger(jobDetail, dateTime);
			scheduler.scheduleJob(jobDetail, trigger);

			EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(),
					jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
			return ResponseEntity.ok(emailResponse);
		} catch (SchedulerException ex) {
			logger.error("Error scheduling email", ex);

			EmailResponse EmailResponse = new EmailResponse(false, "Error scheduling email. Please try later!");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(EmailResponse);
		}
	}

	private JobDetail buildJobDetail(EmailRequest emailRequest) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", emailRequest.getEmail());
		jobDataMap.put("subject", emailRequest.getSubject());
		jobDataMap.put("body", emailRequest.getBody());
		jobDataMap.put("time-zone", emailRequest.getTimeZone());
		jobDataMap.put("date-time", emailRequest.getDateTime());

		return JobBuilder.newJob(EmailJobSchedulerService.class)
				.withIdentity(UUID.randomUUID().toString(), "email-jobs").withDescription("Send Email Job")
				.usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "email-triggers").withDescription("Send Email Trigger")
				.startAt(Date.from(startAt.toInstant()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
	}
}