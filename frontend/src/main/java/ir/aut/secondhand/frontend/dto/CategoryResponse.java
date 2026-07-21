package ir.aut.secondhand.frontend.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String validationSchema;
    private Boolean selectable;
    private List<CategoryResponse> subCategories =
            new ArrayList<>();

    public CategoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getValidationSchema() {
        return validationSchema;
    }

    public Boolean getSelectable() {
        return selectable;
    }

    public List<CategoryResponse> getSubCategories() {
        return subCategories;
    }

    @Override
    public String toString() {
        return name;
    }
}