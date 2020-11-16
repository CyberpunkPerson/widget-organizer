package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetPageableRepository;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;

    private final WidgetPageableRepository widgetPageableRepository;


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

    public List<Widget> findAllSortedPageable(Pageable pageable) {
        return widgetPageableRepository.findByOrderByIndexZ(pageable);
    }

    public List<Widget> findAllByArea(Integer width, Integer height) {

        List<Widget> existWidgets = widgetRepository.findAllSortedByHeightAndWidth();
        return binarySearchOfEdgeIndex(existWidgets, 0, existWidgets.size() - 1, width, height)
                .map(edgeWidgetIndex -> existWidgets.subList(0, edgeWidgetIndex + 1))
                .orElse(emptyList());
    }

    private Optional<Integer> binarySearchOfEdgeIndex(List<Widget> widgets, int start, int end, int width, int height) {

        if (end >= start) {
            int middle = start + (end - start) / 2;

            if (isEdgeCoordinate(widgets, middle, width, height))
                return Optional.of(middle);

            if (isCoordinateMoreThanValue(widgets, middle, width, height))
                return binarySearchOfEdgeIndex(widgets, start, middle - 1, width, height);

            return binarySearchOfEdgeIndex(widgets, middle + 1, end, width, height);
        }

        return Optional.empty();
    }

    private boolean isEdgeCoordinate(List<Widget> widgets, int middle, int width, int height) {

        Widget checkWidget = widgets.get(middle);
        boolean result = checkWidget.getMaxWidthCoordinate() <= width && checkWidget.getMaxHeightCoordinate() <= height;

        if (middle < widgets.size() - 1) {
            Widget nextWidget = widgets.get(middle + 1);
            return result && (nextWidget.getMaxWidthCoordinate() > width || nextWidget.getMaxHeightCoordinate() > height);
        }

        return result;
    }

    private boolean isCoordinateMoreThanValue(List<Widget> widgets, Integer middle, int width, int height) {
        Widget widget = widgets.get(middle);
        return widget.getMaxWidthCoordinate() > width && widget.getMaxHeightCoordinate() > height;
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

    /*
     * Also can be done via iterator, I decided to try this approach
     */
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
