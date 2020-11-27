package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class WidgetShiftMerger implements WidgetMerger {


    @Override
    public List<Widget> mergeEngagedOnly(List<Widget> widgets, Widget newWidget) {

        Set<UUID> engagedWidgets = new HashSet<>();
        return mergeWidgets(widgets, newWidget, engagedWidgets).stream()
                .filter(widget -> isNull(widget.getId()) || engagedWidgets.contains(widget.getId()))
                .collect(toList());
    }

    @Override
    public List<Widget> mergeAll(List<Widget> widgets, Widget newWidget) {
        return new ArrayList<>(mergeWidgets(widgets, newWidget, new HashSet<>()));
    }

    private Collection<Widget> mergeWidgets(List<Widget> widgets, Widget newWidget, Set<UUID> engagedWidgets) {

        if (isEmpty(widgets)) {
            engagedWidgets.add(newWidget.getId());
            return mergeWithEmptyWidgets(widgets, newWidget);
        }

        if (isNull(newWidget.getIndexZ())) {
            engagedWidgets.add(newWidget.getId());
            return mergeWithNullIndexZ(widgets, newWidget);
        }

        HashMap<Integer, Widget> widgetsMap = widgets.stream()
                .collect(toMap(Widget::getIndexZ, widget -> widget, (prev, next) -> next, HashMap::new));

        return insertWidgetWithShift(widgetsMap, newWidget, engagedWidgets).values();
    }

    private List<Widget> mergeWithNullIndexZ(List<Widget> widgets, Widget newWidget) {
        widgets.stream()
                .mapToInt(Widget::getIndexZ)
                .max()
                .ifPresent(maxZIndex -> newWidget.setIndexZ(maxZIndex + 1));

        return new ArrayList<>() {{
            addAll(widgets);
            add(newWidget);
        }};
    }

    private List<Widget> mergeWithEmptyWidgets(List<Widget> widgets, Widget newWidget) {
        if (isNull(newWidget.getIndexZ())) {
            newWidget.setIndexZ(0);
        }

        return new ArrayList<>() {{
            addAll(widgets);
            add(newWidget);
        }};
    }

    /*
     * Also can be done via iterator, I decided to try this approach
     */
    private static HashMap<Integer, Widget> insertWidgetWithShift(HashMap<Integer, Widget> widgetsMap, Widget newWidget, Set<UUID> engagedWidgets) {

        Optional.ofNullable(widgetsMap.get(newWidget.getIndexZ()))
                .ifPresentOrElse(widget -> {
                            widgetsMap.remove(widget.getIndexZ());
                            widgetsMap.put(newWidget.getIndexZ(), newWidget);
                            engagedWidgets.add(newWidget.getId());

                            if (isNull(widget.getId())) {
                                throw new IllegalStateException("Can't merge widgets with null id");
                            } else if (!widget.getId().equals(newWidget.getId())) {
                                widget.setIndexZ(widget.getIndexZ() + 1);
                                engagedWidgets.add(widget.getId());
                                insertWidgetWithShift(widgetsMap, widget, engagedWidgets);
                            }
                        },
                        () -> {
                            engagedWidgets.add(newWidget.getId());
                            widgetsMap.put(newWidget.getIndexZ(), newWidget);
                        });

        return widgetsMap;
    }
}
