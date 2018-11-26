package com.how2java.tmall.service;


        import com.how2java.tmall.dao.CategoryDao;
        import com.how2java.tmall.dao.ProductDao;
        import com.how2java.tmall.pojo.Category;
        import com.how2java.tmall.pojo.Product;
        import com.how2java.tmall.pojo.ProductImage;
        import com.how2java.tmall.pojo.Property;
        import com.how2java.tmall.util.Page4Navigator;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.domain.Sort;
        import org.springframework.stereotype.Service;

        import java.util.ArrayList;
        import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductDao productDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    public void add(Product product){
        productDao.save(product);
    }
    public void delete(int id){
        productDao.delete(id);
    }
    public Product update(Product product){
        return productDao.save(product);
    }
    public Product get(int id){
        return productDao.findOne(id);
    }
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages){
       Category category= categoryDao.findOne(cid);
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        Page<Product> page=productDao.findByCategory(category,pageable);
        Page4Navigator<Product> page4Navigator=new Page4Navigator<>(page,navigatePages);
        return page4Navigator;
    }
    public void fill(Category category){
        List<Product> products = listByCategory(category);
        category.setProducts(products);

        productImageService.setFirstProdutImages(products);

    }
    public void fill(List<Category> categorys){
        for(Category category:categorys){
            fill(category);
        }
    }
    public void fillByRow(List<Category> categorys){
        int productNumberEachRow = 8;
        for(Category category:categorys){
            List<Product> ps = productDao.findByCategoryOrderById(category);
            List<List<Product> > productsByRow=new ArrayList<>();
            for(int i=0;i<ps.size();i+=productNumberEachRow){
                int size = i+productNumberEachRow;
                size= size>ps.size()?ps.size():size;
                List<Product> productsofEachRow = ps.subList(i, size);
                productsByRow.add(productsofEachRow);
            }
            category.setProductsByRow(productsByRow);
        }

    }

    public List<Product> listByCategory(Category category){
        return productDao.findByCategoryOrderById(category);
    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for(Product p:products){
            setSaleAndReviewNumber(p);
        }
    }

    public void setSaleAndReviewNumber(Product p) {
        p.setSaleCount(orderItemService.getSalesCountByProduct(p));
        p.setReviewCount(reviewService.countByProduct(p));
    }
    public List<Product> search(String keyword, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products =productDao.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

}

