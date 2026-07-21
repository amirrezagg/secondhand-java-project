package ir.aut.secondhand.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.CategoryRequest;
import ir.aut.secondhand.dto.CategoryResponse;
import ir.aut.secondhand.exception.ResourceNotFoundException;
import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.repository.CategoryRepository;
import jakarta.transaction.Transactional;

/**
 * Service layer for managing product categories in a hierarchical structure.
 * 
 * This service enforces a strict two-level category hierarchy where root categories can contain
 * subcategories, but subcategories cannot have further children. Only leaf-level (subcategory) categories
 * are selectable for product classification, ensuring consistent data organization across the marketplace.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all categories and organizes them into a hierarchical tree structure.
     * 
     * This method implements an in-memory tree construction using a HashMap-based parent-to-children mapping.
     * This approach minimizes database queries and efficiently builds the nested hierarchy in a single pass,
     * making it suitable for scenarios where the category tree is read frequently.
     * 
     * @return a list of root-level CategoryResponse objects with their subcategories recursively populated
     */
    public List<CategoryResponse> getAllCategoriesTree() {
        List<Category> allCategories = categoryRepository.findAll();

        // Build a map of parent ID to child categories for O(1) lookup during tree construction
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

        // Recursively build tree structure starting from each root category
        List<CategoryResponse> result = new ArrayList<>();
        for (Category root : roots) {
            result.add(buildTree(root, childrenMap));
        }
        return result;
    }

    /**
     * Recursively builds a CategoryResponse tree node with all its children attached.
     * 
     * @param category the category entity to convert into a response object
     * @param childrenMap a pre-built map of category IDs to their direct children for efficient lookup
     * @return a CategoryResponse with all subcategories recursively populated
     */
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

    /**
     * Creates a new category with optional parent association.
     * 
     * When a parent category is provided, the new category becomes a selectable subcategory.
     * Root categories (those without a parent) are not directly selectable, enforcing the rule that
     * only leaf-level categories can be assigned to products.
     * 
     * @param request the category creation request containing name and optional parent ID
     * @return a CategoryResponse containing the newly created category
     * @throws ResourceNotFoundException if the specified parent category does not exist
     * @throws IllegalArgumentException if the parent category itself has a parent, violating the two-level hierarchy constraint
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("category", "Parent category not found"));

            // Enforce maximum hierarchy depth: prevent categories from becoming parents of parents
            if (parent.getParent() != null) {
                throw new IllegalArgumentException(
                        "Categories can have only one level of subcategories.");
            }

            category.setSelectable(true);
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory);
    }

    /**
     * Updates an existing category's properties and parent association.
     * 
     * This method enforces the hierarchical business rules including circular reference prevention
     * and constraints on reassigning parents to categories that already have children.
     * 
     * @param id the ID of the category to update
     * @param request the update request containing new name and optional parent ID
     * @return a CategoryResponse containing the updated category
     * @throws ResourceNotFoundException if the category or specified parent category does not exist
     * @throws IllegalArgumentException if the requested parent ID equals the category's own ID (circular reference),
     *                                    if the parent has a parent (violates two-level hierarchy),
     *                                    or if the category already has subcategories and a parent is being assigned
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category"));

        // Prevent a category from becoming its own parent (circular reference)
        if (request.getParentId() != null && request.getParentId().equals(category.getId())) {
            throw new IllegalArgumentException("A category cannot be its own parent.");
        }

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("category", "Parent category not found"));

            // Enforce maximum hierarchy depth
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("Categories can have only one level of subcategories.");
            }

            // Prevent converting a branch node (one with children) into a leaf node
            if (categoryRepository.existsByParentId(id)) {
                throw new IllegalArgumentException("Cannot assign a parent to a category that already has subcategories.");
            }

            category.setParent(parent);
            category.setSelectable(true);
        } else {
            // Root categories are not selectable; only leaf-level categories can be used for product classification
            category.setParent(null);
            category.setSelectable(false);
        }

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory);
    }

    /**
     * Deletes a category from the system.
     * 
     * Deletion is only allowed for leaf-level categories (those without subcategories) to prevent
     * orphaning child categories and maintain referential integrity across product assignments.
     * 
     * @param id the ID of the category to delete
     * @throws ResourceNotFoundException if the category does not exist
     * @throws IllegalArgumentException if the category has existing subcategories
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category"));

        // Prevent deletion of branch nodes to maintain referential integrity
        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalArgumentException("Cannot delete a category that has subcategories");
        }

        categoryRepository.delete(category);
    }
}