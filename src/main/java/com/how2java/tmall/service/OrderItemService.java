package com.how2java.tmall.service;
 
import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class OrderItemService {
    @Autowired OrderItemDAO orderItemDAO;
    @Autowired ProductImageService productImageService;
 
    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }
 
    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;           
        for (OrderItem oi :orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber+=oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);     
        order.setOrderItems(orderItems);
    }
     
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }
    public int getSalesCountByProduct(Product product){
        int num=0;
        List<OrderItem> orderItemByProduct = orderItemDAO.findOrderItemByProduct(product);
        for(OrderItem oi:orderItemByProduct){
            num+=oi.getNumber();
        }
        return num;

    }

    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findOrderItemByUserAndOrderIsNull(user);
    }

    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public OrderItem getById(int oiid) {
      return   orderItemDAO.getOne(oiid);
    }
    public OrderItem listByUserAndProduct(User user,Product product){
        return orderItemDAO.findOrderItemByUserAndProduct(user,product);
    }
    public void delete(int oiid){
        orderItemDAO.delete(oiid);
    }
}