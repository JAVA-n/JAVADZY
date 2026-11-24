package com.mall.bean;

import java.io.Serializable;

/**
 * 商品分类实体类（JavaBean），对应数据库 category 表。
 *
 * @author 张三
 * @studentId 20240301
 */
public class Category implements Serializable {
    private Integer categoryId;
    private String categoryName;
    private String categoryDesc;
    private Integer sortOrder;
    private Integer parentId;
    private Integer productCount; // 统计字段：该分类下的商品总数

    public Category() {
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

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
}
