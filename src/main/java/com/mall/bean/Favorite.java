package com.mall.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户收藏实体类（JavaBean），对应数据库 favorite 表并关联商品详情。
 *
 * @author 张三
 * @studentId 20240301
 */
public class Favorite implements Serializable {
    private Integer favId;
    private Integer userId;
    private Integer productId;
    private Timestamp createTime;
    private Product product;

    public Integer getFavId() {
        return favId;
    }

    public void setFavId(Integer favId) {
        this.favId = favId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
