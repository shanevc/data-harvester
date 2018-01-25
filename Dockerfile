FROM openjdk:8-jre-alpine

EXPOSE 8080

# Create application dir
WORKDIR /opt/spring-boot/data-harvester

# Copy cert to application directory
COPY icobench.com.cer .

# Add cert to Java keystore
RUN keytool -import -noprompt -storepass changeit -alias icobench -keystore /etc/ssl/certs/java/cacerts -file icobench.com.cer

# Copy jar file to application directory
COPY target/data-harvester-0.1.0.jar .

# Run application
CMD ["java", "-jar", "data-harvester-0.1.0.jar"]