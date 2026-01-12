package org.acme.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection="CATEGORY")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
    private ObjectId id;
    private String descrizione;
    private String codice;
}
