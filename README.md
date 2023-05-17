Ecommerce Demo Microservice Project
This project is a microservice-based application designed for basic understanding of Microsercice with apache kafka and how to use ELK. elastic search logstash kibana.

Features
ecom-config-server: it is use to get all the configuration from the centralize repository and will pass the configuration to all other services.

ecom-gateway-service: API Gateway is responsible for all the request coming from the client will redirect to the particular services.

ecom-service-resgistry: The Service Registry is a vital component in a microservices architecture that provides a centralized registry for service discovery. It enables services to dynamically locate and communicate with each other without hard-coded dependencies or knowledge of specific IP addresses or ports.

order-service: order service is used to create order.

payment-service: Payment service is used to manage the payment related stuff.

user-service: User service is used to manage the users.

Technologies Used
Language: java spring boot isused in the project.

Framework: STS is used for building the microservices.

Database: My-SQl Database is used.

Messaging: Kafka Messaging broker is used to get order placed messages.

ELK : Elastic search, Logstash and kibana is used to, store, processing and visualize the logs data.

Download ElasticSearch: https://www.elastic.co/downloads/elasticsearch Download Logstash: https://www.elastic.co/downloads/logstash Download Kibana: https://www.elastic.co/downloads/kibana

ELK (Elasticsearch, Logstash, Kibana)

Elasticsearch: Elasticsearch is a NOSQL database that is based on the Lucene search engine which will help us to store inputs/logs.

Logstash: is a log pipeline tool that accepts inputs/logs from various sources & exports the data to various targets.

Kibana: Kibana is a visualization UI layer, which will help the developer to monitor application logs.

Installation
