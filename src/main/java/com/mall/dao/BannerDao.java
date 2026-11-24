package com.mall.dao;

import com.mall.bean.Banner;
import com.mall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页轮播广告数据访问类（DAO），支持前台启用轮播查询与后台轮播配置管理。
 *
 * @author 张三
 * @studentId 20240301
 */
public class BannerDao {

    private Banner mapRow(ResultSet rs) throws SQLException {
        Banner b = new Banner();
        b.setBannerId(rs.getInt("banner_id"));
        b.setTitle(rs.getString("title"));
        b.setSubtitle(rs.getString("subtitle"));
        b.setBadge(rs.getString("badge"));
        b.setImageUrl(rs.getString("image_url"));
        b.setLinkUrl(rs.getString("link_url"));
        b.setSortOrder(rs.getInt("sort_order"));
        b.setStatus(rs.getInt("status"));
        return b;
    }

    public List<Banner> findActiveBanners() {
        List<Banner> list = new ArrayList<>();
        String sql = "SELECT * FROM banner WHERE status = 1 ORDER BY sort_order ASC, banner_id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public List<Banner> findAll() {
        List<Banner> list = new ArrayList<>();
        String sql = "SELECT * FROM banner ORDER BY sort_order ASC, banner_id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public boolean insert(Banner b) {
        String sql = "INSERT INTO banner (title, subtitle, badge, image_url, link_url, sort_order, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getSubtitle());
            ps.setString(3, b.getBadge());
            ps.setString(4, b.getImageUrl());
            ps.setString(5, b.getLinkUrl());
            ps.setInt(6, b.getSortOrder() != null ? b.getSortOrder() : 1);
            ps.setInt(7, b.getStatus() != null ? b.getStatus() : 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean toggleStatus(int bannerId, int status) {
        String sql = "UPDATE banner SET status = ? WHERE banner_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, bannerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean deleteById(int bannerId) {
        String sql = "DELETE FROM banner WHERE banner_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bannerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }
}
