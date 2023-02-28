package com.weiyan.atp.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * {@code JsonProvider} given a centralize place for
 * providing all JSON related methods/feature.
 */
public interface JsonProvider {
    // Plain Json functions

    /**
     * Convert Object to plain json string.
     */
    String toJsonString(Object value);

    /**
     * Convert Json text to plain Map.
     */
    Map parse(String text);

    /**
     * Convert Json text to Map with specific parametrize type.
     */
    <K, V> Map<K, V> parse(String text, Class<K> keyType, Class<V> valueType);

    /**
     * Convert Json text to Map with specific map type and parametrize type.
     */
    <T extends Map, K, V> Map<K, V> parse(String text, Class<T> mapType,
                                          Class<K> keyType, Class<V> valueType);

    /**
     * Convert Json text to Obj with value type ref.
     */
    <T> T parse(String text, TypeReference<T> valueTypeRef);

    /**
     * Convert Json text to Obj with target type.
     */
    <T> T parse(String text, Class<T> targetType);

    /**
     * Convert Json text to Java list with element type.
     */
    <T> List<T> parseList(String text, Class<T> elementType);

    /**
     * Convert object to Json string.
     */
    String convertObj(Object obj);

    /**
     * Convert object to target type.
     */
    <T> T convertObj(Object obj, Class<T> targetType);

    /**
     * Recursively get field'enums string value from bean.
     */
    String getFieldAsText(Object bean, String field);

    /**
     * Convert object to Map.
     */
    <K, V> Map<K, V> toMap(Object bean, Class<K> keyType, Class<V> valueType);
}
