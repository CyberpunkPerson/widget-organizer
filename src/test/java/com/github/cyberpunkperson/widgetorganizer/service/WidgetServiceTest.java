package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import com.github.cyberpunkperson.widgetorganizer.repository.WidgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetServiceTest {

    @InjectMocks
    private WidgetService widgetService;

    @Mock
    private WidgetRepository widgetRepository;


    @Test
    public void createWidgetWithDoubleShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 2, 3, 4));
            add(new Widget(null, 5, 6, 3, 3, 4));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 3, 3, 4));
            add(new Widget(null, 5, 6, 4, 3, 4));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void createWidgetWithoutShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 5, 3, 4));
            add(new Widget(null, 5, 6, 6, 3, 4));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 5, 3, 4));
            add(new Widget(null, 5, 6, 6, 3, 4));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void createWidgetWithSingleShift() {

        Widget newWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 2, 3, 4));
            add(new Widget(null, 5, 6, 4, 3, 4));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 2, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 3, 3, 4));
            add(new Widget(null, 5, 6, 4, 3, 4));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void createWidgetWithIndexZGeneration() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4);
        List<Widget> existWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 2, 3, 4));
            add(new Widget(null, 5, 6, 4, 3, 4));
        }};

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 5, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>() {{
            add(new Widget(null, 5, 6, 1, 3, 4));
            add(new Widget(null, 5, 6, 2, 3, 4));
            add(new Widget(null, 5, 6, 4, 3, 4));
        }};

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void createWidgetWithEmptyExistWidgets() {

        Widget newWidget = new Widget(null, 3, 4, 5, 4, 4);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 5, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>();

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void createWidgetWithNullIndexZAndEmptyExistWidgets() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        Widget expectedMergedWidget = new Widget(null, 3, 4, 0, 4, 4);
        List<Widget> expectedMergedWidgets = new ArrayList<>();

        widgetService.create(newWidget);

        verify(widgetRepository).saveAll(eq(expectedMergedWidgets));
        verify(widgetRepository).save(eq(expectedMergedWidget));
    }

    @Test
    public void updateWidgetWithNullIdExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

    @Test
    public void updateNotExistedWidgetExceptionThrown() {

        Widget newWidget = new Widget(null, 3, 4, null, 4, 4);
        List<Widget> existWidgets = new ArrayList<>();

        when(widgetRepository.findAll())
                .thenReturn(existWidgets);

        assertThrows(IllegalArgumentException.class, () -> widgetService.update(newWidget));
    }

}
