package com.mall.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户实体类（JavaBean），对应数据库 users 表。
 *
 * @author 张三
 * @studentId 20240301
 */
public class User implements Serializable {
    private Integer userId;
    private String userName;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String address;
    private Integer role;     // 0: 普通用户, 1: 管理员
    private Integer status;   // 1: 正常, 0: 禁用
    private Timestamp createTime;

    public User() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
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
