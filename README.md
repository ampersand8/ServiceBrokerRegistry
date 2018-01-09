# ServiceBrokerRegistry
A simple registry for Service Brokers

Setup database with 
`docker run --name osbr -e MYSQL_DATABASE=osbr -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 -d mariadb`

Run server with
`mvn jetty:run -Djetty.http.port=3000`



## Setup Open Service Broker
```
git clone https://github.com/swisscom/open-service-broker.git
curl -u 'cf_ext:change_me' -XPOST -H 'Content-Type: application/json' -d "@servicedefinition.json" http://localhost:8080/custom/admin/service-definition
docker run --name appc-cf-service-broker-db -e MYSQL_DATABASE=CFBroker -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 33306:3306 -d mariadb
./gradlew bootRun -Dspring.config.location=file:broker/src/main/resources/

```


Example curl to get catalog from an Open Service Broker API
`curl -u 'cc_admin:change_me' http://localhost:8080/v2/catalog`
