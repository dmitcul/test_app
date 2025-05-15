package org.example.tech_spec_java_spring_final_v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechSpecJavaSpringFinalV2Application {

    private static final Logger logger = LoggerFactory.getLogger(TechSpecJavaSpringFinalV2Application.class);

    public static void main(String[] args) {
        logger.info("Starting TechSpecJavaSpringFinalV2Application");
        SpringApplication.run(TechSpecJavaSpringFinalV2Application.class, args);
        logger.info("TechSpecJavaSpringFinalV2Application started successfully");
    }

}
