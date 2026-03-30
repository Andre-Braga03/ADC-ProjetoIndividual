package com.example.adc.repository;

import com.example.adc.config.DatastoreConfig;
import com.example.adc.model.User;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the repository for the user.
 * It handles the repository for the user.
 * It is used to relate all users with the datastore.
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
        entity.getString("username"),
        entity.getString("password"),
        entity.getString("phone"),
        entity.getString("address"),
        entity.getString("role")

       );
    }

    public void save (User user){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(user.getUsername());

        Entity entity = Entity.newBuilder(key)
        .set("username", user.getUsername())
        .set("password", user.getPassword())
        .set("phone", user.getPhone())
        .set("address", user.getAddress())
        .set("role", user.getRole())
        .build();

        datastore.put(entity);
    }

    public void deleteByUsername(String username){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        datastore.delete(key);
    }

    public List<User> findAll() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(KIND)
                .build();

        QueryResults<Entity> results = datastore.run(query);
        List<User> users = new ArrayList<>();

        while (results.hasNext()) {
            Entity entity = results.next();

            users.add(new User(
                    entity.getString("username"),
                    entity.getString("password"),
                    entity.getString("phone"),
                    entity.getString("address"),
                    entity.getString("role")
            ));
        }

        return users;
    }

    public void updateRole(String username, String newRole){

        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        Entity currentEntity = datastore.get(key);

        if(currentEntity == null){ return; }
        

        Entity updatedEntity = Entity.newBuilder(currentEntity).set("role", newRole).build();
        datastore.put(updatedEntity);
    }

    public void updatePassword(String username, String newPassword){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        Entity currentEntity = datastore.get(key);

        if(currentEntity == null){ return; }
        
        Entity updatedEntity = Entity.newBuilder(currentEntity).set("password", newPassword).build();
        datastore.put(updatedEntity);
    }

    public void updatePhone(String username, String newPhone){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        Entity currentEntity = datastore.get(key);

        if(currentEntity == null){ return; }

        Entity updatedEntity = Entity.newBuilder(currentEntity).set("phone", newPhone).build();
        datastore.put(updatedEntity);
    }

    public void updateAddress(String username, String newAddress){
        Key key = datastore.newKeyFactory().setKind(KIND).newKey(username);
        Entity currentEntity = datastore.get(key);

        if(currentEntity == null){ return; }
        
        Entity updatedEntity = Entity.newBuilder(currentEntity).set("address", newAddress).build();
        datastore.put(updatedEntity);
    }
}
