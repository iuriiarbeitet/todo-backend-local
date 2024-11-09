package com.dev.tasks.business.repository;

import com.dev.tasks.business.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c where " +

            "(:title is null or :title='' " +
            " or lower(c.title) like lower(concat('%', :title,'%'))) " +

            " and c.user.email=:email  " +

            "order by c.title asc")
    List<Category> find(@Param("title") String title, @Param("email") String email);
    List<Category> findByUserEmailOrderByTitleAsc(String email);
}
