package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Category;

import java.util.List;

public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private String validationSchema;
    private List<CategoryResponse> subCategories = new ArrayList<>();

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
        this.validationSchema = category.getValidationSchema();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getValidationSchema() {
        return validationSchema;
    }

    public void setValidationSchema(String validationSchema) {
        this.validationSchema = validationSchema;
    }

    public List<CategoryResponse> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CategoryResponse> subCategories) {
        this.subCategories = subCategories;
    }
}