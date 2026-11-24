package com.mall.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 后台操作日志实体类（JavaBean），对应数据库 operation_log 表。
 *
 * @author 张三
 * @studentId 20240301
 */
public class OperationLog implements Serializable {
    private Integer logId;
    private String adminName;
    private String moduleName;
    private String actionDesc;
    private String ipAddr;
    private Timestamp createTime;

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
