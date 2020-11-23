package com.github.cyberpunkperson.widgetorganizer.controller;

import com.github.cyberpunkperson.widgetorganizer.annotation.Projection;
import com.github.cyberpunkperson.widgetorganizer.controller.dto.WidgetProjection;
import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/widgets")
public class WidgetController {

    private final WidgetService widgetService;

    private final ModelMapper modelMapper;


    /*
        @Projection could looks weird, just using my chance to experiment,
        could be replaced on @RequestBody and manual mapping
     */
    @PostMapping
    public ResponseEntity<WidgetProjection> createWidget(@Projection(WidgetProjection.class) @Valid Widget widget) {

        Widget createdWidget = widgetService.create(widget);
        return ResponseEntity
                .ok(modelMapper.map(createdWidget, WidgetProjection.class));
    }

    @PutMapping
    public ResponseEntity<WidgetProjection> updateWidget(@Projection(WidgetProjection.class) @Valid Widget widget) {

        Widget updatedWidget = widgetService.update(widget);
        return ResponseEntity
                .ok(modelMapper.map(updatedWidget, WidgetProjection.class));
    }

    @DeleteMapping("/{widgetId}")
    public void deleteWidget(@PathVariable UUID widgetId) {
        widgetService.deleteById(widgetId);
    }

    @GetMapping("/{widgetId}")
    public ResponseEntity<WidgetProjection> findWidget(@PathVariable UUID widgetId) {

        Widget foundWidget = widgetService.findById(widgetId);
        return ResponseEntity
                .ok(modelMapper.map(foundWidget, WidgetProjection.class));
    }

    @GetMapping
    public ResponseEntity<List<WidgetProjection>> findAllWidgets(@RequestParam Integer page,
                                                                 @RequestParam(defaultValue = "10") Integer size,
                                                                 @RequestParam(required = false) Integer width,
                                                                 @RequestParam(required = false) Integer height) {

        List<Widget> foundWidgets;
        if (nonNull(width) && nonNull(height)) {
            foundWidgets = widgetService.findAllByArea(PageRequest.of(page, size), width, height);
        } else {
            foundWidgets = widgetService.findAllSortedByIndexZ(PageRequest.of(page, size));
        }

        return ResponseEntity
                .ok(foundWidgets.stream()
                        .map(widget -> modelMapper.map(widget, WidgetProjection.class))
                        .collect(toList()));
    }
}
