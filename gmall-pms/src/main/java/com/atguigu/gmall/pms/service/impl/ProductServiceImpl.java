package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.atguigu.gmall.to.es.EsSkuProductInfo;
import com.atguigu.gmall.vo.PageInfoVo;
import com.atguigu.gmall.vo.product.PmsProductParam;
import com.atguigu.gmall.vo.product.PmsProductQueryParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    ProductFullReductionMapper productFullReductionMapper;
    @Autowired
    ProductLadderMapper productLadderMapper;
    @Autowired
    SkuStockMapper skuStockMapper;
    //当前线程共享同样的数据
    ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public Product productInfo(Long id) {
        return productMapper.selectById(id);
    }

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

    /**
     * 很多张表都要被插入数据
     *1.事务的传播行为
     * 考虑事务。。。
     * 1）、那些东西是一定要回滚的、那些及时出错了不必要回滚。
     *      商品的核心信息（基本数据、sku）保存的时候，不要受到别的无关信息的影响。
     *      无关信息出问题，核心信息也不用回滚。
     * 2）、事务的传播行为：propagation：当前方法的事务【是否要和别人公用一个事务】如何传播下去（里面的方法如果用事务，是否和他公用一个事务）
     *     REQUIRED：如果当前方法有事务，就公用一个事务，如果没有就创建一个事务
     *     REQUIRES_NEW：创建一个新事物，如果当前方法有事务，先暂定当前事务，执行内部事务。
     *
     *     SUPPORTS（支持）：当前方法有事务，就以事务的方式运行，没有事务也可以。
     *     MANDATORY（强制）：一定要有事务，没有交报错
     *     NOT_SUPPORTED（不支持）：不支持在事务内运行，如果已经有事务了，就先挂起当前事务。
     *     NEVER（从不使用）：不支持在事务内运行，如果已经有事务了，就抛异常。
     *     NESTED：开启一个子事务（MySQL不支持），需要支持还原点功能的数据库。
     *
     * 事务问题：
     *      Service自己调用自己的方法，无法加上真正的自己内部调整的各个事务
     *      解决：如果是  对象.方法()就好了
     *          1）、要是能拿到ioc容器，从容器中再把我们的组件获取一下，用对象调用方法（变成了代理对象）
     *
     *2.事务的隔离级别：解决读写加锁问题的（数据库底层的方案）（MySQL默认是可重复读）
     * 读未提交：
     * 读已提交：
     * 可重复读：
     * 串行化：
     *
     *
     *
     * 3.异常回滚策略
     * 异常：
     *      运行时异常（不检查异常）
     *          AirthmeticException
     *      编译时异常（受检查异常）
     *          fileNotFound
     *
     *      运行时异常默认是一定回滚
     *           @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {ArithmeticException.class, NullPointerException.class})
     *           noRollBackFor:指定那些异常不用回滚。
     *      编译时异常默认是不回滚的
     *           想让他回滚@Transactional(propagation = Propagation.REQUIRED, rollbackFor = FileNotFoundException.class)
     *           rollbackFor:指定那些异常一定回滚的。
     *       rollbackFor = Exception.class:只要出异常就回滚。
     *
     * @param productParam
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveProduct(PmsProductParam productParam) {
        //获取代理对象
        ProductServiceImpl Proxy = (ProductServiceImpl) AopContext.currentProxy();

        //1)、pms_product：保存商品基本信息
        Proxy.saveBaseINfo(productParam);

        //2)、pms_product_attribute_value：保存这个商品对应的所有属性的值
        Proxy.saveProductAttributeValue(productParam);

        //3)、pms_product_full_reduction：保存商品的满减信息
        Proxy.saveProductFullReduction(productParam);

        //4)、pms_product_ladder：满减表
        Proxy.saveProductLadder(productParam);

        //5)、pms_sku_stok：sku_库存表
        Proxy.saveSkuStok(productParam);

    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        //1、对于数据库是修改商品的状态位
        for (Long id : ids) {
            Product productInfo = productInfo(id);
            Product product = new Product();
            //默认所有属性为null
            product.setId(id);
            product.setPublishStatus(publishStatus);
            //mybatis-plus自带的更新方法是哪个字段有值就更哪个字段
            productMapper.updateById(product);

            //3、如果我上架的是sku
            EsProduct esProduct = new EsProduct();
            //1、赋值基本信息
            BeanUtils.copyProperties(productInfo, esProduct);

            //2、赋值sku信息    对于es要保存商品信息 还要查出这个商品的sku，给es中保存
            List<SkuStock> stocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
            List<EsSkuProductInfo> esSkuProductInfos = new ArrayList<>(stocks.size());

            //查出当前商品的sku属性
            List<ProductAttribute> skuAttributeNames = productAttributeValueMapper.selectProductSaleAttrName(id);
            for (SkuStock skuStok : stocks) {
                EsSkuProductInfo info = new EsSkuProductInfo();
                BeanUtils.copyProperties(skuStok, info);

                //闪亮 黑色
                String subTitle = esProduct.getName();
                if(!StringUtils.isEmpty(skuStok.getSp1())){
                    subTitle+=" "+skuStok.getSp1();
                }
                if(!StringUtils.isEmpty(skuStok.getSp2())){
                    subTitle+=" "+skuStok.getSp2();
                }
                if(!StringUtils.isEmpty(skuStok.getSp3())){
                    subTitle+=" "+skuStok.getSp3();
                }
                //sku的特色标题
                info.setSkuTitle(subTitle);
                List<EsProductAttributeValue> skuAttributeValues = new ArrayList<>();

                for (int i=0;i<skuAttributeNames.size();i++){
                    //skuAttr 颜色/尺码
                    EsProductAttributeValue value = new EsProductAttributeValue();

                    value.setName(skuAttributeNames.get(i).getName());
                    value.setProductId(id);
                    value.setProductAttributeId(skuAttributeNames.get(i).getId());
                    value.setType(skuAttributeNames.get(i).getType());

                    //颜色   尺码;让es去统计‘；改掉查询商品的属性分类里面所有属性的时候，按照sort字段排序好
                    if(i==0){
                        value.setValue(skuStok.getSp1());
                    }
                    if(i==1){
                        value.setValue(skuStok.getSp2());
                    }
                    if(i==2){
                        value.setValue(skuStok.getSp3());
                    }

                    skuAttributeValues.add(value);

                }


                info.setAttributeValues(skuAttributeValues);
                //sku有多个销售属性；颜色，尺码
                esSkuProductInfos.add(info);
                //查出销售属性的名

            }
            esProduct.setEsSkuProductInfos(esSkuProductInfos);

            List<EsProductAttributeValue> attributeValues = productAttributeValueMapper.selectProductBaseAttrAndValue(id);
            //3、复制公共属性信息，查出这个商品的公共属性
            esProduct.setAttrValueList(attributeValues);

            /*try {
                //把商品保存到es中
                Index build = new Index.Builder(esProduct)
                        .index(EsConstant.PRODUCT_ES_INDEX)
                        .type(EsConstant.PRODUCT_INFO_ES_TYPE)
                        .id(id.toString())
                        .build();
                DocumentResult execute = jestClient.execute(build);
                boolean succeeded = execute.isSucceeded();
                if(succeeded){
                    log.info("ES中；id为{}商品上架完成",id);
                }else {
                    log.error("ES中；id为{}商品未保存成功，开始重试",id);
                    //saveProductToEs(id);
                }
            }catch (Exception e){
                log.error("ES中；id为{}商品数据保存异常；{}",id,e.getMessage());
                //saveProductToEs(id);
            }*/

        }

    }

    //5)、pms_sku_stok：sku_库存表
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSkuStok(PmsProductParam productParam) {
        List<SkuStock> skuStockList = productParam.getSkuStockList();
        for (int i = 1; i<=skuStockList.size(); i++){
            SkuStock skuStock = skuStockList.get(i-1);
            if(StringUtils.isEmpty(skuStock.getSkuCode())){
                //skuCode必须有    生成规则   商品id_sku自增id
                skuStock.setSkuCode(threadLocal.get()+"_"+i);
            }
            skuStock.setProductId(threadLocal.get());
            skuStockMapper.insert(skuStock);
        }
    }

    //4)、pms_product_ladder：满减表
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(PmsProductParam productParam) {
        List<ProductLadder> productLadderList = productParam.getProductLadderList();
        for (ProductLadder productLadder : productLadderList) {
            productLadder.setProductId(threadLocal.get());
            productLadderMapper.insert(productLadder);
        }
    }

    //3)、pms_product_full_reduction：保存商品的满减信息
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(PmsProductParam productParam) {
        List<ProductFullReduction> fullReductionList = productParam.getProductFullReductionList();
        for (ProductFullReduction reduction : fullReductionList) {
            reduction.setProductId(threadLocal.get());
            productFullReductionMapper.insert(reduction);
        }
    }

    //2)、pms_product_attribute_value：保存这个商品对应的所有属性的值
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(PmsProductParam productParam) {
        List<ProductAttributeValue> valueList = productParam.getProductAttributeValueList();
        for (ProductAttributeValue item : valueList) {
            item.setProductId(threadLocal.get());
            productAttributeValueMapper.insert(item);
        }
    }

    //1)、pms_product：保存商品基本信息
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseINfo(PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        productMapper.insert(product);
        //mybatis-plus能自动获取刚才这个数据的自增id
        log.debug("刚才添加的商品id{}",product.getId());
        threadLocal.set(product.getId());
    }
}



























