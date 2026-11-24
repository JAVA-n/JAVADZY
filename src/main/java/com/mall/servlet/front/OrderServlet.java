package com.mall.servlet.front;

import com.mall.bean.Cart;
import com.mall.bean.Order;
import com.mall.bean.User;
import com.mall.dao.CategoryDao;
import com.mall.dao.OrderDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 前台订单结算提交、订单历史查询与确认收货控制器（Servlet）。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/checkout", "/orders"})
public class OrderServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        req.setAttribute("categories", categoryDao.findAll());

        if ("/checkout".equals(req.getServletPath())) {
            Cart cart = (Cart) req.getSession().getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            req.setAttribute("navActive", "cart");
            req.getRequestDispatcher("/WEB-INF/views/front/checkout.jsp").forward(req, resp);
            return;
        }

        // /orders 历史订单查询与状态跟踪
        int stateFilter = StringUtil.parseInt(req.getParameter("state"), -1);
        List<Order> orders = orderDao.findByUserId(loginUser.getUserId(), stateFilter >= 0 ? stateFilter : null);
        req.setAttribute("orders", orders);
        req.setAttribute("stateFilter", stateFilter);
        req.setAttribute("createdOrderId", req.getParameter("created"));
        req.setAttribute("navActive", "orders");
        req.getRequestDispatcher("/WEB-INF/views/front/order_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");

        if ("/checkout".equals(req.getServletPath())) {
            Cart cart = (Cart) req.getSession().getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            String consignee = req.getParameter("consignee");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            String remark = req.getParameter("remark");

            if (StringUtil.isEmpty(consignee) || StringUtil.isEmpty(phone) || StringUtil.isEmpty(address)) {
                req.setAttribute("errorMsg", "请完整填写收货人姓名、联系电话和详细收货地址");
                req.setAttribute("categories", categoryDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/front/checkout.jsp").forward(req, resp);
                return;
            }

            try {
                String orderId = orderDao.createOrderWithTransaction(
                        loginUser.getUserId(), consignee.trim(), phone.trim(), address.trim(),
                        remark != null ? remark.trim() : "", cart);
                // 下单成功后清空 Session 购物车
                cart.clear();
                resp.sendRedirect(req.getContextPath() + "/orders?created=" + orderId);
            } catch (SQLException e) {
                req.setAttribute("errorMsg", "提交订单失败：" + e.getMessage());
                req.setAttribute("categories", categoryDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/front/checkout.jsp").forward(req, resp);
            }
            return;
        }

        // 用户端确认收货或取消未发货订单
        String action = req.getParameter("action");
        String orderId = req.getParameter("orderId");
        Order existing = orderDao.findByOrderId(orderId);
        if (existing != null && existing.getUserId().equals(loginUser.getUserId())) {
            if ("confirm".equals(action) && existing.getOrderState() == 1) {
                orderDao.updateOrderState(orderId, 2);
            } else if ("cancel".equals(action) && existing.getOrderState() == 0) {
                orderDao.updateOrderState(orderId, 3);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/orders");
    }
}
