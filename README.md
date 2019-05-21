# JDBI_APP
---
### 1. About

![Alt text](http://i.imgur.com/GLW9eYj.jpg "EER DIAGRAM")
***

### Prerequisities

* JDK 12 (switch expressions)
* An actual running database (An open sourced **MySQL DB** was used originally)
***
### Build with

* [Maven](https://maven.apache.org/) - Dependency Management
***
### Main dependencies:
* [jdbi 3](http://jdbi.org/) - layer on top of JDBC, provides quite convenient access to relational DB and JAVA 8 STREAM API integration
* [j2html](https://j2html.com/examples.html) - used for html table structure generation
* [lombok](https://projectlombok.org/) - minimized boilerplate code
* [javaMail API](https://mvnrepository.com/artifact/javax.mail/mail/1.4.7) - Java API used to send and receive email via SMTP

### How to run it

* configure your database connection (database url etc.) - change appropriate properties in **connection.DbConnection** class
* change project sdk to 12 (Preview) with switch expressions enabled
