package com.example.adc.repository;

import com.example.adc.config.DatastoreConfig;
import com.example.adc.model.Session;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class SessionRepository {

    private static final String KIND = "Session";
    private final Datastore datastore;

    public SessionRepository(){
        this.datastore = DatastoreConfig.getDatastore();
    }


    public void save(Session session){
       Key key = datastore.newKeyFactory().setKind(KIND)
       .newKey(session.getTokenId());

       Entity entity = Entity.newBuilder(key)
       .set("tokenId", session.getTokenId())
       .set("userId", session.getUserId())
       .set("role", session.getRole())
       .set("issuedAt", session.getIssuedAt())
       .set("expiresAt", session.getExpiresAt())
       .build();

       datastore.put(entity);
    }


    public Session findByTokenId(String tokenId){
      Key key = datastore.newKeyFactory().setKind(KIND).newKey(tokenId);
      Entity entity = datastore.get(key);

      if (entity == null){ return null; }

      return new Session(
        entity.getString("tokenId"),
        entity.getString("userId"),
        entity.getString("role"),
        entity.getString("issuedAt"),
        entity.getString("expiresAt")
      );

    }
    
}
