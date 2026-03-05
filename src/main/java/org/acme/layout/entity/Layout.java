package org.acme.layout.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.acme.layoutItem.entity.LayoutItem;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection="LAYOUT")
public class Layout {
    private ObjectId id;
    private String layoutName;
    private List<LayoutItem> layoutItems;
    private LocalDateTime updatedAt;
    private Boolean isDefault;

}
