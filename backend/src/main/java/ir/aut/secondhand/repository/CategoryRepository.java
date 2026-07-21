package ir.aut.secondhand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ir.aut.secondhand.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByParentId(Long parentId);
}