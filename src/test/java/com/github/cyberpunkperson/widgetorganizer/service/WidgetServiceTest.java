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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetServiceTest {

    @InjectMocks
    private WidgetServiceImpl widgetService;

    @Mock
    private WidgetRepository widgetRepository;


    @Test
    public void createWidgetWithDoubleShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 2, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 3, 3, 4, 5f, 6f));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 3, 4, 2, 4, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 3, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 4, 3, 4, 5f, 6f));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void createWidgetWithoutShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 5, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 6, 3, 4, 5f, 6f));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 3, 4, 2, 4, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 5, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 6, 3, 4, 5f, 6f));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void createWidgetWithSingleShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 2, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 4, 3, 4, 5f, 6f));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 3, 4, 2, 4, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 3, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 4, 3, 4, 5f, 6f));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void updateOnlyChangedWidgets() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4, null, null);

        Widget expectedToBeUpdatedWidget = new Widget(UUID.randomUUID(), 5, 6, 2, 3, 4, 5f, 6f);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f));
            add(expectedToBeUpdatedWidget);
            add(new Widget(UUID.randomUUID(), 5, 6, 4, 3, 4, 5f, 6f));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 3, 4, 2, 4, 4, 5f, 6f));
            add(new Widget(expectedToBeUpdatedWidget.getId(), 5, 6, 3, 3, 4, 5f, 6f));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void createWidgetWithIndexZGeneration() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 2, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 4, 3, 4, 5f, 6f));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 2, 3, 4, 5f, 6f));
            add(new Widget(null, 5, 6, 4, 3, 4, 5f, 6f));
            add(new Widget(null, 3, 4, 5, 4, 4, 5f, 6f));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void createWidgetWithEmptyExistWidgets() {

        Widget newWidget = new Widget(null, 3, 4, 5, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 5, 4, 4, 5f, 6f);
        List<Widget> expectedMergedWidgets = Collections.singletonList(expectedMergedWidget);

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void createWidgetWithNullIndexZAndEmptyExistWidgets() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 0, 4, 4, 5f, 6f);
        List<Widget> expectedMergedWidgets = Collections.singletonList(expectedMergedWidget);

        widgetService.create(newWidget);

        verify(widgetRepository).saveWidgets(eq(expectedMergedWidgets));
    }

    @Test
    public void updateWidgetWithNullIdExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

    @Test
    public void updateNotExistedWidgetExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4, null, null);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

    @Test
    public void filterWidgetsByArea() {

        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 50, 50, 4, 98, 100, 99f, 100f));
            add(new Widget(null, 50, 50, 1, 100, 100, 100f, 100f));
            add(new Widget(null, 50, 100, 4, 100, 98, 100f, 99f));
            add(new Widget(null, 50, 100, 2, 100, 100, 100f, 150f));
            add(new Widget(null, 50, 100, 4, 100, 101, 100f, 150.5f));
            add(new Widget(null, 50, 50, 4, 101, 100, 100.5f, 100f));
            add(new Widget(null, 75, 75, 4, 100, 100, 125f, 125f));
        }};

        when(widgetRepository.findAllSortedByWidthAndHeight())
                .thenReturn(existWidgets);

        List<Widget> expectedWidgets = new ArrayList<>() {{
            add(new Widget(null, 50, 50, 4, 98, 100, 99f, 100f));
            add(new Widget(null, 50, 50, 1, 100, 100, 100f, 100f));
            add(new Widget(null, 50, 100, 4, 100, 98, 100f, 99f));
            add(new Widget(null, 50, 100, 2, 100, 100, 100f, 150f));
        }};

        List<Widget> filterWidgets = widgetService.findAllByArea(PageRequest.of(0, 10), 100, 150);

        assertEquals(expectedWidgets, filterWidgets);
    }

    @Test
    public void filterWidgetsByAreaOutOfArea() {

        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 50, 50, 1, 100, 100, 100f, 100f));
            add(new Widget(null, 50, 100, 2, 100, 100, 100f, 150f));
            add(new Widget(null, 100, 100, 4, 100, 100, 150f, 150f));
        }};

        when(widgetRepository.findAllSortedByWidthAndHeight())
                .thenReturn(existWidgets);

        List<Widget> expectedWidgets = emptyList();

        List<Widget> filterWidgets = widgetService.findAllByArea(PageRequest.of(0, 10), 10, 15);

        assertEquals(expectedWidgets, filterWidgets);
    }

}
