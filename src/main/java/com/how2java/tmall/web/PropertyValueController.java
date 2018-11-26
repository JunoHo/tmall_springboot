package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;
    @GetMapping("products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable int pid){
        List<PropertyValue> list = propertyValueService.list(productService.get(pid));
        return list;
    }

    @PutMapping("propertyValues")
    public PropertyValue update(@RequestBody PropertyValue propertyValue){
        propertyValueService.update(propertyValue);
        return propertyValue;
    }
}
