package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.CategoryRequest;
import ir.aut.secondhand.dto.CategoryResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategoriesTree() {
        List<Category> allCategories = categoryRepository.findAll();

        Map<Long, List<Category>> childrenMap = new HashMap<>();
        List<Category> roots = new ArrayList<>();

        for (Category c : allCategories) {
            if (c.getParent() != null) {
                Long parentId = c.getParent().getId();
                if (!childrenMap.containsKey(parentId)) {
                    childrenMap.put(parentId, new ArrayList<>());
                }
                childrenMap.get(parentId).add(c);
            } else {
                roots.add(c);
            }
        }

        List<CategoryResponse> result = new ArrayList<>();
        for (Category root : roots) {
            result.add(buildTree(root, childrenMap));
        }
        return result;
    }

    private CategoryResponse buildTree(Category category, Map<Long, List<Category>> childrenMap) {
        CategoryResponse response = new CategoryResponse(category);
        List<Category> children = childrenMap.get(category.getId());
        List<CategoryResponse> childDtos = new ArrayList<>();

        if (children != null) {
            for (Category child : children) {
                childDtos.add(buildTree(child, childrenMap));
            }
        }

        response.setSubCategories(childDtos);
        return response;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());


        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("category", "Parent category not found"));

            if (parent.getParent() != null) {
                throw new IllegalArgumentException(
                        "Categories can have only one level of subcategories.");
            }

            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category"));

        if (request.getParentId() != null &&
                request.getParentId().equals(category.getId())) {
            throw new IllegalArgumentException("A category cannot be its own parent.");
        }

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("category", "Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return new CategoryResponse(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category"));

        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalArgumentException("Cannot delete a category that has subcategories");
        }

        categoryRepository.delete(category);
    }
}