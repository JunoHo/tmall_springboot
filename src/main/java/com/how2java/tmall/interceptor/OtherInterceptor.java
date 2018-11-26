package com.how2java.tmall.interceptor;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CategoryService categoryService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
       return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session=httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartNumber=0;
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        for(OrderItem orderItem:orderItems){
            cartNumber+=orderItem.getNumber();
        }
        List<Category> categories = categoryService.list();
        session.setAttribute("cartTotalItemNumber",cartNumber);
        httpServletRequest.getServletContext().setAttribute("categories_below_search",categories);
        httpServletRequest.getServletContext().setAttribute("contextPath",httpServletRequest.getContextPath());
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
