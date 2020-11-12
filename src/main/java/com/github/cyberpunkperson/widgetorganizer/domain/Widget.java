package com.github.cyberpunkperson.widgetorganizer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "widget")
public class Widget extends Auditor {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @NotNull(message = "{widget.coordinate-x.not-null}")
    private Integer coordinateX;

    @NotNull(message = "{widget.coordinate-y.not-null}")
    private Integer coordinateY;

    @Column(unique = true)
    @NotNull(message = "{widget.index-z.not-null}")
    private Integer indexZ;

    @NotNull(message = "{widget.width.not-null}")
    @Positive(message = "{widget.width.positive}")
    private Integer width;

    @NotNull(message = "{widget.height.not-null}")
    @Positive(message = "{widget.height.positive}")
    private Integer height;

    @Version
    @JsonIgnore
    private Long version;

}
