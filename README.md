# ServiceBrokerRegistry
A simple registry for Service Brokers

Setup database with 
`docker run --name osbr -e MYSQL_DATABASE=osbr -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 -d mariadb`

Run server with
`mvn jetty:run`


Example curl to get catalog from an Open Service Broker API
`curl -u 'cc_admin:change_me' http://localhost:8080/v2/catalog`