package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.vo.PageInfoVo;
import com.atguigu.gmall.vo.product.PmsProductQueryParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author GA
 * @since 2020-01-30
 */
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public PageInfoVo productPageInfo(PmsProductQueryParam param) {

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        if(param.getBrandId()!=null){
            queryWrapper.eq("brand_id",param.getBrandId());
        }
        if(!StringUtils.isEmpty(param.getKeyword())){
            queryWrapper.like("name",param.getKeyword());
        }
        if(param.getProductCategoryId()!=null){
            queryWrapper.eq("product_category_id",param.getProductCategoryId());
        }
        if(!StringUtils.isEmpty(param.getProductSn())){
            queryWrapper.like("product_sn",param.getProductSn());
        }
        if(param.getPublishStatus()!=null){
            queryWrapper.eq("publish_status",param.getPublishStatus());
        }
        if(param.getVerifyStatus()!=null){
            queryWrapper.eq("verify_status",param.getVerifyStatus());
        }
        IPage<Product> page = productMapper.selectPage(new Page<Product>(param.getPageNum(), param.getPageSize()), queryWrapper);
        PageInfoVo pageInfoVo = new PageInfoVo(page.getTotal(),page.getPages(),param.getPageSize(),
                page.getRecords(),page.getCurrent());
        return pageInfoVo;
    }
}



























