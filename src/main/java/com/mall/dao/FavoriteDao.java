package com.mall.dao;

import com.mall.bean.Favorite;
import com.mall.bean.Product;
import com.mall.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品收藏数据访问类（DAO），实现用户收藏夹的查询、新增与取消收藏。
 *
 * @author 张三
 * @studentId 20240301
 */
public class FavoriteDao {

    public boolean isFavorited(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM favorite WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return false;
    }

    public boolean addFavorite(int userId, int productId) {
        if (isFavorited(userId, productId)) {
            return true;
        }
        String sql = "INSERT INTO favorite (user_id, product_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean removeFavorite(int userId, int productId) {
        String sql = "DELETE FROM favorite WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public List<Favorite> findByUserId(int userId) {
        List<Favorite> list = new ArrayList<>();
        String sql = "SELECT f.fav_id, f.user_id, f.product_id, f.create_time, "
                + "p.product_name, p.subtitle, p.price, p.discount, p.stock, p.sales, p.image, p.status "
                + "FROM favorite f INNER JOIN product p ON f.product_id = p.product_id "
                + "WHERE f.user_id = ? ORDER BY f.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Favorite fav = new Favorite();
                fav.setFavId(rs.getInt("fav_id"));
                fav.setUserId(rs.getInt("user_id"));
                fav.setProductId(rs.getInt("product_id"));
                fav.setCreateTime(rs.getTimestamp("create_time"));

                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setSubtitle(rs.getString("subtitle"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setDiscount(rs.getBigDecimal("discount"));
                p.setStock(rs.getInt("stock"));
                p.setSales(rs.getInt("sales"));
                p.setImage(rs.getString("image"));
                p.setStatus(rs.getInt("status"));
                fav.setProduct(p);
                list.add(fav);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }
}
