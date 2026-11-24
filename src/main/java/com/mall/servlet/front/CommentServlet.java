package com.mall.servlet.front;

import com.mall.bean.User;
import com.mall.dao.CommentDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 前台商品评价提交控制器（Servlet），允许登录用户对商品发表星级与文字评价。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "CommentServlet", urlPatterns = {"/comment"})
public class CommentServlet extends HttpServlet {

    private final CommentDao commentDao = new CommentDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
        int rating = StringUtil.parseInt(req.getParameter("rating"), 5);
        String content = req.getParameter("content");

        if (productId > 0 && StringUtil.isNotEmpty(content)) {
            String clean = content.trim();
            if (clean.length() > 400) {
                clean = clean.substring(0, 400);
            }
            commentDao.insert(productId, loginUser.getUserId(), rating, clean);
            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&commented=1#reviews");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
