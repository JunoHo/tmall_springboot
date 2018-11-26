package com.how2java.tmall.web;
 
import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderService orderService;
    @GetMapping("/forehome")
    public List<Category> home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }
    @PostMapping("/foreregister")
    public Result regist(@RequestBody User user){
        String name=user.getName();
        name=HtmlUtils.htmlEscape(name);
        user.setName(name);
        if(userService.isExist(user.getName())){
        return Result.fail("用户名已存在");
      }
      userService.add(user);
      return Result.success();
    }
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session, HttpServletResponse response,HttpServletRequest request) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60*60*24*7);
            System.out.println(session.getMaxInactiveInterval());
            Cookie cookie=new Cookie("JSESSIONID",session.getId());
            cookie.setMaxAge(60*60*24*7);
            cookie.setPath(request.getContextPath());
            response.addCookie(cookie);
            return Result.success();
        }
    }
   @GetMapping("/foreproduct/{pid}")
    public Object getProduct(@PathVariable int pid){
        Product product=productService.get(pid);
       List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
       List<ProductImage> productSingleImages1 = productImageService.listSingleProductImages(product);
       product.setFirstProductImage(productSingleImages1.get(0));
       product.setProductDetailImages(productDetailImages);
       product.setProductSingleImages(productSingleImages1);
       product.setReviewCount(reviewService.countByProduct(product));
       product.setSaleCount(orderItemService.getSalesCountByProduct(product));
       List<PropertyValue> pvs = propertyValueService.list(product);
       List<Review> reviews = reviewService.getByProduct(product);
       HashMap<String,Object>map=new HashMap<>();
       map.put("product",product);
       map.put("pvs",pvs);
       map.put("reviews",reviews);
       return Result.success(map);
       //测试图片显示不正常待解决
   }
    @GetMapping("/forecheckLogin")
    public Result checkLogin(HttpSession session){
        User user= (User) session.getAttribute("user");
        if(user!=null)
            return Result.success();
        return Result.fail("未登录");
    }
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category c = categoryService.findOne(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return c;
    }
    @PostMapping("foresearch")
    public Object search( String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }
    @GetMapping("forebuyone")
    public Object buyone(int pid,int num,HttpSession session){
      return buyoneAndAddcart(pid,num,session);
    }
    public int buyoneAndAddcart(int pid,int num,HttpSession session){
        Product product = productService.get(pid);
        int oiid=0;
        User user= (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem orderItem : ois) {
            if (orderItem.getProduct().getId() == pid) {
                num += orderItem.getNumber();
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                found = true;
                oiid = orderItem.getId();
                break;
            }
        }
        if(!found){
            OrderItem oi=new OrderItem();
            oi.setProduct(product);
            oi.setNumber(num);
            oi.setUser(user);
            orderItemService.update(oi);
            oiid=oi.getId();
        }
        return oiid;

    }

    @GetMapping("forebuy")
    public Object buy(int[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for(int id:oiid){
            OrderItem oi = orderItemService.getById(id);
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            orderItems.add(oi);
        }
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        session.setAttribute("ois",orderItems);
        HashMap<String,Object> results=new HashMap<>();
        results.put("total",total);
        results.put("orderItems",orderItems);
        return Result.success(results);
    }
    @PostMapping("forecreateOrder")
    public Object createOrder(HttpSession session,@RequestBody Order order){
        User user= (User) session.getAttribute("user");
        if(user==null)
            return Result.fail("未登录");
        int num=0;
        float total=0;
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setStatus(OrderService.waitPay);
        order.setUser(user);
        order.setCreateDate(new Date());
        orderService.add(order, ois);
        for(OrderItem oi:ois){
            total+= oi.getProduct().getPromotePrice() * oi.getNumber();
            num+=oi.getNumber();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
        return Result.success(map);
    }
    @GetMapping("forepayed")
    public Object payed(int oid){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddcart(pid,num,session);
        return Result.success();
    }
    @GetMapping("forecart")
    public Object getCart(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        return orderItems;
    }
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(int pid,int num,HttpSession session){
        User user = (User) session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        OrderItem orderItem = orderItemService.listByUserAndProduct(user, productService.get(pid));
        orderItem.setNumber(num);
        orderItemService.update(orderItem);
        return Result.success();
    }
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(int oiid,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }
    @GetMapping("forereview")//需要返回order,product.review
    public Object getReview(int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        List<OrderItem> orderItems = order.getOrderItems();
        Product p=orderItems.get(0).getProduct();
        productService.setSaleAndReviewNumber(p);
        List<Review> reviews = reviewService.getByProduct(p);
        Map<String,Object> map=new HashMap<>();
        map.put("p", p);
        map.put("o",order);
        map.put("reviews",reviews);
        return Result.success(map);
    }
    @PostMapping("foredoreview")
    public Object review(int oid,int pid,String content,HttpSession session){
        User user= (User) session.getAttribute("user");
        if(user==null){
            return Result.fail("未登录");
        }
        Review review=new Review();
        review.setProduct(productService.get(pid));
        review.setUser(orderService.get(oid).getUser());
        review.setContent(content);
        review.setCreateDate(new Date());
        reviewService.add(review);
        return Result.success();
    }
}