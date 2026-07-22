package ir.aut.secondhand.frontend.dto;

public class CategoryRequest {

    private String name;
    private Long parentId;

    public CategoryRequest() {
    }

    public CategoryRequest(String name) {
        this.name = name;
        this.parentId = null;
    }

    public CategoryRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}