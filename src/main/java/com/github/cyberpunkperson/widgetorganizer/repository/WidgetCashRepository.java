package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Repository
@Validated
@Profile("cash")
public class WidgetCashRepository implements WidgetRepository {

    private final Map<UUID, Widget> widgetCash;
    private final ReentrantReadWriteLock readWriteLock;


    public WidgetCashRepository() {
        this.widgetCash = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public Optional<Widget> findById(UUID widgetId) {
        readWriteLock.readLock().lock();
        try {
            return Optional.ofNullable(widgetCash.get(widgetId));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Widget> findAll() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(widgetCash.values());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Page<Widget> findAll(Pageable pageable) {
        readWriteLock.readLock().lock();
        try {
            int startIndex = pageable.getPageNumber() * pageable.getPageSize();
            int endIndex = startIndex + pageable.getPageSize();

            return new PageImpl<>(new ArrayList<>(widgetCash.values()).subList(startIndex, endIndex));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Page<Widget> findAllSortedByIndexZ(Pageable pageable) {
        readWriteLock.readLock().lock();
        try {
            int startIndex = pageable.getPageNumber() * pageable.getPageSize();

            List<Widget> sortedWidgets = widgetCash.values().stream()
                    .sorted(Comparator.comparingInt(Widget::getIndexZ))
                    .collect(toList());

            int pageLastIndex = (startIndex + pageable.getPageSize());
            int endIndex = Math.min(pageLastIndex, sortedWidgets.size());
            return new PageImpl<>(sortedWidgets.subList(startIndex, endIndex));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Widget> findAllSortedByWidthAndHeight() {
        readWriteLock.readLock().lock();
        try {
            return widgetCash.values().stream()
                    .sorted(comparing(Widget::getMaxCoordinateX)
                            .thenComparing(Widget::getCoordinateY))
                    .collect(toList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Widget> saveWidgets(@Valid List<Widget> widgets) {

        readWriteLock.writeLock().lock();

        Map<UUID, Widget> preparedWidget = widgets.stream()
                .peek(this::prepareWidget)
                .collect(toMap(Widget::getId, widget -> widget));
        try {

            this.widgetCash.putAll(preparedWidget);

        } catch (Exception e) {
            throw new RuntimeException(String.format("Widgets update failed: %s", e.getMessage()));
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return widgets;
    }

    private void prepareWidget(Widget widget) {
        if (isNull(widget.getId())) {
            widget.setId(UUID.randomUUID());
            widget.setCreatedDate(ZonedDateTime.now());
            widget.setLastModifiedDate(ZonedDateTime.now());

        } else if (this.widgetCash.containsKey(widget.getId())) {
            widget.setLastModifiedDate(ZonedDateTime.now());
        } else {
            throw new NoSuchElementException(String.format("Widget with is: '%s' was not found", widget.getId()));
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID widgetId) {
        readWriteLock.writeLock().lock();
        try {
            this.widgetCash.remove(widgetId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
