package com.omniva.emailscheduler.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(EmailJobSchedulerController.class)
@RunWith(SpringRunner.class)
public class EmailJobSchedulerControllerTest {

	@Mock
	RestTemplate restTemplate;

	@Autowired
	private MockMvc mvc;

	@Mock
	SchedulerFactoryBean schedulerFactoryBean;

	@Before
	public void before() {
		when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
	}

	@Mock
	Scheduler scheduler;

	@Test
	public void SchedulePastEmail_throwExpection() throws Exception {

		RequestBuilder request = MockMvcRequestBuilders.post("/schedule-email/").accept(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"mehul.soni89@gmail.com\",\n" + " \"subject\":\"testing\",\n"
						+ " \"body\":\"testing body\",\n" + " \"timeZone\":\"Europe/Helsinki\",\n"
						+ " \"dateTime\":\"2021-04-17T03:27:06\"\n" + "}")
				.contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().is4xxClientError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("dateTime must be after current time"));

	}

	@Test
	public void ScheduleFutureEmail_ReturnSuccessResponse() throws Exception {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime localDate = LocalDateTime.now();
		localDate.plusHours(1);
		System.out.println(dtf.format(localDate));

		RequestBuilder request = MockMvcRequestBuilders.post("/schedule-email/").accept(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"mehul.soni89@gmail.com\",\n" + " \"subject\":\"testing\",\n"
						+ " \"body\":\"testing body\",\n" + " \"timeZone\":\"Europe/Helsinki\",\n"
						+ " \"dateTime\":\"2021-04-22T03:27:06\"\n" + "}")
				.contentType(MediaType.APPLICATION_JSON);

		when(scheduler.scheduleJob((JobDetail) any(), (Trigger) any())).thenReturn(new Date());
		mvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(status().is(200))
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email Scheduled Successfully!"));

	}
}
