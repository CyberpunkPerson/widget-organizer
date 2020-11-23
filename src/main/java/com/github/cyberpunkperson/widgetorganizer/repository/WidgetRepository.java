package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WidgetRepository {

    Optional<Widget> findById(UUID widgetId);

    List<Widget> findAll();

    Page<Widget> findAll(Pageable pageable);

    Page<Widget> findAllSortedByIndexZ(Pageable pageable);

    List<Widget> findAllSortedByWidthAndHeight();

    List<Widget> saveWidgets(List<Widget> widgets);

    void deleteById(UUID widgetId);
}
