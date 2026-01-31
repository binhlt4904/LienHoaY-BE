package com.he180773.testreact.repository;

import com.he180773.testreact.entity.Product;
import jdk.jfr.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "WHERE p.category = :category AND p.totalQuantity > 0")
    List<Product> findAllByCategoryAndInStock(@Param("category") String category);

    @Query("SELECT p FROM Product p " +
            "WHERE p.totalQuantity > 0")
    List<Product> findAllInStock();



    Page<Product> findAllByCategory(String category, Pageable pageable);

    List<Product> findAllByCategory(String category);

    List<Product> findAllByOrderByArrivedDateDesc();

    @Query("SELECT DISTINCT p FROM Product p " +
            "WHERE p.id IN (" +
            "   SELECT v.productId FROM ProductVariant v " +
            "   WHERE (:minPrice IS NULL OR v.price >= :minPrice) " +
            "     AND (:maxPrice IS NULL OR v.price <= :maxPrice)" +
            ") " +
            "AND p.arrivedDate >= :startDate " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findNewArrivalsSorted(
            @Param("startDate") LocalDateTime startDate,
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.arrivedDate >= :startDate"+
            " AND p.totalQuantity > 0")
    List<Product> findNewArrivals(@Param("startDate") LocalDateTime startDate);

    // Tìm theo tên chứa và lọc theo khoảng giá (min-max)
    @Query("""
SELECT  p FROM Product p
WHERE EXISTS (
   SELECT v.productId FROM ProductVariant v
   WHERE (:minPrice IS NULL OR v.price >= :minPrice)
     AND (:maxPrice IS NULL OR v.price <= :maxPrice)
)
AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
ORDER BY
  CASE
    WHEN LOWER(p.category) LIKE '%buddhist%'  THEN 1
    WHEN LOWER(p.category) LIKE '%robe%' THEN 2
    WHEN LOWER(p.category) LIKE '%accessory%'  THEN 3
    ELSE 4
  END,
  p.id DESC
""")
    Page<Product> findFilteredProducts(
            @Param("category") String category,
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );


    @Query("SELECT DISTINCT p FROM Product p " +
            "WHERE p.id IN (" +
            "   SELECT v.productId FROM ProductVariant v " +
            "   WHERE (:minPrice IS NULL OR v.price >= :minPrice) AND" +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:maxPrice IS NULL OR v.price <= :maxPrice))")
    Page<Product> findFilteredAllProducts(
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );

    List<Product> findAllByNameContains(String name);


}
