package com.how2java.tmall.service;

import com.how2java.tmall.dao.ReviewDao;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    ReviewDao reviewDao;
    public  int countByProduct(Product product){
        return reviewDao.countByProduct(product);
    }
    public List<Review> getByProduct(Product product){
        return reviewDao.getByProductOrderByIdDesc(product);
    }
    public void add(Review review){
        reviewDao.save(review);
    }

}
