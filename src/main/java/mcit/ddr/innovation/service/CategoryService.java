package mcit.ddr.innovation.service;

import jakarta.persistence.EntityNotFoundException;
import mcit.ddr.innovation.entity.Category;
import mcit.ddr.innovation.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private CategoryService innovationService;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create category service
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get a category by ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

//    public Category updateCategory(Long id, Category updatedCategory) {
//        return categoryRepository.findById(id).map(category -> {
//            if (updatedCategory.getName() != null) {
//                category.setName(updatedCategory.getName());
//            }
//            return categoryRepository.save(category);
//        }).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
//    }
//
//    // Delete a category
//    public void deleteCategory(Long id) {
//        categoryRepository.deleteById(id);
//    }

    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            if (!category.getInnovations().isEmpty()) {
                throw new IllegalStateException("Cannot update category that is in use.");
            }

            if (updatedCategory.getName() != null) {
                category.setName(updatedCategory.getName());
            }

            return categoryRepository.save(category);
        }).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (!category.getInnovations().isEmpty()) {
            throw new IllegalStateException("Cannot delete category that is in use.");
        }

        categoryRepository.deleteById(id);
    }



}
