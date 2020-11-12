package com.github.cyberpunkperson.widgetorganizer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditor {

    @CreatedDate
    @DateTimeFormat(iso = DATE_TIME)
    protected ZonedDateTime createdDate;

    @LastModifiedDate
    @DateTimeFormat(iso = DATE_TIME)
    protected ZonedDateTime lastModifiedDate;

}
