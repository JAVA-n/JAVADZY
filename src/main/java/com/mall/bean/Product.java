package com.mall.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * 数码配件商品实体类（JavaBean），对应数据库 product 表，支持实时折扣价计算。
 *
 * @author 张三
 * @studentId 20240301
 */
public class Product implements Serializable {
    private Integer productId;
    private String productName;
    private String subtitle;
    private Integer categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal discount;
    private Integer stock;
    private Integer sales;
    private String image;
    private String specs;
    private String description;
    private Integer isHot;
    private Integer status;
    private Timestamp createTime;

    public Product() {
    }

    /**
     * 计算折后销售单价（保留两位小数）
     */
    public BigDecimal getSalePrice() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        if (discount == null || discount.compareTo(BigDecimal.ONE) >= 0 || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return price.setScale(2, RoundingMode.HALF_UP);
        }
        return price.multiply(discount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 判断商品当前是否处于打折特惠状态
     */
    public boolean isHasDiscount() {
        return discount != null && discount.compareTo(new BigDecimal("0.99")) < 0 && discount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 返回中文折扣标签，例如 0.85 -> "8.5折"，0.90 -> "9折"
     */
    public String getDiscountLabel() {
        if (!isHasDiscount()) {
            return "";
        }
        BigDecimal tenFold = discount.multiply(BigDecimal.TEN).stripTrailingZeros();
        return tenFold.toPlainString() + "折";
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
