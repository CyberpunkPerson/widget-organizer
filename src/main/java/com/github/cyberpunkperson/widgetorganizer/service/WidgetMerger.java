package com.github.cyberpunkperson.widgetorganizer.service;

import com.github.cyberpunkperson.widgetorganizer.domain.Widget;

import java.util.List;

public interface WidgetMerger {

    List<Widget> mergeEngagedOnly(List<Widget> widgets, Widget newWidget);

    List<Widget> mergeAll(List<Widget> widgets, Widget newWidget);

}
