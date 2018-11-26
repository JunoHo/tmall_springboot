package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @Autowired
    CategoryService categoryService;
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable int cid, @RequestParam(value="start",defaultValue = "0") int start,@RequestParam(value="size",defaultValue = "5")int size){
        start=start<0?0:start;
        return propertyService.list(cid,start,size,5);
    }
    @PostMapping("/properties")
    public Object add(@RequestBody  Property bean){
       propertyService.add(bean);
       return bean;
    }
    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable int id){
        propertyService.delete(id);
        return null;
    }
    @PutMapping("/properties")
    public Object update(@RequestBody  Property bean){
        propertyService.update(bean);
        return bean;
    }
    @GetMapping("/properties/{id}")
    public Property get(@PathVariable int id){
        return propertyService.get(id);
    }
}
