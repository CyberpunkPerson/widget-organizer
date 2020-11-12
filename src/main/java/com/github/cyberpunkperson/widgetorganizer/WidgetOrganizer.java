package com.github.cyberpunkperson.widgetorganizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class WidgetOrganizer {

    public static void main(String[] args) {
        SpringApplication.run(WidgetOrganizer.class, args);
    }

}
