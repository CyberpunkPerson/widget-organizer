package com.github.cyberpunkperson.widgetorganizer.repository;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

public interface WidgetPageableRepository extends PagingAndSortingRepository<Widget, UUID> {

    @Transactional(isolation = READ_COMMITTED, readOnly = true)
    List<Widget> findByOrderByIndexZ(Pageable pageable);

}
