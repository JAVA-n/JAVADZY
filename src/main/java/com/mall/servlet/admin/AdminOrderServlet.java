package com.mall.servlet.admin;

import com.mall.bean.Order;
import com.mall.bean.User;
import com.mall.dao.LogDao;
import com.mall.dao.OrderDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 后台订单管理控制器（Servlet），支持按订单状态筛选、查看明细与发货/完结状态流转。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/orders"})
public class AdminOrderServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int stateFilter = StringUtil.parseInt(req.getParameter("state"), -1);
        String keyword = req.getParameter("keyword");
        List<Order> orders = orderDao.findAllForAdmin(stateFilter >= 0 ? stateFilter : null, keyword);

        req.setAttribute("orders", orders);
        req.setAttribute("stateFilter", stateFilter);
        req.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("adminMenu", "orders");
        req.getRequestDispatcher("/WEB-INF/views/admin/order_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String orderId = req.getParameter("orderId");
        int newState = StringUtil.parseInt(req.getParameter("newState"), -1);
        String msg = "";

        if (StringUtil.isNotEmpty(orderId) && newState >= 0 && newState <= 3) {
            if (orderDao.updateOrderState(orderId, newState)) {
                String stateText = newState == 1 ? "已发货" : (newState == 2 ? "已完成" : (newState == 3 ? "已取消" : "待发货"));
                logDao.record(admin.getUserName(), "订单管理", "更新订单 [" + orderId + "] 状态为：" + stateText, req.getRemoteAddr());
                msg = "订单 " + orderId + " 状态已更新为【" + stateText + "】";
            }
        }
        String target = req.getContextPath() + "/admin/orders";
        if (StringUtil.isNotEmpty(msg)) {
            target += "?msg=" + URLEncoder.encode(msg, "UTF-8");
        }
        resp.sendRedirect(target);
    }
}
