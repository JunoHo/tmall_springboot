package com.how2java.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
 
public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    public List<OrderItem> findOrderItemByProduct(Product product);
    public List<OrderItem> findOrderItemByUserAndOrderIsNull(User user);
    public OrderItem findOrderItemByUserAndProduct(User user,Product product);
}