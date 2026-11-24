package com.mall.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页模型封装类（JavaBean），用于商品列表与后台数据列表分页展示。
 *
 * @author 张三
 * @studentId 20240301
 */
public class PageBean<T> implements Serializable {
    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPages;
    private List<T> list;

    public PageBean(int currentPage, int pageSize, int totalCount, List<T> list) {
        this.pageSize = Math.max(1, pageSize);
        this.totalCount = Math.max(0, totalCount);
        this.totalPages = Math.max(1, (int) Math.ceil((double) this.totalCount / this.pageSize));
        this.currentPage = Math.min(Math.max(1, currentPage), this.totalPages);
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getList() {
        return list;
    }
}
