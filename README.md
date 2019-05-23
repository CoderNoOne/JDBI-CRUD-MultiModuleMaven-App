# JDBI_APP
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

![Alt text](http://i.imgur.com/GLW9eYj.jpg "EER DIAGRAM")
***

### Prerequisities

* JDK 12 (switch expressions)
* A fully functional database (An open sourced **MySQL DB** was used originally)
***
### Build with

* [Maven](https://maven.apache.org/) - Dependency Management
***
### Main dependencies:
* [jdbi 3](http://jdbi.org/) - layer on top of JDBC, provides convenient access to relational DB and JAVA 8 STREAM API integration
* [j2html](https://j2html.com/examples.html) - used for html table structure generation
* [lombok](https://projectlombok.org/) - minimized boilerplate code
* [javaMail API](https://mvnrepository.com/artifact/javax.mail/mail/1.4.7) - Java API used to send and receive email via SMTP

### How to run it

* configure your database connection (database url etc.) - change appropriate properties in **connection.DbConnection** class
* change project sdk to 12 (Preview) with switch expressions enabled
