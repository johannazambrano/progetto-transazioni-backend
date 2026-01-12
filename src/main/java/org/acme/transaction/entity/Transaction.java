package org.acme.transaction.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.category.entity.Category;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection="TRANSACTION")
public class Transaction {
    private ObjectId id;
    public String title;
    public Double amount;
    public Category category;
    public String date;
}
