package com.example.adc.config;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;


public class DatastoreConfig {

    private static final Datastore DATASTORE = DatastoreOptions.getDefaultInstance().getService();

    private DatastoreConfig(){}

    public static Datastore getDatastore(){
        return DATASTORE;
    }
}
