package com.github.cyberpunkperson.widgetorganizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cyberpunkperson.widgetorganizer.controller.dto.WidgetProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void createWidgetWithValidDataIsOkReturned() throws Exception {

        MvcResult result = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget = readJsonAsWidget(result);
        assertNotNull(createdWidget.getId());
        assertNotNull(createdWidget.getLastModifiedDate());
    }

    @Test
    public void createWidgetWithInvalidDataBadRequestReturned() throws Exception {

        mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createWidgetWithNullBodyBadRequestReturned() throws Exception {

        mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void updateWidgetWithValidDataIsOkReturned() throws Exception {

        MvcResult createResult = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget = readJsonAsWidget(createResult);
        assertNotNull(createdWidget.getId());
        assertNotNull(createdWidget.getLastModifiedDate());


        WidgetProjection expectedWidget = WidgetProjection.builder()
                .id(createdWidget.getId())
                .coordinateX(100)
                .coordinateY(100)
                .indexZ(4)
                .width(200)
                .height(300)
                .build();

        MvcResult updateResult = mvc.perform(put("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(expectedWidget)))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection updatedWidget = readJsonAsWidget(updateResult);

        assertThat(updatedWidget)
                .returns(expectedWidget.getId(), from(WidgetProjection::getId))
                .returns(expectedWidget.getCoordinateX(), from(WidgetProjection::getCoordinateX))
                .returns(expectedWidget.getCoordinateY(), from(WidgetProjection::getCoordinateY))
                .returns(expectedWidget.getWidth(), from(WidgetProjection::getWidth))
                .returns(expectedWidget.getHeight(), from(WidgetProjection::getHeight))
                .returns(expectedWidget.getIndexZ(), from(WidgetProjection::getIndexZ));
    }

    @Test
    public void updateNotExistWidgetBadRequestReturned() throws Exception {

        mvc.perform(put("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .id(UUID.randomUUID())
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateWidgetWithNullBodyBadRequestReturned() throws Exception {

        mvc.perform(put("/widgets")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void getWidgetByIdIsOkReturned() throws Exception {

        MvcResult createResult = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget = readJsonAsWidget(createResult);

        MvcResult searchResult = mvc.perform(get("/widgets/{widgetId}", createdWidget.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        WidgetProjection foundWidget = readJsonAsWidget(searchResult);

        assertThat(foundWidget)
                .returns(createdWidget.getId(), from(WidgetProjection::getId))
                .returns(createdWidget.getCoordinateX(), from(WidgetProjection::getCoordinateX))
                .returns(createdWidget.getCoordinateY(), from(WidgetProjection::getCoordinateY))
                .returns(createdWidget.getWidth(), from(WidgetProjection::getWidth))
                .returns(createdWidget.getHeight(), from(WidgetProjection::getHeight))
                .returns(createdWidget.getIndexZ(), from(WidgetProjection::getIndexZ));
    }

    @Test
    public void getNotExistWidgetByIdBadRequestReturned() throws Exception {

        mvc.perform(get("/widgets/{widgetId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void deleteWidgetByIdIsOkReturned() throws Exception {

        MvcResult createResult = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget = readJsonAsWidget(createResult);

        mvc.perform(delete("/widgets/{widgetId}", createdWidget.getId()))
                .andExpect(status().isOk());

        mvc.perform(get("/widgets/{widgetId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void findWidgetsSortedByIndexZIsOkReturned() throws Exception {

        MvcResult createResult1 = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(6)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult createResult2 = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult createResult3 = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(5)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget1 = readJsonAsWidget(createResult1);
        WidgetProjection createdWidget2 = readJsonAsWidget(createResult2);
        WidgetProjection createdWidget3 = readJsonAsWidget(createResult3);


        List<WidgetProjection> sortedWidgets = Stream.of(createdWidget1, createdWidget2, createdWidget3)
                .sorted(Comparator.comparingInt(WidgetProjection::getIndexZ))
                .collect(toList());

        MvcResult searchResult = mvc.perform(get("/widgets")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        List<WidgetProjection> foundWidgets = readJsonAsWidgetsList(searchResult);

        assertThat(foundWidgets)
                .usingElementComparatorIgnoringFields("lastModifiedDate")
                .isEqualTo(sortedWidgets);
    }


    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void findWidgetsFilteredByWidthAndHeightIsOkReturned() throws Exception {

        MvcResult createResult1 = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(50)
                                .coordinateY(50)
                                .indexZ(6)
                                .width(50)
                                .height(50)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult createResult2 = mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(50)
                                .coordinateY(100)
                                .indexZ(4)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        mvc.perform(post("/widgets")
                .contentType(APPLICATION_JSON)
                .content(writeWidgetAsJson(
                        WidgetProjection.builder()
                                .coordinateX(100)
                                .coordinateY(100)
                                .indexZ(5)
                                .width(100)
                                .height(100)
                                .build())))
                .andExpect(status().isOk())
                .andReturn();

        WidgetProjection createdWidget1 = readJsonAsWidget(createResult1);
        WidgetProjection createdWidget2 = readJsonAsWidget(createResult2);


        List<WidgetProjection> sortedWidgets = Stream.of(createdWidget1, createdWidget2)
                .sorted(Comparator.comparingInt(WidgetProjection::getIndexZ))
                .collect(toList());

        MvcResult searchResult = mvc.perform(get("/widgets")
                .param("page", "0")
                .param("width", "100")
                .param("height", "150"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        List<WidgetProjection> foundWidgets = readJsonAsWidgetsList(searchResult);

        assertThat(foundWidgets)
                .usingElementComparatorIgnoringFields("lastModifiedDate")
                .isEqualTo(sortedWidgets);
    }

    @Test
    public void deleteNotExistWidgetByIdBadRequestReturned() throws Exception {

        mvc.perform(delete("/widgets/{widgetId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private String writeWidgetAsJson(WidgetProjection widget) throws JsonProcessingException {
        return objectMapper.writeValueAsString(widget);
    }

    private WidgetProjection readJsonAsWidget(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), WidgetProjection.class);
    }

    private List<WidgetProjection> readJsonAsWidgetsList(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }
}
