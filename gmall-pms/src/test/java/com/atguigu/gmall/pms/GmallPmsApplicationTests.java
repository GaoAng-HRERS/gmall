package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.service.ProductService;
import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPmsApplicationTests {

    @Autowired
    ProductService productService;
    @Autowired
    BrandService brandService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisTemplate<Object,Object> redisTemplateobj;

    @Test
    public void contextLoads() {
        Product byId = productService.getById(1);
        System.out.println(byId.getName());
    }

    @Test
    public void textMasterSlave(){
        /*Brand brand = new Brand();
        brand.setName("测试主从库");
        brandService.save(brand);*/

        Brand brand = brandService.getById(53);

        System.out.println("查询成功！"+brand.getName());

    }

    @Test
    public void testReis(){
        redisTemplate.opsForValue().set("hello","redis");
        String hello = redisTemplate.opsForValue().get("hello");
        System.out.println(hello);
    }

    @Test
    public void testBrand(){
        List<Brand> brands = brandService.listAll();
        for (Brand brand : brands) {
            System.out.println(brand);
        }
    }

}


































