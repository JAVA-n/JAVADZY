package com.mall.dao;

import com.mall.bean.Comment;
import com.mall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品评价数据访问类（DAO），支持前台商品评价展示与提交、后台评价审核与删除。
 *
 * @author 张三
 * @studentId 20240301
 */
public class CommentDao {

    public List<Comment> findApprovedByProductId(int productId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.user_name FROM product_comment c "
                + "LEFT JOIN users u ON c.user_id = u.user_id "
                + "WHERE c.product_id = ? AND c.status = 1 ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setProductId(rs.getInt("product_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setUserName(rs.getString("user_name"));
                c.setRating(rs.getInt("rating"));
                c.setContent(rs.getString("content"));
                c.setStatus(rs.getInt("status"));
                c.setCreateTime(rs.getTimestamp("create_time"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public boolean insert(int productId, int userId, int rating, String content) {
        String sql = "INSERT INTO product_comment (product_id, user_id, rating, content, status) VALUES (?, ?, ?, ?, 1)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setInt(3, Math.min(5, Math.max(1, rating)));
            ps.setString(4, content);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public List<Comment> findAllForAdmin() {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.user_name, p.product_name FROM product_comment c "
                + "LEFT JOIN users u ON c.user_id = u.user_id "
                + "LEFT JOIN product p ON c.product_id = p.product_id "
                + "ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setProductId(rs.getInt("product_id"));
                c.setProductName(rs.getString("product_name"));
                c.setUserId(rs.getInt("user_id"));
                c.setUserName(rs.getString("user_name"));
                c.setRating(rs.getInt("rating"));
                c.setContent(rs.getString("content"));
                c.setStatus(rs.getInt("status"));
                c.setCreateTime(rs.getTimestamp("create_time"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public boolean updateStatus(int commentId, int status) {
        String sql = "UPDATE product_comment SET status = ? WHERE comment_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean deleteById(int commentId) {
        String sql = "DELETE FROM product_comment WHERE comment_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }
}
