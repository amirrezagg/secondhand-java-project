package ir.aut.secondhand.repository;

import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByParentId(Long parentId);
}