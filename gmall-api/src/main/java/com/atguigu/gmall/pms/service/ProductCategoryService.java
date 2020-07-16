package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.vo.product.PmsProductAttributeCategoryItem;
import com.atguigu.gmall.vo.product.PmsProductCategoryWithChildrenItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author GA
 * @since 2020-01-30
 */
public interface ProductCategoryService extends IService<ProductCategory> {
    /**
     * 查询一个菜单以及他下面的子菜单
     * @param i
     * @return
     */
    List<PmsProductCategoryWithChildrenItem> listCatlogWithChildren(Integer i);
}
