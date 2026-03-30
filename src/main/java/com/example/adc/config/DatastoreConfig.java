package com.example.adc.config;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

//**
// This class is the configuration for the datastore.
// It handles the configuration for the datastore.
// It is used to get the datastore.
// */
public class DatastoreConfig {

    private static final Datastore DATASTORE = DatastoreOptions.getDefaultInstance().getService();

    private DatastoreConfig(){}

    public static Datastore getDatastore(){
        return DATASTORE;
    }
}
