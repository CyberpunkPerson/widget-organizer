package com.github.cyberpunkperson.widgetorganizer.config;

import com.github.cyberpunkperson.widgetorganizer.annotation.Projection;
import org.modelmapper.ModelMapper;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.stream.Stream;

public class ProjectionMethodProcessor extends RequestResponseBodyMethodProcessor {

    private final ModelMapper modelMapper;

    public ProjectionMethodProcessor(ModelMapper modelMapper) {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Projection.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object projection = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        return modelMapper.map(projection, parameter.getParameterType());
    }

    @Override
    protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        Class<?> projectionType = Stream.of(parameter.getParameterAnnotations())
                .filter(Projection.class::isInstance)
                .map(annotation -> ((Projection) annotation).value())
                .findFirst()
                .orElseThrow(() -> new AnnotationConfigurationException(String.format("'%s' annotation was not found", Projection.class.getName())));

        return super.readWithMessageConverters(webRequest, parameter, projectionType);
    }
}
