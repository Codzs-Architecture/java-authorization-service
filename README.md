# Authorization Server Using Spring, Spring Security, OpenID, JWT, and MySQL

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) ![GitHub issues](https://img.shields.io/github/issues/khaitan-nitin/java-authorization-service)

Welcome to the Authorization Server Repository! 

This repository contains the source code for a authorization server written in Spring Boot and Java. This service provides support for OAuth 2.0 and OpenID Connect (OIDC) 1.0 specifications.
This service is designed to be used in conjunction with the [Resource Server](https://github.com/khaitan-nitin/java-resource-service.git) and [Config Server](https://github.com/khaitan-nitin/java-config-service.git) repositories.

## Table of Contents
* [Features](#features)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Setup](#setup)
* [Usage](#usage)
* [Supported Configuration Sources](#supported-configuration-sources)
    * [GitHub](#github)
    * [Database](#database)
    * [Redis](#redis)
* [Swagger](#swagger)
* [Configuration](#configuration)
* [Contributing](#contributing)
* [License](#license)

## Features
* OAuth 2.0 Authorization Code Grant
* OAuth 2.0 Client Credentials Grant
* OAuth 2.0 Refresh Token Grant
* OpenID Connect (OIDC) 1.0 Authorization Code Flow
* OpenID Connect (OIDC) 1.0 Client Credentials Flow
* OpenID Connect (OIDC) 1.0 Refresh Token Flow
* JSON Web Token (JWT) Support
* MySQL Database Support
* Spring Security Integration
* Spring Acl Integration

## Getting Started
Follow these instructions to get the Authorization Server up and running on your local development environment.

### Prerequisites
Make sure you have the following installed:

* Java Development Kit (JDK) 17 or later
* Maven
* Git
* MySql
* Redis
* Kafka

### Setup
1. Copy all the 4 repositories to your local machine in the same folder.
2. Clone the `sample config repository` to your local machine:
    ```
    git clone https://github.com/khaitan-nitin/java-codzs-config.git
    cd java-codzs-config
    ```
3.  Clone the `sample keys repository` to your local machine:
    ```
    git clone https://github.com/khaitan-nitin/java-codzs-resource.git
    cd java-codzs-resource
    ```
4.  Clone the `config server repository` to your local machine:
    ```
    git clone https://github.com/khaitan-nitin/java-config-service.git
    cd java-config-service
    ```
5.  Clone the `authorization server repository` to your local machine:
    ```
    git clone https://github.com/khaitan-nitin/java-authorization-service.git
    cd java-authorization-service
    ```
6.  Build the `authorization server project` using Maven:
    ```
    mvn clean install
    ```
7.  We should ensure that the `authorization server` is up and running before we start the `authorization server`. 
    To start the `config server`, follow the instructions in the [Config Server](https://github.com/khaitan-nitin/java-config-service) repository.
8.  Set up the `authorization server database` in mysql using the script below (change the values as per your environment):
    ```
    mvn clean flyway:migrate -Dflyway.user=root -Dflyway.password=Testing@123 -Dflyway.schemas=authorization -Dflyway.url=jdbc:mysql://127.0.0.1:3306/authorization
    ```
9.  Run the Authorization Server:
    ```
    java -jar target/java-authorization-service.jar 
    -Dspring.profiles.active=local 
    -Dconfig.server.url=https://local.codzs.com:5002
    -Dconfig.password=password 
    -Dauthorization.server.url=https://local.codzs.com:5003
    -Dserver.ssl.keystore=/path/to/java-codzs-resource/<environment>/local.codzs.com.p12 
    -Dserver.ssl.password=localpassword
    ```
    * `spring.profiles.active`: current active profile as per the environment
    * `config.server.url`: Config server base URL
    * `config.password`: Config server password
    * `authorization.server.url`: Admin server base URL
    * `server.ssl.keystore`: Path to the ssl key store file
    * `server.ssl.password`: SSL Key store password

    To configure IDE, do the below entry in the environment variable in the IDE (and change the values as per your environment):
    ```
    -Dspring.profiles.active=local
    
    config.server.url=https://local.codzs.com:5002; config.password=password; authorization.server.url=https://local.codzs.com:5003; server.ssl.keystore=/path/to/java-codzs-resource/<environment>/local.codzs.com.p12; server.ssl.password=localpassword
    ```

10.  Set up the Admin Server using the instructions in the Admin Server repository README. using the link: https://github.com/khaitan-nitin/java-admin-service.git

By default, the Config Server will run on port `5002`. Local url to access admin server is `https://local.localhost.com:5002`. Admin port is configured as `6002`.

## Usage
Below-mentioned are the steps to use the Authorization Server in your Spring Boot application. The steps are:

1. We have created a sample insert script `V0_0_6__oauth2_sample_data.sql` to insert records in `authorization server` database. Below-mentioned are the entries in the script with example:
   * We first create an entry in `oauth2_registered_client` table for the client application:
   ```
   INSERT INTO `oauth2_registered_client` VALUES (
                                               '1',
                                               'articles-client',
                                               '2023-07-05 09:41:41',
                                               '{bcrypt}$2a$10$iiU/FPsG3wzuau.mEdwFieEkwiOO7oHFXUzG8J8SfVLIX8DHWg1V6',
                                               NULL,
                                               'articles-client',
                                               'client_secret_basic',
                                               'refresh_token,client_credentials,authorization_code',
                                               'https://local.localhost.com:8001/authorized,https://local.localhost.com:8001/login/oauth2/code/articles-client-oidc',
                                               'https://local.localhost.com:8001/',
                                               'articles.read,openid',
                                               '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}',
                                               '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
                                            );
   ```
   The purpose of the above entry is to register the client application with the authorization server. For every new client, an entry needs to go in this table. We can refer [java-resouce-server]() and [java-resource-client-server]() for the same.
   * Then we do entry in `oauth2_authorization_consent` table for the client application:
   ```
   INSERT INTO `oauth2_authorization_consent` VALUES ('1','admin','ROLE_ADMIN');
   ```
   The purpose of the above entry is to give consent to the client application for the user. For every new client, an entry needs to go in this table. We can refer [java-resouce-server]() and [java-resource-client-server]() for the same.
   * Then we do entry in `user` table for the user and `authorities` table for the user's role:
   ```
   INSERT INTO `users` VALUES ('admin','{bcrypt}$2a$10$63CNAw69rPATq7hoyGbQFe4GojJ.xK9bNWs1rH88U0GPiHyK2ueIC',1);
   INSERT INTO `authorities` VALUES ('admin','ADMIN');
   ```
2. We then add the authorization domain issuer in `\etc\hosts` file as below for local:
   ```
   local.localhost.com 127.0.0.1
   ```

## Swagger
Swagger is a set of open-source tools built around the OpenAPI Specification that can help you design, build, document and consume REST APIs. The url for the swagger is: https://local.localhost.com:6003/management/swagger-ui/index.html

## Configuration
The Config Server can be customized to suit various requirements. You can modify the following files and settings:

* `application.yml`: Basic Authorization server settings.
* `application-config-service.yml`: Config service connection details, to pull authorization server configuration details on startup.
* `application-keystore.yml`: Configuration for SSL settings.

For more advanced customization and features, refer to the Spring Cloud Config documentation.

## Contributing
We welcome contributions from the community! If you want to contribute to this project, please follow these steps:

* Fork the repository.
* Create a new branch for your changes.
* Make your changes and commit them with descriptive commit messages.
* Push your changes to your forked repository.
* Create a pull request, detailing the changes you made.

We appreciate your contributions, whether it's bug fixes, new features, or documentation improvements.

## License
This project is licensed under the MIT License - see the LICENSE file for details.


Environment Variables:
