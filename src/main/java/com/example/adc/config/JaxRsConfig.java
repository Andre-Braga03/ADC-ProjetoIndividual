package com.example.adc.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JaxRsConfig extends ResourceConfig {

    public JaxRsConfig() {
        packages("com.example.adc.resource");
        register(JacksonFeature.class);
    }
}
