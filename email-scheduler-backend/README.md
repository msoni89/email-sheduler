## Email Sheduler Backend Service


Used Technologies

```
Java 8, Maven, Web, JPA, MySQL, Quartz, and Mail

```

Update Requred Properties

```
spring.datasource.url = jdbc:mysql://localhost:3306/email_scheduler?useSSL=false
spring.datasource.username=root
spring.datasource.password=StrongPassword@123

## QuartzProperties
spring.quartz.job-store-type=jdbc
spring.quartz.properties.org.quartz.threadPool.threadCount=5

## MailProperties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@address.com
spring.mail.password=you-personal-password

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

```

Creating Quartz Tables

```bash
mysql> source resources/email_quartz_tables.sql 
```

### Running the Application and Testing the API
Open your terminal, go to the root directory of the project and type the following command to run it -

important cmd
```
mvn clean compile
mvn install build
mnv clean compile -DskipTests=true
```
```
mvn spring-boot:run -Dspring.mail.password=<YOUR_SMTP_PASSWORD>
```

The application will start on port 8080 by default. Let’s now schedule an email using the /schedule-email API -


### Request Payload Email Request
```json

{"email":"mehul.soni89@gmail.com",
 "subject":"testing",
 "body":"testing body",
 "timeZone":"Europe/Helsinki",
 "dateTime":"2021-04-21T03:27:06"
}
```

### Success Response 200
```json

{
 "success":true,
 "jobId":<uuid>,
 "message":"Email Scheduled Successfully!",
 "jobGroup":<job-group>,
}
```


### Failed Response 404
```json

{ 
 "success":false,
 "message":"dateTime must be after current time",
}
```

### Register App Password
An app password is a 16-digit passcode that gives a non-Google app or device permission to access your Google Account. App passwords can only be used with accounts that have 2-Step Verification turned on.

```
https://support.google.com/mail/answer/185833?hl=en-GB
```
