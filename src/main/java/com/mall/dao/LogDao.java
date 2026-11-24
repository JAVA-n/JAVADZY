package com.mall.dao;

import com.mall.bean.OperationLog;
import com.mall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台操作日志数据访问类（DAO），记录并查询管理员关键业务操作审计信息。
 *
 * @author 张三
 * @studentId 20240301
 */
public class LogDao {

    public void record(String adminName, String moduleName, String actionDesc, String ipAddr) {
        String sql = "INSERT INTO operation_log (admin_name, module_name, action_desc, ip_addr) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, adminName != null ? adminName : "admin");
            ps.setString(2, moduleName);
            ps.setString(3, actionDesc);
            ps.setString(4, ipAddr);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    public List<OperationLog> findLatest(int limit) {
        List<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_log ORDER BY log_id DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                OperationLog log = new OperationLog();
                log.setLogId(rs.getInt("log_id"));
                log.setAdminName(rs.getString("admin_name"));
                log.setModuleName(rs.getString("module_name"));
                log.setActionDesc(rs.getString("action_desc"));
                log.setIpAddr(rs.getString("ip_addr"));
                log.setCreateTime(rs.getTimestamp("create_time"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }
}
