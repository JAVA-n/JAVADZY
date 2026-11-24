package com.mall.dao;

import com.mall.bean.Category;
import com.mall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类数据访问类（DAO），负责数码配件分类列表查询及后台分类增删改查。
 *
 * @author 张三
 * @studentId 20240301
 */
public class CategoryDao {

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM product p WHERE p.category_id = c.category_id) AS product_count "
                + "FROM category c ORDER BY c.sort_order ASC, c.category_id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setCategoryDesc(rs.getString("category_desc"));
                c.setSortOrder(rs.getInt("sort_order"));
                c.setParentId(rs.getInt("parent_id"));
                c.setProductCount(rs.getInt("product_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public Category findById(int categoryId) {
        String sql = "SELECT * FROM category WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setCategoryDesc(rs.getString("category_desc"));
                c.setSortOrder(rs.getInt("sort_order"));
                c.setParentId(rs.getInt("parent_id"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    public boolean insert(Category c) {
        String sql = "INSERT INTO category (category_name, category_desc, sort_order, parent_id) VALUES (?, ?, ?, 0)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getCategoryDesc());
            ps.setInt(3, c.getSortOrder() != null ? c.getSortOrder() : 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean update(Category c) {
        String sql = "UPDATE category SET category_name = ?, category_desc = ?, sort_order = ? WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getCategoryDesc());
            ps.setInt(3, c.getSortOrder() != null ? c.getSortOrder() : 1);
            ps.setInt(4, c.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public int countProductsInCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM product WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return 0;
    }

    public boolean deleteById(int categoryId) {
        String sql = "DELETE FROM category WHERE category_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }
}
