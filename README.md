# JDBI CRUD Application with some other functionalities
---
### 1. About

This is an application that allows you to simulate ticket sales of a cinema. The sales mechanism is considered. 
The movie table represents a movie in which you, the user, will be able to purchase a ticket.
The customer table represents the actual customer who wishes to buy a movie ticket. In addition, the table has a one-to-one relation with the loyalty_card table, which represents the loyalty card was assigned to the client. The application
initially doesn't have knowledge of the association between the loyalty card and the customer,
The loyal_card table has columns describing the date in which the card was activated(after this date the card is in active), The card holder is entitled to a discount. The card may expire under two circumstances : When the cards
expiration date is reached or when the the card holder reaches the limit on the number of films they are allowed a 
discount on. The sales_stand table represents the movie sales stand, this is an intermediate table of the many-to-many
relationship between movie and customer tables.

This application also provides several additional functionalities, such as filtering results, sorting, history, sending e-mails and some various statistics on different tables.

![Alt text](http://i.imgur.com/GLW9eYj.jpg "EER DIAGRAM")
***

### 2. Prerequisities

* JDK 12 (switch expressions)
* A fully functional database (An open sourced **MySQL DB** was used originally)
***
### 3. Build with

* [Maven](https://maven.apache.org/) - Dependency Management
***
### 4. Main dependencies:
* [jdbi 3](http://jdbi.org/) - layer on top of JDBC, provides convenient access to relational DB and JAVA 8 STREAM API integration
* [j2html](https://j2html.com/examples.html) - used for html table structure generation
* [lombok](https://projectlombok.org/) - minimized boilerplate code, used also to generate a logger field
* [javaMail API](https://mvnrepository.com/artifact/javax.mail/mail/1.4.7) - Java API used to send and receive email via SMTP
* [gson](https://github.com/google/gson/blob/master/UserGuide.md) - 
Java-based library to serialize Java objects to JSON and vice versa
* [Junit 5](https://junit.org/junit5/docs/current/user-guide/) - one of the most popular unit-testing frameworks in the Java ecosystem
* [Mockito 3](https://site.mockito.org/) -  JAVA-based library that is used to mock interfaces so that a dummy functionality can be added to a mock interface that can be used in unit testing
* [Hamcrest](http://hamcrest.org/JavaHamcrest/) - a framework for writing matcher objects allowing 'match' rules to be defined declaratively
### 5. How to run it

* configure your database connection (database url etc.) - change appropriate properties in **connection.DbConnection** class
* change project sdk to 12 (Preview) with switch expressions enabled
* provide email address and corresponding password to your gmail account in utils.other.EmailUtils class - the email will be used to send the email messages to the recipients

## Docker version

* clone docker branch from repository into local machine with command:
```
git clone -b docker https://github.com/CoderNoOne/JDBI-CRUD-MultiModuleMaven-App.git <local git repository name>

```
* open a terminal and run a command (with default values for mysql container environemnt variables):

```docker
docker-compose run jdbi-application
```
The default values can be found in docker-compose.yml file:

user | password | database | db_host_port | mysql container name
--- | --- | --- | --- | ---
user | pass | db | 2000 | mysql_service

You can specify custom values with a command:

```
<env_variable>=<custom_value> docker-compose run jdbi-application
```

For instance:

```
user=customUser password=customPassword docker-compose run hibernate-console-app
```

To log in with specified mysql user credentials you can:

1. Log via exposed port on localhost using a command:

```
mysql -u user -p -P 2000 -h127.0.0.1
```
2. Log into bash of running mysql container with interactive mode using command:

```
docker container exec -ti mysql_service bash
```

Then you can log into mysql with:

```
mysql -u user -ppassword

```
3. Use mysql workbench and add a new mysql connection:
![Alt text](http://i.imgur.com/xb1VlWb.png "MYSQL WORKBENCH")

