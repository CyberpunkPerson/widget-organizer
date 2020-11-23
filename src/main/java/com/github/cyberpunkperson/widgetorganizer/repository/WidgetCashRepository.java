package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;


@Repository
@Profile("cash")
public class WidgetCashRepository implements WidgetRepository {

    private ConcurrentHashMap<UUID, Widget> widgetCash;


    public WidgetCashRepository() {
        this.widgetCash = new ConcurrentHashMap<>();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    public Optional<Widget> findById(UUID widgetId) {
        return Optional.ofNullable(widgetCash.get(widgetId));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    public List<Widget> findAll() {
        return new ArrayList<>(widgetCash.values());
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    public Page<Widget> findAll(Pageable pageable) {

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = startIndex + pageable.getPageSize();

        return new PageImpl<>(new ArrayList<>(widgetCash.values()).subList(startIndex, endIndex));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    public Page<Widget> findAllSortedByIndexZ(Pageable pageable) {

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = startIndex + pageable.getPageSize();

        List<Widget> sortedWidgets = widgetCash.values().stream()
                .sorted(Comparator.comparingInt(Widget::getIndexZ))
                .collect(toList());

        return new PageImpl<>(sortedWidgets.subList(startIndex, endIndex));
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    public List<Widget> findAllSortedByWidthAndHeight() {

        return widgetCash.values().stream()
                .sorted(Comparator.comparing(Widget::getMaxCoordinateX)
                        .thenComparing(Widget::getCoordinateY))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<Widget> saveWidgets(List<Widget> widgets) {
        Map<UUID, Widget> widgetsToSave = widgets.stream()
                .peek(widget -> {
                    if (isNull(widget.getId())) {
                        widget.setId(UUID.randomUUID());
                        widget.setCreatedDate(ZonedDateTime.now());

                    } else if (!widget.equals(this.widgetCash.get(widget.getId()))) {
                        widget.setLastModifiedDate(ZonedDateTime.now());
                    }
                })
                .collect(toMap(Widget::getId, widget -> widget));

        this.widgetCash.putAll(widgetsToSave);
        return widgets;
    }

    @Override
    @Transactional
    public void deleteById(UUID widgetId) {
        this.widgetCash.remove(widgetId);
    }
}
