package com.weiyan.atp.utils;

/**
 * {@code JsonProviderHolder} hold {@code JsonProvider} for providing Json related
 * features/methods.
 */
public class JsonProviderHolder {

    private JsonProviderHolder() {

    }

    /**
     * Create the json provider of Jackson.
     */
    public static final JsonProvider JACKSON = new JacksonProvider();

}
