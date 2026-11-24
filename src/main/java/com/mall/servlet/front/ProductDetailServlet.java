package com.mall.servlet.front;

import com.mall.bean.Comment;
import com.mall.bean.PageBean;
import com.mall.bean.Product;
import com.mall.bean.User;
import com.mall.dao.CategoryDao;
import com.mall.dao.CommentDao;
import com.mall.dao.FavoriteDao;
import com.mall.dao.ProductDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 前台商品详情页控制器（Servlet），展示规格参数、折后价、用户评价列表与收藏状态。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final CommentDao commentDao = new CommentDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int productId = StringUtil.parseInt(req.getParameter("id"), 0);
        Product product = productDao.findById(productId);
        if (product == null || (product.getStatus() != null && product.getStatus() == 0)) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        List<Comment> comments = commentDao.findApprovedByProductId(productId);
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        boolean isFavorited = loginUser != null && favoriteDao.isFavorited(loginUser.getUserId(), productId);

        PageBean<Product> relatedPage = productDao.findPageForFront(product.getCategoryId(), null, false, "sales", 1, 4);

        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("product", product);
        req.setAttribute("comments", comments);
        req.setAttribute("isFavorited", isFavorited);
        req.setAttribute("relatedProducts", relatedPage.getList());
        req.setAttribute("navActive", "products");
        req.getRequestDispatcher("/WEB-INF/views/front/product_detail.jsp").forward(req, resp);
    }
}
