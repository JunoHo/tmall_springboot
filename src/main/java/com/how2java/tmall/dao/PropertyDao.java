package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PropertyDao extends JpaRepository<Property,Integer> {
    Page<Property> findByCategory(Category category,Pageable pageable);
    List<Property> findByCategory(Category category);

}
