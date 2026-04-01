package com.example.adc.repository;

import com.example.adc.config.DatastoreConfig;
import com.example.adc.model.Session;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import java.util.ArrayList;
import java.util.List;

//*
// This class is the repository for the session.
// It handles the repository for the session.
// It is used to relate all sessions with the datastore.
// */
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
       .set("username", session.getUsername())
       .set("role", session.getRole())
       .set("issuedAt", session.getIssuedAt())
       .set("expiresAt", session.getExpiresAt())
       .build();

       datastore.put(entity);
    }

    public void delete(Session session){
      Key key = datastore.newKeyFactory().setKind(KIND).newKey(session.getTokenId());
      datastore.delete(key);
    }
    


    public Session findByTokenId(String tokenId){
      Key key = datastore.newKeyFactory().setKind(KIND).newKey(tokenId);
      Entity entity = datastore.get(key);

      if (entity == null){ return null; }

      return new Session(
        entity.getString("tokenId"),
        entity.getString("username"),
        entity.getString("role"),
        entity.getLong("issuedAt"),
        entity.getLong("expiresAt")
      );

    }

    public List<Session> findAll() {
      Query<Entity> query = Query.newEntityQueryBuilder()
              .setKind(KIND)
              .build();

      QueryResults<Entity> results = datastore.run(query);
      List<Session> sessions = new ArrayList<>();

      while (results.hasNext()) {
        Entity entity = results.next();
        sessions.add(new Session(
                entity.getString("tokenId"),
                entity.getString("username"),
                entity.getString("role"),
                entity.getLong("issuedAt"),
                entity.getLong("expiresAt")
        ));
      }

      return sessions;
    }

    
    
}
