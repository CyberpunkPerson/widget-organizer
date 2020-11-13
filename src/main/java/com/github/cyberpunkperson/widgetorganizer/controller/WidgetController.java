package com.github.cyberpunkperson.widgetorganizer.controller;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/widget")
public class WidgetController {

    private final WidgetService widgetService;


    @PostMapping("/create")
    public ResponseEntity<Widget> createWidget(@RequestBody @Valid Widget widget) {

        return ResponseEntity
                .ok(widgetService.create(widget));
    }

    @PutMapping("/update")
    public ResponseEntity<Widget> updateWidget(@RequestBody @Valid Widget widget) {

        return ResponseEntity
                .ok(widgetService.update(widget));
    }

    @DeleteMapping("/delete/{widgetId}")
    public void deleteWidget(@PathVariable UUID widgetId) {
        widgetService.deleteById(widgetId);
    }

    @GetMapping("/find/{widgetId}")
    public ResponseEntity<Widget> findWidget(@PathVariable UUID widgetId) {

        return ResponseEntity
                .ok(widgetService.findById(widgetId));
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Widget>> findAllWidgets() {

        return ResponseEntity
                .ok(widgetService.findAllSorted());
    }

    @GetMapping("/find-all/pageable")
    public ResponseEntity<List<Widget>> findAllWidgetsPageable(@RequestParam("page") Integer page,
                                                               @RequestParam("size") Integer size) {

        return ResponseEntity
                .ok(widgetService.findAllSortedPageable(PageRequest.of(page, size)));
    }

    @GetMapping("/find-all/by/area")
    public ResponseEntity<List<Widget>> findAllWidgetsByArea(@RequestParam("width") Integer width,
                                                               @RequestParam("height") Integer height) {

        return ResponseEntity
                .ok(widgetService.findAllByArea(width, height));
    }

}
