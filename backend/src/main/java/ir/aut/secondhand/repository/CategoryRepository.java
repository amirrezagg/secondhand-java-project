package ir.aut.secondhand.repository;

import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}