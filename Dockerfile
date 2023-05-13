FROM openjdk:20
COPY target/product-monitoring-0.0.1-SNAPSHOT.jar product-monitoring-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/product-monitoring-0.0.1-SNAPSHOT.jar"]