package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;


    @Transactional
    public Widget create(Widget newWidget) {

        List<Widget> existWidgets = findAll();
        mergeWidgets(existWidgets, newWidget);

        widgetRepository.saveAll(existWidgets);
        widgetRepository.flush();
        return widgetRepository.save(newWidget);
    }

    @Transactional
    public Widget update(Widget newWidget) {

        notNull(newWidget.getId(), "Widget id should to be specified for update");

        List<Widget> existWidgets = findAll();
        existWidgets.stream()
                .filter(widget -> widget.getId().equals(newWidget.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Widget with id:'%s' does not exist", newWidget.getId())));

        mergeWidgets(existWidgets, newWidget);

        widgetRepository.saveAll(existWidgets);
        widgetRepository.flush();
        return widgetRepository.save(newWidget);
    }

    public void deleteById(UUID widgetId) {
        widgetRepository.deleteById(widgetId);
    }

    public Widget findById(UUID widgetId) {
        return widgetRepository.findById(widgetId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Widget with id:'%s' was not found", widgetId)));
    }

    public List<Widget> findAll() {
        return widgetRepository.findAll();
    }

    public List<Widget> findAllSorted() {
        return widgetRepository.findByOrderByIndexZ();
    }

    private List<Widget> mergeWidgets(List<Widget> widgets, Widget newWidget) {

        if (isEmpty(widgets)) {
            if (isNull(newWidget.getIndexZ())) {
                newWidget.setIndexZ(0);
            }

            return new ArrayList<>() {{
                addAll(widgets);
                add(newWidget);
            }};
        }

        if (isNull(newWidget.getIndexZ())) {
            widgets.stream()
                    .mapToInt(Widget::getIndexZ)
                    .max()
                    .ifPresent(maxZIndex -> newWidget.setIndexZ(maxZIndex + 1));

            return new ArrayList<>() {{
                addAll(widgets);
                add(newWidget);
            }};
        }

        HashMap<Integer, Widget> widgetsMap = widgets.stream()
                .collect(toMap(Widget::getIndexZ, widget -> widget, (prev, next) -> next, HashMap::new));

        return new ArrayList<>(insertWidgetWithShift(widgetsMap, newWidget).values());
    }

    private static HashMap<Integer, Widget> insertWidgetWithShift(HashMap<Integer, Widget> widgetsMap, Widget newWidget) {

        Optional.ofNullable(widgetsMap.get(newWidget.getIndexZ()))
                .ifPresentOrElse(widget -> {
                            widgetsMap.remove(widget.getIndexZ());
                            widgetsMap.put(newWidget.getIndexZ(), newWidget);
                            widget.setIndexZ(widget.getIndexZ() + 1);
                            insertWidgetWithShift(widgetsMap, widget);
                        },
                        () -> widgetsMap.put(newWidget.getIndexZ(), newWidget));

        return widgetsMap;
    }

}
