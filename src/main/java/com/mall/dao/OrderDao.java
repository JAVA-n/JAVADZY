package com.mall.dao;

import com.mall.bean.Cart;
import com.mall.bean.CartItem;
import com.mall.bean.Order;
import com.mall.bean.OrderItem;
import com.mall.util.DBUtil;
import com.mall.util.StringUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问类（DAO），使用 JDBC 手动事务保证订单主表、明细表与商品库存同步扣减的原子性，并提供统计报表查询。
 *
 * @author 张三
 * @studentId 20240301
 */
public class OrderDao {

    /**
     * 在同一个数据库事务中创建订单主表记录、批量写入订单明细并扣减库存与累加销量
     *
     * @return 成功返回生成的订单号，库存不足或失败抛出异常或返回 null
     */
    public String createOrderWithTransaction(int userId, String consignee, String phone,
                                             String address, String remark, Cart cart) throws SQLException {
        if (cart == null || cart.isEmpty()) {
            throw new SQLException("购物车为空，无法生成订单");
        }
        String orderId = StringUtil.generateOrderId();
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        PreparedStatement psStock = null;

        try {
            conn = DBUtil.getConnection();
            // 开启手动事务，防止主从表写入不一致
            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO orders (order_id, user_id, total_price, order_state, consignee, phone, address, remark, create_time, update_time) "
                    + "VALUES (?, ?, ?, 0, ?, ?, ?, ?, ?, ?)";
            Timestamp now = new Timestamp(System.currentTimeMillis());
            psOrder = conn.prepareStatement(sqlOrder);
            psOrder.setString(1, orderId);
            psOrder.setInt(2, userId);
            psOrder.setBigDecimal(3, cart.getTotalAmount());
            psOrder.setString(4, consignee);
            psOrder.setString(5, phone);
            psOrder.setString(6, address);
            psOrder.setString(7, remark);
            psOrder.setTimestamp(8, now);
            psOrder.setTimestamp(9, now);
            psOrder.executeUpdate();

            String sqlItem = "INSERT INTO order_item (order_id, product_id, product_name, product_image, unit_price, quantity, subtotal) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            psItem = conn.prepareStatement(sqlItem);

            String sqlStock = "UPDATE product SET stock = stock - ?, sales = sales + ? WHERE product_id = ? AND stock >= ?";
            psStock = conn.prepareStatement(sqlStock);

            for (CartItem ci : cart.getCartItems()) {
                // 校验并扣减商品库存
                psStock.setInt(1, ci.getQuantity());
                psStock.setInt(2, ci.getQuantity());
                psStock.setInt(3, ci.getProduct().getProductId());
                psStock.setInt(4, ci.getQuantity());
                int updated = psStock.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("商品【" + ci.getProduct().getProductName() + "】库存不足");
                }

                // 写入订单商品快照与折后成交单价
                psItem.setString(1, orderId);
                psItem.setInt(2, ci.getProduct().getProductId());
                psItem.setString(3, ci.getProduct().getProductName());
                psItem.setString(4, ci.getProduct().getImage());
                psItem.setBigDecimal(5, ci.getProduct().getSalePrice());
                psItem.setInt(6, ci.getQuantity());
                psItem.setBigDecimal(7, ci.getSubtotal());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            DBUtil.close(null, psStock, null);
            DBUtil.close(null, psItem, null);
            DBUtil.close(null, psOrder, conn);
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getString("order_id"));
        o.setUserId(rs.getInt("user_id"));
        try {
            o.setUserName(rs.getString("user_name"));
        } catch (SQLException ignored) {
        }
        o.setTotalPrice(rs.getBigDecimal("total_price"));
        o.setOrderState(rs.getInt("order_state"));
        o.setConsignee(rs.getString("consignee"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setRemark(rs.getString("remark"));
        o.setCreateTime(rs.getTimestamp("create_time"));
        o.setUpdateTime(rs.getTimestamp("update_time"));
        return o;
    }

    private List<OrderItem> findItemsByOrderId(Connection conn, String orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ? ORDER BY item_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setOrderId(rs.getString("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * 查询指定用户的全部订单（支持按订单状态筛选，按时间倒序排列）
     */
    public List<Order> findByUserId(int userId, Integer stateFilter) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.user_name FROM orders o LEFT JOIN users u ON o.user_id = u.user_id WHERE o.user_id = ?");
        if (stateFilter != null && stateFilter >= 0) {
            sql.append(" AND o.order_state = ?");
        }
        sql.append(" ORDER BY o.create_time DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setInt(1, userId);
            if (stateFilter != null && stateFilter >= 0) {
                ps.setInt(2, stateFilter);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setItems(findItemsByOrderId(conn, order.getOrderId()));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    public Order findByOrderId(String orderId) {
        String sql = "SELECT o.*, u.user_name FROM orders o LEFT JOIN users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Order o = mapOrder(rs);
                o.setItems(findItemsByOrderId(conn, orderId));
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    /**
     * 后台查询订单列表（支持按状态、订单号或收货人模糊检索）
     */
    public List<Order> findAllForAdmin(Integer stateFilter, String keyword) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT o.*, u.user_name FROM orders o LEFT JOIN users u ON o.user_id = u.user_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (stateFilter != null && stateFilter >= 0) {
            sql.append(" AND o.order_state = ?");
            params.add(stateFilter);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            sql.append(" AND (o.order_id LIKE ? OR o.consignee LIKE ? OR o.phone LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY o.create_time DESC");

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
                Order o = mapOrder(rs);
                o.setItems(findItemsByOrderId(conn, o.getOrderId()));
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /**
     * 更新订单流转状态并同步更新时间
     */
    public boolean updateOrderState(String orderId, int newState) {
        String sql = "UPDATE orders SET order_state = ?, update_time = ? WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, newState);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(null, ps, conn);
        }
        return false;
    }

    public BigDecimal sumTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM orders WHERE order_state <> 3";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return BigDecimal.ZERO;
    }

    public int countAllOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
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

    /**
     * 统计各数码分类的累计销售额（用于后台数据报表饼图/柱图展示）
     */
    public Map<String, BigDecimal> sumSalesByCategory() {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        String sql = "SELECT c.category_name, COALESCE(SUM(oi.subtotal), 0) AS cat_total "
                + "FROM category c "
                + "LEFT JOIN product p ON c.category_id = p.category_id "
                + "LEFT JOIN order_item oi ON p.product_id = oi.product_id "
                + "GROUP BY c.category_id, c.category_name, c.sort_order "
                + "ORDER BY c.sort_order ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("category_name"), rs.getBigDecimal("cat_total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return result;
    }

    /**
     * 统计最近各日期订单销售额趋势（用于后台折线/柱状趋势图）
     */
    public Map<String, BigDecimal> sumDailyRevenue(int limitDays) {
        Map<String, BigDecimal> raw = new LinkedHashMap<>();
        String sql = "SELECT SUBSTRING(CAST(create_time AS CHAR(19)), 6, 5) AS day_label, SUM(total_price) AS day_sum "
                + "FROM orders WHERE order_state <> 3 "
                + "GROUP BY SUBSTRING(CAST(create_time AS CHAR(19)), 6, 5) "
                + "ORDER BY day_label ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Map.Entry<String, BigDecimal>> entries = new ArrayList<>();
            while (rs.next()) {
                raw.put(rs.getString("day_label"), rs.getBigDecimal("day_sum"));
            }
            entries.addAll(raw.entrySet());
            int start = Math.max(0, entries.size() - limitDays);
            Map<String, BigDecimal> sliced = new LinkedHashMap<>();
            for (int i = start; i < entries.size(); i++) {
                sliced.put(entries.get(i).getKey(), entries.get(i).getValue());
            }
            return sliced;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return raw;
    }
}
