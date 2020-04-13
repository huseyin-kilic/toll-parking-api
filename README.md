# Toll Parking API Application

This is a sample project to demonstrate a toll parking API, powered by Spring Boot, Open API, Apache Kafka and JBoss Drools.

API documentation is available in the [github pages](https://huseyin-kilic.github.io/toll-parking-api/).

[![Build Status](https://travis-ci.com/huseyin-kilic/toll-parking-api.svg?branch=master)](https://travis-ci.com/huseyin-kilic/toll-parking-api)

## Prerequisites 

1. [JDK/JRE 11](https://maven.apache.org/install.html)
2. [Apache maven (version > 3.6)](https://kafka.apache.org/quickstart)
3. [Apache Kafka (version > 2.4) & Apache Zookeper (version > 3.6)](https://kafka.apache.org/quickstart)

## Steps to build & start the project

1. Start Zookeeper and Kafka servers as described in the [installation guide](https://kafka.apache.org/quickstart).

2. Clone the repository by `git clone https://github.com/huseyin-kilic/toll-parking-api.git`.

3. Build the project with maven with the command `mvn clean package`.
    
4. Start the application `java -jar -DtypeGasolineCount=10 -DtypeKW20Count=10 -DtypeKW50Count=10 target/api-0.0.1-SNAPSHOT.jar`.
Notice the 3 command line arguments `typeGasolineCount`, `typeKW20Count`, and `typeKW50Count`, representing the number of parking spaces to create for respective car types.

## Steps to access to the API in local environment

1. Once the application starts, you can browse to the Swagger documentation at `http://localhost:8191/swagger-ui.html`.
 You can this Swagger UI to explore the API, see examples and inject messages.

2. API definition file is available in [YAML format](https://github.com/huseyin-kilic/toll-parking-api/blob/master/swagger.yaml).

## Steps to implement API client 

1. Go to [Swagger Editor](https://editor.swagger.io/). 

2. Copy & paste the [API definition file](https://github.com/huseyin-kilic/toll-parking-api/blob/master/swagger.yaml) to the editor on the left panel.

3. Click on the <i>Generate Client</i> option at the top of the page and choose your favorite programming language. 
The client code will be generated automatically and will be downloaded as a compressed file.

  