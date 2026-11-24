package com.mall.servlet.front;

import com.mall.bean.Cart;
import com.mall.bean.Product;
import com.mall.dao.CategoryDao;
import com.mall.dao.ProductDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 购物车管理控制器（Servlet），基于 HttpSession 维护商品加入、数量增减、单项移除与清空计算。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();

    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (StringUtil.isNotEmpty(action) && !"view".equals(action)) {
            handleCartAction(req, resp, action);
            return;
        }
        getOrCreateCart(req.getSession());
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("navActive", "cart");
        req.getRequestDispatcher("/WEB-INF/views/front/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        handleCartAction(req, resp, StringUtil.isEmpty(action) ? "add" : action);
    }

    private void handleCartAction(HttpServletRequest req, HttpServletResponse resp, String action) throws IOException {
        Cart cart = getOrCreateCart(req.getSession());
        int productId = StringUtil.parseInt(req.getParameter("productId"), 0);

        if ("add".equals(action)) {
            int quantity = StringUtil.parseInt(req.getParameter("quantity"), 1);
            Product product = productDao.findById(productId);
            if (product != null && product.getStatus() != null && product.getStatus() == 1 && product.getStock() > 0) {
                cart.addItem(product, quantity);
            }
            if ("1".equals(req.getParameter("buyNow"))) {
                resp.sendRedirect(req.getContextPath() + "/checkout");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/cart?added=1");
            return;
        } else if ("update".equals(action)) {
            int quantity = StringUtil.parseInt(req.getParameter("quantity"), 1);
            Product latest = productDao.findById(productId);
            if (latest != null && cart.getItems().containsKey(productId)) {
                cart.getItems().get(productId).setProduct(latest);
            }
            cart.updateQuantity(productId, quantity);
        } else if ("remove".equals(action)) {
            cart.removeItem(productId);
        } else if ("clear".equals(action)) {
            cart.clear();
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
