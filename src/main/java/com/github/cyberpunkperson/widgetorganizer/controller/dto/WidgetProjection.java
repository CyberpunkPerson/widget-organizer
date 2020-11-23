package com.github.cyberpunkperson.widgetorganizer.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@NoArgsConstructor
public class WidgetProjection {

    UUID id;

    @NotNull(message = "{widget.coordinate-x.not-null}")
    Integer coordinateX;

    @NotNull(message = "{widget.coordinate-y.not-null}")
    Integer coordinateY;

    Integer indexZ;

    @NotNull(message = "{widget.width.not-null}")
    @Positive(message = "{widget.width.positive}")
    Integer width;

    @NotNull(message = "{widget.height.not-null}")
    @Positive(message = "{widget.height.positive}")
    Integer height;

    @DateTimeFormat(iso = DATE_TIME)
    ZonedDateTime lastModifiedDate;
}
