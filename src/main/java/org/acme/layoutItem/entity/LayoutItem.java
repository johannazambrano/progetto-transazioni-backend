package org.acme.layoutItem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayoutItem {
    private ObjectId id;
    private String i;
    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;
    private Integer minW;
    private Integer maxW;
    private Integer minH;
    private Integer maxH;
    private Boolean staticLayout;

}
