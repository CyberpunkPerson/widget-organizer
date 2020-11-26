package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Profile("jdbc")
public interface WidgetJDBCRepository extends JpaRepository<Widget, UUID>, WidgetRepository {

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    Optional<Widget> findById(UUID widgetId);

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    List<Widget> findAll();

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    Page<Widget> findAll(Pageable pageable);

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    Page<Widget> findByOrderByIndexZ(Pageable pageable);

    @Override
    default Page<Widget> findAllSortedByIndexZ(Pageable pageable) {
        return findByOrderByIndexZ(pageable);
    }

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    @Query("select w from Widget w order by  w.maxCoordinateX asc, w.maxCoordinateY asc")
    List<Widget> findAllSortedByWidthAndHeight();

    @Override
    @Transactional
    default List<Widget> saveWidgets(List<Widget> widgets) {
        List<Widget> existWidgets = widgets.stream()
                .filter(widget -> nonNull(widget.getId()))
                .collect(toList());

        if (existWidgets.size() == widgets.size())
            return saveAll(existWidgets);

        saveAll(existWidgets);
        flush();
        List<Widget> newWidgets = widgets.stream()
                .filter(widget -> isNull(widget.getId()))
                .collect(toList());
        saveAll(newWidgets);

        return widgets;
    }
}
