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
    public String i;
    public Integer x;
    public Integer y;
    public Integer w;
    public Integer h;
    public Integer minW;
    public Integer maxW;
    public Integer minH;
    public Integer maxH;
    public Boolean staticLayout;

}
