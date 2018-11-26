package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDao extends JpaRepository<PropertyValue,Integer>{
   PropertyValue  getByPropertyAndProduct(Property property,Product product);
   List<PropertyValue> findByProductOrderByIdDesc(Product product);
}
