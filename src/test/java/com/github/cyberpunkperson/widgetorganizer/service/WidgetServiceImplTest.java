package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetServiceImplTest {

    @InjectMocks
    private WidgetServiceImpl widgetService;

    @Mock
    private WidgetRepository widgetRepository;


    @Test
    public void updateWidgetWithNullIdExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

    @Test
    public void updateNotExistedWidgetExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

    @Test
    public void filterWidgetsByArea() {

        Widget widget1 = new Widget(null, 50, 50, 4, 98, 100, 99f, null, 100f, null);
        Widget widget2 = new Widget(null, 50, 50, 1, 100, 100, 100f, null, 100f, null);
        Widget widget3 = new Widget(null, 50, 100, 4, 100, 98, 100f, null, 99f, null);
        Widget widget4 = new Widget(null, 50, 100, 2, 100, 100, 100f, null, 150f, null);
        Widget widget5 = new Widget(null, 50, 100, 4, 100, 101, 100f, null, 150.5f, null);
        Widget widget6 = new Widget(null, 50, 50, 4, 101, 100, 100.5f, null, 100f, null);
        Widget widget7 = new Widget(null, 75, 75, 4, 100, 100, 125f, null, 125f, null);

        List<Widget> existWidgets = List.of(widget1, widget2, widget3, widget4, widget5, widget6, widget7);

        when(widgetRepository.findAllSortedByWidthAndHeight())
                .thenReturn(existWidgets);

        List<Widget> expectedWidgets = Stream.of(widget1, widget2, widget3, widget4)
                .sorted(Comparator.comparingInt(Widget::getIndexZ))
                .collect(toList());

        List<Widget> filterWidgets = widgetService.findAllByArea(PageRequest.of(0, 10), 100, 150);

        assertEquals(expectedWidgets, filterWidgets);
    }

    @Test
    public void filterWidgetsByAreaOutOfArea() {

        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 50, 50, 1, 100, 100, 100f, null, 100f, null));
            add(new Widget(null, 50, 100, 2, 100, 100, 100f, null, 150f, null));
            add(new Widget(null, 100, 100, 4, 100, 100, 150f, null, 150f, null));
        }};

        when(widgetRepository.findAllSortedByWidthAndHeight())
                .thenReturn(existWidgets);

        List<Widget> expectedWidgets = emptyList();

        List<Widget> filterWidgets = widgetService.findAllByArea(PageRequest.of(0, 10), 10, 15);

        assertEquals(expectedWidgets, filterWidgets);
    }

}
