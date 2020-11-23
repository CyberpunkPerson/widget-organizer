package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WidgetService {

    Widget create(Widget newWidget);

    Widget update(Widget newWidget);

    void deleteById(UUID widgetId);

    Widget findById(UUID widgetId);

    List<Widget> findAll();

    List<Widget> findAll(Pageable pageable);

    List<Widget> findAllSortedByIndexZ(Pageable pageable);

    List<Widget> findAllByArea(Pageable pageable, Integer width, Integer height);

}
