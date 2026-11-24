package com.mall.dao;

import com.mall.bean.PageBean;
import com.mall.bean.Product;
import com.mall.util.DBUtil;
import com.mall.util.StringUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品数据访问类（DAO），支持多条件模糊搜索、分类筛选、分页浏览、折扣维护与上下架管理。
 *
 * @author 张三
 * @studentId 20240301
 */
public class ProductDao {

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setSubtitle(rs.getString("subtitle"));
        p.setCategoryId(rs.getInt("category_id"));
        try {
            p.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {
        }
        p.setPrice(rs.getBigDecimal("price"));
        p.setDiscount(rs.getBigDecimal("discount"));
        p.setStock(rs.getInt("stock"));
        p.setSales(rs.getInt("sales"));
        p.setImage(rs.getString("image"));
        p.setSpecs(rs.getString("specs"));
        p.setDescription(rs.getString("description"));
        p.setIsHot(rs.getInt("is_hot"));
        p.setStatus(rs.getInt("status"));
        p.setCreateTime(rs.getTimestamp("create_time"));
        return p;
    }

    /**
     * 前台商品多条件分页查询（仅查询已上架商品 status = 1）
     */
    public PageBean<Product> findPageForFront(Integer categoryId, String keyword, boolean discountOnly,
                                              String sort, int page, int pageSize) {
        StringBuilder where = new StringBuilder(" FROM product p LEFT JOIN category c ON p.category_id = c.category_id WHERE p.status = 1");
        List<Object> params = new ArrayList<>();
        if (categoryId != null && categoryId > 0) {
            where.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            where.append(" AND (p.product_name LIKE ? OR p.subtitle LIKE ? OR p.specs LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (discountOnly) {
            where.append(" AND p.discount < 0.99");
        }

        int totalCount = 0;
        Connection conn = null;
        PreparedStatement psCount = null;
        PreparedStatement psList = null;
        ResultSet rsCount = null;
        ResultSet rsList = null;
        List<Product> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            psCount = conn.prepareStatement("SELECT COUNT(*)" + where);
            for (int i = 0; i < params.size(); i++) {
                psCount.setObject(i + 1, params.get(i));
            }
            rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                totalCount = rsCount.getInt(1);
            }

            int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / pageSize));
            int safePage = Math.min(Math.max(1, page), totalPages);
            int offset = (safePage - 1) * pageSize;

            String orderBy = " ORDER BY p.is_hot DESC, p.sales DESC, p.product_id ASC";
            if ("sales".equals(sort)) {
                orderBy = " ORDER BY p.sales DESC, p.product_id ASC";
            } else if ("price_asc".equals(sort)) {
                orderBy = " ORDER BY (p.price * p.discount) ASC";
            } else if ("price_desc".equals(sort)) {
                orderBy = " ORDER BY (p.price * p.discount) DESC";
            } else if ("newest".equals(sort)) {
                orderBy = " ORDER BY p.product_id DESC";
            }

            String listSql = "SELECT p.*, c.category_name" + where + orderBy + " LIMIT ? OFFSET ?";
            psList = conn.prepareStatement(listSql);
            int idx = 1;
            for (Object param : params) {
                psList.setObject(idx++, param);
            }
            psList.setInt(idx++, pageSize);
            psList.setInt(idx, offset);
            rsList = psList.executeQuery();
            while (rsList.next()) {
                list.add(mapRow(rsList));
            }
            return new PageBean<>(safePage, pageSize, totalCount, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new PageBean<>(1, pageSize, 0, list);
        } finally {
            DBUtil.close(rsCount, psCount, null);
            DBUtil.close(rsList, psList, conn);
        }
    }

    /**
     * 首页查询热销爆款推荐商品
     */
    public List<Product> findHotProducts(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id "
                + "WHERE p.status = 1 AND p.is_hot = 1 ORDER BY p.sales DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
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

    /**
     * 首页查询限时特惠折扣商品
     */
    public List<Product> findDiscountProducts(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id "
                + "WHERE p.status = 1 AND p.discount < 0.99 ORDER BY p.discount ASC, p.sales DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
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

    public Product findById(int productId) {
        String sql = "SELECT p.*, c.category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id WHERE p.product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    /**
     * 后台查询全部商品（含上架/下架、分类筛选与名称检索）
     */
    public List<Product> findAllForAdmin(Integer categoryId, Integer status, String keyword) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (status != null && (status == 0 || status == 1)) {
            sql.append(" AND p.status = ?");
            params.add(status);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            sql.append(" AND p.product_name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        sql.append(" ORDER BY p.product_id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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

    public boolean insert(Product p) {
        String sql = "INSERT INTO product (product_name, subtitle, category_id, price, discount, stock, sales, image, specs, description, is_hot, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getSubtitle());
            ps.setInt(3, p.getCategoryId());
            ps.setBigDecimal(4, p.getPrice());
            ps.setBigDecimal(5, p.getDiscount() != null ? p.getDiscount() : BigDecimal.ONE);
            ps.setInt(6, p.getStock() != null ? p.getStock() : 50);
            ps.setString(7, StringUtil.isNotEmpty(p.getImage()) ? p.getImage() : "static/images/prod_gan140.jpg");
            ps.setString(8, p.getSpecs());
            ps.setString(9, p.getDescription());
            ps.setInt(10, p.getIsHot() != null ? p.getIsHot() : 0);
            ps.setInt(11, p.getStatus() != null ? p.getStatus() : 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean update(Product p) {
        String sql = "UPDATE product SET product_name=?, subtitle=?, category_id=?, price=?, discount=?, stock=?, image=?, specs=?, description=?, is_hot=?, status=? WHERE product_id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getProductName());
            ps.setString(2, p.getSubtitle());
            ps.setInt(3, p.getCategoryId());
            ps.setBigDecimal(4, p.getPrice());
            ps.setBigDecimal(5, p.getDiscount());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getImage());
            ps.setString(8, p.getSpecs());
            ps.setString(9, p.getDescription());
            ps.setInt(10, p.getIsHot());
            ps.setInt(11, p.getStatus());
            ps.setInt(12, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean updateStatus(int productId, int status) {
        String sql = "UPDATE product SET status = ? WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean updateDiscount(int productId, BigDecimal discount, int isHot) {
        String sql = "UPDATE product SET discount = ?, is_hot = ? WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, discount);
            ps.setInt(2, isHot);
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public boolean deleteById(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public int countAllProducts() {
        String sql = "SELECT COUNT(*) FROM product";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
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
}
