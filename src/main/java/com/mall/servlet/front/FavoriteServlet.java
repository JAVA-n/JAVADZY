package com.mall.servlet.front;

import com.mall.bean.Favorite;
import com.mall.bean.User;
import com.mall.dao.CategoryDao;
import com.mall.dao.FavoriteDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 前台商品收藏夹控制器（Servlet），实现商品一键收藏、取消收藏及个人收藏列表查看。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "FavoriteServlet", urlPatterns = {"/favorite"})
public class FavoriteServlet extends HttpServlet {

    private final FavoriteDao favoriteDao = new FavoriteDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        List<Favorite> favorites = favoriteDao.findByUserId(loginUser.getUserId());
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("favorites", favorites);
        req.setAttribute("navActive", "favorite");
        req.getRequestDispatcher("/WEB-INF/views/front/favorites.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
        String action = req.getParameter("action");
        String from = req.getParameter("from");

        if (productId > 0) {
            if ("remove".equals(action)) {
                favoriteDao.removeFavorite(loginUser.getUserId(), productId);
            } else if ("toggle".equals(action)) {
                if (favoriteDao.isFavorited(loginUser.getUserId(), productId)) {
                    favoriteDao.removeFavorite(loginUser.getUserId(), productId);
                } else {
                    favoriteDao.addFavorite(loginUser.getUserId(), productId);
                }
            } else {
                favoriteDao.addFavorite(loginUser.getUserId(), productId);
            }
        }

        if ("detail".equals(from) && productId > 0) {
            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/favorite");
        }
    }
}
