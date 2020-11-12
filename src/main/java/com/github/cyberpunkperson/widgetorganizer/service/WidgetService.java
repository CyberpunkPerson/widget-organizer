package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.springframework.util.Assert.notNull;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;


    public Widget create(Widget widget) {
        return widgetRepository.save(widget);
    }

    public Widget update(Widget widget) { //todo perhaps pessimistic locking required

        notNull(widget.getId(), "Widget id should to be specified for update");

        Widget savedWidget = findById(widget.getId());
        widget.setVersion(savedWidget.getVersion());

        return widgetRepository.save(widget);
    }

    public void deleteById(UUID widgetId) {
        widgetRepository.deleteById(widgetId);
    }

    public Widget findById(UUID widgetId) {
        return widgetRepository.findById(widgetId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Widget with id:'%s' was not found", widgetId)));
    }

    public List<Widget> findAll() {
        return List.copyOf(widgetRepository.findByOrderByIndexZ());
    }


}
