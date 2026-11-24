package com.mall.servlet.admin;

import com.mall.bean.Order;
import com.mall.bean.Product;
import com.mall.dao.LogDao;
import com.mall.dao.OrderDao;
import com.mall.dao.ProductDao;
import com.mall.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台运营概览与销售统计报表控制器（Servlet），汇总核心经营指标、分类销售占比与每日营收趋势。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();
    private final ProductDao productDao = new ProductDao();
    private final UserDao userDao = new UserDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BigDecimal totalRevenue = orderDao.sumTotalRevenue();
        int totalOrders = orderDao.countAllOrders();
        int totalProducts = productDao.countAllProducts();
        int totalCustomers = userDao.countCustomers();

        Map<String, BigDecimal> categorySales = orderDao.sumSalesByCategory();
        Map<String, BigDecimal> dailyRevenue = orderDao.sumDailyRevenue(7);

        List<Order> allOrders = orderDao.findAllForAdmin(null, null);
        List<Order> recentOrders = allOrders.size() > 5 ? allOrders.subList(0, 5) : allOrders;
        int pendingOrderCount = 0;
        for (Order o : allOrders) {
            if (o.getOrderState() != null && o.getOrderState() == 0) {
                pendingOrderCount++;
            }
        }

        List<Product> allProducts = productDao.findAllForAdmin(null, null, null);
        List<Product> lowStockProducts = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getStock() != null && p.getStock() <= 45) {
                lowStockProducts.add(p);
            }
        }

        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("pendingOrderCount", pendingOrderCount);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("categorySales", categorySales);
        req.setAttribute("dailyRevenue", dailyRevenue);
        req.setAttribute("recentOrders", recentOrders);
        req.setAttribute("lowStockProducts", lowStockProducts);
        req.setAttribute("recentLogs", logDao.findLatest(5));
        req.setAttribute("adminMenu", "dashboard");
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
