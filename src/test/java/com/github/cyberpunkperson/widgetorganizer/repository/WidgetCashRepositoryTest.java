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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetCashRepositoryTest {

    @InjectMocks
    private WidgetCashRepository widgetCashRepository;


    @Test
    public void rollbackWidgetsUpdateIfExceptionIsThrown() {

        Widget widget1 = new Widget(null, 5, 6, 1, 3, 4, null, null, null, null);

        widgetCashRepository.saveWidgets(Collections.singletonList(widget1));

        Widget widget2 = new Widget(widget1.getId(), 5, 6, 100, 3, 4, null, null, null, null);
        Widget widget3 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, null, null, null, null);

        assertThrows(RuntimeException.class, () -> widgetCashRepository.saveWidgets(List.of(widget2, widget3)));

        Widget expectedWidget = new Widget(widget1.getId(), 5, 6, 1, 3, 4, null, null, null, null);

        Widget foundWidget = widgetCashRepository.findById(widget1.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(foundWidget)
                .returns(expectedWidget.getId(), from(Widget::getId))
                .returns(expectedWidget.getCoordinateX(), from(Widget::getCoordinateX))
                .returns(expectedWidget.getCoordinateY(), from(Widget::getCoordinateY))
                .returns(expectedWidget.getWidth(), from(Widget::getWidth))
                .returns(expectedWidget.getHeight(), from(Widget::getHeight))
                .returns(expectedWidget.getIndexZ(), from(Widget::getIndexZ));

        assertEquals(1, widgetCashRepository.findAll().size());
    }

    @Test
    public void getWidgetById() {

        Widget widget = new Widget(null, 5, 6, 1, 3, 4, null, null, null, null);

        widgetCashRepository.saveWidgets(Collections.singletonList(widget));

        Widget savedWidget = widgetCashRepository.findById(widget.getId())
                .orElseThrow(NoSuchElementException::new);

        assertEquals(widget, savedWidget);
    }

    @Test
    public void deleteWidgetById() {

        Widget widget = new Widget(null, 5, 6, 1, 3, 4, null, null, null, null);

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
            add(new Widget(null, 5, 6, 1, 3, 4, null, null, null, null));
            add(new Widget(null, 5, 6, 2, 3, 4, null, null, null, null));
            add(new Widget(null, 5, 6, 4, 3, 4, null, null, null, null));
            add(new Widget(null, 3, 4, 5, 4, 4, null, null, null, null));
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
    public void updateWidget() {

        Widget widget = new Widget(null, 5, 6, 1, 3, 4, null, null, null, null);

        widgetCashRepository.saveWidgets(Collections.singletonList(widget));

        widget.setIndexZ(4);
        widget.setWidth(300);
        widget.setHeight(200);
        widgetCashRepository.saveWidgets(Collections.singletonList(widget));

        Widget expectedWidget = new Widget(widget.getId(), 5, 6, 4, 300, 200, null, null, null, null);


        Widget updatedWidget = widgetCashRepository.findById(widget.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(updatedWidget)
                .returns(expectedWidget.getId(), from(Widget::getId))
                .returns(expectedWidget.getCoordinateX(), from(Widget::getCoordinateX))
                .returns(expectedWidget.getCoordinateY(), from(Widget::getCoordinateY))
                .returns(expectedWidget.getWidth(), from(Widget::getWidth))
                .returns(expectedWidget.getHeight(), from(Widget::getHeight))
                .returns(expectedWidget.getIndexZ(), from(Widget::getIndexZ));
    }

    @Test
    public void findAllSortedByWidthAndHeight() {

        List<Widget> savedWidgets = new ArrayList<>() {{
            add(new Widget(null, 100, 100, 4, 100, 100, 150f, 50f, 150f, 50f));
            add(new Widget(null, 50, 100, 2, 100, 100, 100f, 0f, 150f, 50f));
            add(new Widget(null, 100, 150, 5, 100, 100, 150f, 50f, 200f, 100f));
            add(new Widget(null, 50, 50, 1, 100, 100, 100f, 0f, 100f, 0f));
        }};

        widgetCashRepository.saveWidgets(savedWidgets);

        List<Widget> sortedWidgets = savedWidgets.stream()
                .sorted(Comparator.comparingDouble(Widget::getMaxCoordinateX).thenComparing(Widget::getMaxCoordinateY))
                .collect(toList());

        assertEquals(sortedWidgets, widgetCashRepository.findAllSortedByWidthAndHeight());
    }
}
