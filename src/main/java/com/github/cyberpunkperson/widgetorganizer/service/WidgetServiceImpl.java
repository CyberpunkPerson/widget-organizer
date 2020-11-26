package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;

@Service
@RequiredArgsConstructor
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;

    private final WidgetMerger widgetMerger;


    @Override
    @Transactional
    public Widget create(Widget widget) {

        List<Widget> existWidgets = findAll();
        List<Widget> mergedWidgets = widgetMerger.mergeEngagedOnly(existWidgets, widget);
        calculateMaxMinCoordinates(widget);

        widgetRepository.saveWidgets(mergedWidgets);
        return widget;
    }

    @Override
    @Transactional
    public Widget update(Widget widget) {

        notNull(widget.getId(), "Widget id should to be specified for update");

        List<Widget> existWidgets = findAll();
        existWidgets.stream()
                .filter(existWidget -> existWidget.getId().equals(widget.getId()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(String.format("Widget with id:'%s' does not exist", widget.getId())));

        List<Widget> mergedWidgets = widgetMerger.mergeEngagedOnly(existWidgets, widget);
        calculateMaxMinCoordinates(widget);

        widgetRepository.saveWidgets(mergedWidgets);
        return widget;
    }

    private void calculateMaxMinCoordinates(Widget widget) {
        Float maxCoordinateX = (widget.getCoordinateX() + (float) widget.getWidth() / 2);
        Float minCoordinateX = (widget.getCoordinateX() - (float) widget.getWidth() / 2);
        Float maxCoordinateY = (widget.getCoordinateY() + (float) widget.getHeight() / 2);
        Float minCoordinateY = (widget.getCoordinateY() - (float) widget.getHeight() / 2);

        widget.setMaxCoordinateX(maxCoordinateX);
        widget.setMinCoordinateX(minCoordinateX);
        widget.setMaxCoordinateY(maxCoordinateY);
        widget.setMinCoordinateY(minCoordinateY);
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
        return widgetRepository.findAll(pageable).stream()
                .collect(toList());
    }

    @Override
    public List<Widget> findAllSortedByIndexZ(Pageable pageable) {
        return widgetRepository.findAllSortedByIndexZ(pageable).stream()
                .collect(toList());
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
}
