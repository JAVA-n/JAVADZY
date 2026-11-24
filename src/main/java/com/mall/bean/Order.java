package com.mall.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单主表实体类（JavaBean），对应数据库 orders 表，关联订单商品明细集合。
 *
 * @author 张三
 * @studentId 20240301
 */
public class Order implements Serializable {
    private String orderId;
    private Integer userId;
    private String userName;
    private BigDecimal totalPrice;
    private Integer orderState; // 0: 待发货, 1: 已发货, 2: 已完成, 3: 已取消
    private String consignee;
    private String phone;
    private String address;
    private String remark;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<OrderItem> items = new ArrayList<>();

    public String getStateLabel() {
        if (orderState == null) {
            return "未知";
        }
        switch (orderState) {
            case 0:
                return "待发货";
            case 1:
                return "已发货";
            case 2:
                return "已完成";
            case 3:
                return "已取消";
            default:
                return "处理中";
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
