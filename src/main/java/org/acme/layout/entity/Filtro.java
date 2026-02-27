package org.acme.layout.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filtro {
    private String layoutName;
    private Boolean isDefault;
}
