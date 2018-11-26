package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDao;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    public Page4Navigator list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable=new PageRequest(start,size,sort);
        Page page= categoryDao.findAll(pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDao.findAll(sort);
    }
    public void add(Category category){
        categoryDao.save(category);
    }
    public void delete(int id){
        categoryDao.delete(id);
    }

    public Category findOne(Integer id) {
     return categoryDao.findOne(id);
    }

    public Category update(Category c) {
        return categoryDao.save(c);
    }
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products=category.getProducts();
        if(products!=null){
            for(Product p:products){
                p.setCategory(null);
            }
        }
        List<List<Product>> productsByRow=category.getProductsByRow();
        if(productsByRow!=null){
            for(List<Product> ps:productsByRow){
                for(Product p:ps){
                    p.setCategory(null);
                }
            }
        }
    }
}
