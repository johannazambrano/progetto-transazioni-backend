package org.acme.category.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    List<CategoryDTO> categories;
}
