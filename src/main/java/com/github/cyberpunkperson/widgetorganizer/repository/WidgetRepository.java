package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    Optional<Widget> findById(UUID widgetId);

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    List<Widget> findByOrderByIndexZ();

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    List<Widget> findAll();

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    @Query("select w from Widget w order by (w.coordinateY + w.height / 2) asc, (w.coordinateX + w.width / 2) asc")
    List<Widget> findAllSortedByHeightAndWidth();
}
