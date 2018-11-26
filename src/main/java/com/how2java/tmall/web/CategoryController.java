package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.Page4Navigator;
import com.sun.imageio.plugins.common.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;



@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping(value="/categories")
    public Page4Navigator<Category> list(@RequestParam(value="start",defaultValue = "0") int start, @RequestParam(value = "size",defaultValue = "5") int size){
        start=start<0?0:start;
        return categoryService.list(start,size,5);
    }
    @PostMapping(value="/categories")
    public Object addCategory(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
      categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;

    }

    private void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        File ImageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file= new File(ImageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage img=com.how2java.tmall.util.ImageUtil.change2jpg(file);
        ImageIO.write(img,"jpg", file);
    }
    @DeleteMapping(value="/categories/{id}")
    public String deleteCategory(@PathVariable int id,HttpServletRequest request){
        categoryService.delete(id);
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file=new File(imageFolder,id+".jpg");
        file.delete();
        return null;
    }
    @GetMapping(value="/categories/{id}")
   public Category getCategory(@PathVariable Integer id){
        return categoryService.findOne(id);
   }
   @PutMapping(value="/categories/{id}")
   public Category updateCategory(Category bean,MultipartFile image,HttpServletRequest request) throws IOException {
       String name = request.getParameter("name");
       bean.setName(name);
       categoryService.update(bean);

       if(image!=null) {
           saveOrUpdateImageFile(bean, image, request);
       }
       return bean;
   }
}
