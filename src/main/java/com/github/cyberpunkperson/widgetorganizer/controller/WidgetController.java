package com.github.cyberpunkperson.widgetorganizer.controller;

import com.github.cyberpunkperson.widgetorganizer.annotation.Projection;
import com.github.cyberpunkperson.widgetorganizer.controller.dto.WidgetProjection;
import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
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
    public WidgetProjection createWidget(@Projection(WidgetProjection.class) @Valid Widget widget) {

        Widget createdWidget = widgetService.create(widget);
        return modelMapper.map(createdWidget, WidgetProjection.class);
    }

    @PutMapping
    public WidgetProjection updateWidget(@RequestBody @Valid WidgetProjection widgetProjection) {

        return Optional.of(widgetProjection)
                .map(this::convertProjectionToWidget)
                .map(widgetService::update)
                .map(this::convertWidgetToProjection)
                .orElseThrow(() -> new RuntimeException("Failed to update widget"));
    }

    @DeleteMapping("/{widgetId}")
    public void deleteWidget(@PathVariable UUID widgetId) {
        widgetService.deleteById(widgetId);
    }

    @GetMapping("/{widgetId}")
    public WidgetProjection findWidget(@PathVariable UUID widgetId) {

        Widget foundWidget = widgetService.findById(widgetId);
        return modelMapper.map(foundWidget, WidgetProjection.class);
    }

    @GetMapping
    public List<WidgetProjection> findAllWidgets(@RequestParam Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) Integer width,
                                                 @RequestParam(required = false) Integer height) {

        List<Widget> foundWidgets;
        if (nonNull(width) && nonNull(height)) {
            foundWidgets = widgetService.findAllByArea(PageRequest.of(page, size), width, height);
        } else {
            foundWidgets = widgetService.findAllSortedByIndexZ(PageRequest.of(page, size));
        }

        return foundWidgets.stream()
                .map(this::convertWidgetToProjection)
                .collect(toList());
    }

    private Widget convertProjectionToWidget(WidgetProjection widgetProjection) {
        return modelMapper.map(widgetProjection, Widget.class);
    }

    private WidgetProjection convertWidgetToProjection(Widget widget) {
        return modelMapper.map(widget, WidgetProjection.class);
    }
}
