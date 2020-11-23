package com.github.cyberpunkperson.widgetorganizer.repository;


import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetCashRepositoryTest {

    @InjectMocks
    private WidgetCashRepository widgetCashRepository;


    @Test
    public void getWidgetById() {

        Widget widget = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, null, null);

        widgetCashRepository.saveWidgets(Collections.singletonList(widget));

        Widget savedWidget = widgetCashRepository.findById(widget.getId())
                .orElseThrow(NoSuchElementException::new);

        assertEquals(widget, savedWidget);
    }

    @Test
    public void deleteWidgetById() {

        Widget widget = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, null, null);

        widgetCashRepository.saveWidgets(Collections.singletonList(widget));

        Widget savedWidget = widgetCashRepository.findById(widget.getId())
                .orElseThrow(NoSuchElementException::new);

        assertEquals(widget, savedWidget);

        widgetCashRepository.deleteById(widget.getId());

        assertEquals(Optional.empty(), widgetCashRepository.findById(widget.getId()));
    }

    @Test
    public void saveWidgetsWithIdGeneration() {

        List<Widget> widgetsToSave = new ArrayList<>() {{
            add(new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, null, null));
            add(new Widget(null, 5, 6, 2, 3, 4, null, null));
            add(new Widget(UUID.randomUUID(), 5, 6, 4, 3, 4, null, null));
            add(new Widget(UUID.randomUUID(), 3, 4, 5, 4, 4, null, null));
        }};

        widgetCashRepository.saveWidgets(widgetsToSave);

        List<Widget> savedWidgets = widgetCashRepository.findAll();

        assertEquals(4, savedWidgets.size());
        assertEquals(Collections.emptyList(),
                savedWidgets.stream()
                        .filter(widget -> isNull(widget.getId()))
                        .collect(toList()));
    }

    @Test
    public void findAllSortedByWidthAndHeight() {

        List<Widget> savedWidgets = new ArrayList<>() {{
            add(new Widget(UUID.randomUUID(), 100, 100, 4, 100, 100, 150f, 150f));
            add(new Widget(UUID.randomUUID(), 50, 100, 2, 100, 100, 100f, 150f));
            add(new Widget(UUID.randomUUID(), 100, 150, 5, 100, 100, 150f, 200f));
            add(new Widget(UUID.randomUUID(), 50, 50, 1, 100, 100, 100f, 100f));
        }};

        widgetCashRepository.saveWidgets(savedWidgets);

        List<Widget> sortedWidgets = savedWidgets.stream()
                .sorted(Comparator.comparingDouble(Widget::getMaxCoordinateX).thenComparing(Widget::getMaxCoordinateY))
                .collect(toList());

        assertEquals(sortedWidgets, widgetCashRepository.findAllSortedByWidthAndHeight());
    }


}
