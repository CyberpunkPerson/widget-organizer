package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class WidgetShiftMergerTest {

    @InjectMocks
    private WidgetShiftMerger widgetShiftMerger;


    @Test
    public void mergeEngagedWidgetsWithDoubleShift() {

        Widget newWidget = new Widget(UUID.randomUUID(), 3, 4, 2, 4, 4, 5f, 6f, null, null);

        Widget existWidget1 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f, null, null);
        Widget existWidget2 = new Widget(UUID.randomUUID(), 5, 6, 2, 3, 4, 5f, 6f, null, null);
        Widget existWidget3 = new Widget(UUID.randomUUID(), 5, 6, 3, 3, 4, 5f, 6f, null, null);

        List<Widget> expectedWidgets = Stream.of(
                new Widget(newWidget.getId(), 3, 4, 2, 4, 4, 5f, 6f, null, null),
                new Widget(existWidget2.getId(), 5, 6, 3, 3, 4, 5f, 6f, null, null),
                new Widget(existWidget3.getId(), 5, 6, 4, 3, 4, 5f, 6f, null, null)
        ).sorted(comparingInt(Widget::getIndexZ))
                .collect(toList());

        List<Widget> mergedWidgets = widgetShiftMerger.mergeEngagedOnly(List.of(existWidget1, existWidget2, existWidget3), newWidget);
        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }

    @Test
    public void mergeAllWidgetsWithDoubleShift() {

        Widget newWidget = new Widget(UUID.randomUUID(), 3, 4, 2, 4, 4, 5f, 6f, null, null);

        Widget existWidget1 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f, null, null);
        Widget existWidget2 = new Widget(UUID.randomUUID(), 5, 6, 2, 3, 4, 5f, 6f, null, null);
        Widget existWidget3 = new Widget(UUID.randomUUID(), 5, 6, 3, 3, 4, 5f, 6f, null, null);

        List<Widget> expectedWidgets = Stream.of(
                new Widget(existWidget1.getId(), 5, 6, 1, 3, 4, 5f, 6f, null, null),
                new Widget(newWidget.getId(), 3, 4, 2, 4, 4, 5f, 6f, null, null),
                new Widget(existWidget2.getId(), 5, 6, 3, 3, 4, 5f, 6f, null, null),
                new Widget(existWidget3.getId(), 5, 6, 4, 3, 4, 5f, 6f, null, null)
        ).sorted(comparingInt(Widget::getIndexZ))
                .collect(toList());

        List<Widget> mergedWidgets = widgetShiftMerger.mergeAll(List.of(existWidget1, existWidget2, existWidget3), newWidget);
        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }

    @Test
    public void mergeEngagedWidgetsWithoutShift() {

        Widget newWidget = new Widget(UUID.randomUUID(), 3, 4, 2, 4, 4, 5f, 6f, null, null);

        Widget existWidget1 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f, null, null);
        Widget existWidget2 = new Widget(UUID.randomUUID(), 5, 6, 5, 3, 4, 5f, 6f, null, null);
        Widget existWidget3 = new Widget(UUID.randomUUID(), 5, 6, 6, 3, 4, 5f, 6f, null, null);

        List<Widget> expectedWidgets = Collections.singletonList(new Widget(newWidget.getId(), 3, 4, 2, 4, 4, 5f, 6f, null, null));

        List<Widget> mergedWidgets = widgetShiftMerger.mergeEngagedOnly(List.of(existWidget1, existWidget2, existWidget3), newWidget);
        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }

    @Test
    public void mergeEngagedWidgetsWithSingleShift() {

        Widget newWidget = new Widget(UUID.randomUUID(), 3, 4, 2, 4, 4, 5f, 6f, null, null);

        Widget existWidget1 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f, null, null);
        Widget existWidget2 = new Widget(UUID.randomUUID(), 5, 6, 2, 3, 4, 5f, 6f, null, null);
        Widget existWidget3 = new Widget(UUID.randomUUID(), 5, 6, 4, 3, 4, 5f, 6f, null, null);

        List<Widget> expectedWidgets = Stream.of(
                new Widget(newWidget.getId(), 3, 4, 2, 4, 4, 5f, 6f, null, null),
                new Widget(existWidget2.getId(), 5, 6, 3, 3, 4, 5f, 6f, null, null)
        ).sorted(comparingInt(Widget::getIndexZ))
                .collect(toList());

        List<Widget> mergedWidgets = widgetShiftMerger.mergeEngagedOnly(List.of(existWidget1, existWidget2, existWidget3), newWidget);
        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }

    @Test
    public void mergeEngagedWidgetsWithIndexZGeneration() {

        Widget newWidget = new Widget(UUID.randomUUID(), 3, 4, null, 4, 4, 5f, 6f, null, null);
        Widget existWidget1 = new Widget(UUID.randomUUID(), 5, 6, 1, 3, 4, 5f, 6f, null, null);
        Widget existWidget2 = new Widget(UUID.randomUUID(), 5, 6, 2, 3, 4, 5f, 6f, null, null);
        Widget existWidget3 = new Widget(UUID.randomUUID(), 5, 6, 4, 3, 4, 5f, 6f, null, null);

        List<Widget> expectedWidgets = Collections.singletonList(new Widget(newWidget.getId(), 3, 4, 5, 4, 4, 5f, 6f, null, null));

        List<Widget> mergedWidgets = widgetShiftMerger.mergeEngagedOnly(List.of(existWidget1, existWidget2, existWidget3), newWidget);
        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }

    @Test
    public void mergeWithEmptyExistWidgets() {

        Widget newWidget = new Widget(null, 3, 4, 0, 4, 4, 5f, 6f, null, null);

        List<Widget> mergedWidgets = widgetShiftMerger.mergeEngagedOnly(Collections.emptyList(), newWidget);
        List<Widget> expectedWidgets = Collections.singletonList(new Widget(newWidget.getId(), 3, 4, 0, 4, 4, 5f, 6f, null, null));

        assertThat(mergedWidgets)
                .usingElementComparatorIgnoringFields("createdDate", "lastModifiedDate")
                .isEqualTo(expectedWidgets);
    }
}
