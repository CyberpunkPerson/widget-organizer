package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;


    @Override
    @Transactional
    public Widget create(Widget newWidget) {

        List<Widget> existWidgets = findAll();
        List<Widget> mergedWidgets = mergeWidgets(existWidgets, newWidget);

        calculateMaxCoordinates(newWidget);

        widgetRepository.saveWidgets(mergedWidgets);
        return newWidget;
    }

    @Override
    @Transactional
    public Widget update(Widget newWidget) {

        notNull(newWidget.getId(), "Widget id should to be specified for update");

        List<Widget> existWidgets = findAll();
        existWidgets.stream()
                .filter(widget -> widget.getId().equals(newWidget.getId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(String.format("Widget with id:'%s' does not exist", newWidget.getId())));

        calculateMaxCoordinates(newWidget);
        List<Widget> mergedWidgets = mergeWidgets(existWidgets, newWidget);

        widgetRepository.saveWidgets(mergedWidgets);
        return newWidget;
    }

    private void calculateMaxCoordinates(Widget widget) {
        Float maxCoordinateX = (widget.getCoordinateX() + (float) widget.getWidth() / 2);
        Float maxCoordinateY = (widget.getCoordinateY() + (float) widget.getHeight() / 2);

        widget.setMaxCoordinateX(maxCoordinateX);
        widget.setMaxCoordinateY(maxCoordinateY);
    }

    @Override
    public void deleteById(UUID widgetId) {
        Widget deleteWidget = findById(widgetId);
        widgetRepository.deleteById(deleteWidget.getId());
    }

    @Override
    public Widget findById(UUID widgetId) {
        return widgetRepository.findById(widgetId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Widget with id:'%s' was not found", widgetId)));
    }

    @Override
    public List<Widget> findAll() {
        return widgetRepository.findAll();
    }

    @Override
    public List<Widget> findAll(Pageable pageable) {
        return widgetRepository.findAll(pageable)
                .getContent();
    }

    @Override
    public List<Widget> findAllSortedByIndexZ(Pageable pageable) {
        return widgetRepository.findAllSortedByIndexZ(pageable)
                .getContent();
    }

    @Override
    public List<Widget> findAllByArea(Pageable pageable, Integer width, Integer height) {

        List<Widget> existWidgets = widgetRepository.findAllSortedByWidthAndHeight();

        return binarySearchOfEdgeIndex(existWidgets, 0, existWidgets.size() - 1, width, height)
                .map(edgeWidgetIndex -> existWidgets.subList(0, edgeWidgetIndex + 1).stream()
                        .sorted(Comparator.comparingInt(Widget::getIndexZ))
                        .collect(toList()))
                .orElse(emptyList());
    }

    private Optional<Integer> binarySearchOfEdgeIndex(List<Widget> widgets, int start, int end, int width, int height) {

        if (end >= start) {
            int middle = start + ((end - start) >> 1);

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
        boolean result = checkWidget.getMaxCoordinateX() <= width && checkWidget.getMaxCoordinateY() <= height;

        if (middle < widgets.size() - 1) {
            Widget nextWidget = widgets.get(middle + 1);
            return result && (nextWidget.getMaxCoordinateX() > width || nextWidget.getMaxCoordinateY() > height);
        }

        return result;
    }

    private boolean isCoordinateMoreThanValue(List<Widget> widgets, Integer middle, int width, int height) {
        Widget widget = widgets.get(middle);
        return widget.getMaxCoordinateX() > width && widget.getMaxCoordinateY() > height;
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

        Set<UUID> updatedWidgets = new HashSet<>();

        return insertWidgetWithShift(widgetsMap, updatedWidgets, newWidget).values().stream()
                .filter(widget -> isNull(widget.getId()) || updatedWidgets.contains(widget.getId()))
                .collect(toList());
    }

    /*
     * Also can be done via iterator, I decided to try this approach
     */
    private static HashMap<Integer, Widget> insertWidgetWithShift(HashMap<Integer, Widget> widgetsMap, Set<UUID> updatedWidgets, Widget newWidget) {

        Optional.ofNullable(widgetsMap.get(newWidget.getIndexZ()))
                .ifPresentOrElse(widget -> {
                            widgetsMap.remove(widget.getIndexZ());
                            widgetsMap.put(newWidget.getIndexZ(), newWidget);
                            updatedWidgets.add(newWidget.getId());

                            if (isNull(widget.getId())) {
                                throw new IllegalStateException("Can't merge widgets with null id");
                            } else if (!widget.getId().equals(newWidget.getId())) {
                                widget.setIndexZ(widget.getIndexZ() + 1);
                                updatedWidgets.add(widget.getId());
                                insertWidgetWithShift(widgetsMap, updatedWidgets, widget);
                            }
                        },
                        () -> {
                            updatedWidgets.add(newWidget.getId());
                            widgetsMap.put(newWidget.getIndexZ(), newWidget);
                        });

        return widgetsMap;
    }

}
