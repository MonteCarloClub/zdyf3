package com.weiyan.atp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * @author : 魏延thor
 * @since : 2020/6/3
 */
@Configuration
public class ConverterConfig {

    @Bean
    public MapperFactory mapperFactory() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
            .mapNulls(false)
            .build();
        mapperFactory.getConverterFactory()
            .registerConverter(new PassThroughConverter(LocalDateTime.class));
        mapperFactory.getConverterFactory()
            .registerConverter(new PassThroughConverter(LocalDate.class));
        return mapperFactory;
    }

    @Bean
    public MapperFacade mapperFacade(MapperFactory mapperFactory) {
        return mapperFactory.getMapperFacade();
    }
}
