package com.example.adc.repository;

import com.example.adc.config.DatastoreConfig;
import com.example.adc.model.User;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

/**
 * 
 */
public class UserRepository {

    private static final String KIND = "User";
    private final Datastore datastore;

    public UserRepository(){
        this.datastore = DatastoreConfig.getDatastore();
    }

    public boolean existsByUsername(String username){

        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        Entity entity = datastore.get(key);
        return entity != null;
    }

    public User findByUsername(String username){
       Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
       Entity entity = datastore.get(key);
       
       if (entity == null){ return null; }

       return new User(
        entity.getString("userId"),
        entity.getString("username"),
        entity.getString("passwordHash"),
        entity.getString("email"),
        entity.getString("phone"),
        entity.getString("address"),
        entity.getString("role")

       );
    }

    public void save (User user){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(user.getUsername());

        Entity entity = Entity.newBuilder(key)
        .set("userId", user.getUserId())
        .set("username", user.getUsername())
        .set("passwordHash", user.getPasswordHash())
        .set("email", user.getEmail())
        .set("phone", user.getPhone())
        .set("address", user.getAddress())
        .set("role", user.getRole())
        .build();

        datastore.put(entity);
    }
    
}
