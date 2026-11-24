package com.mall.servlet.front;

import com.mall.bean.User;
import com.mall.dao.CategoryDao;
import com.mall.dao.FavoriteDao;
import com.mall.dao.OrderDao;
import com.mall.dao.UserDao;
import com.mall.util.MD5Util;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 前台个人中心与收货地址管理控制器（Servlet），支持修改个人资料、默认收货地址与登录密码。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "UserCenterServlet", urlPatterns = {"/user"})
public class UserCenterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final OrderDao orderDao = new OrderDao();
    private final FavoriteDao favoriteDao = new FavoriteDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("loginUser");
        User freshUser = userDao.findById(sessionUser.getUserId());
        if (freshUser != null) {
            req.getSession().setAttribute("loginUser", freshUser);
        }
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("orderCount", orderDao.findByUserId(sessionUser.getUserId(), null).size());
        req.setAttribute("favCount", favoriteDao.findByUserId(sessionUser.getUserId()).size());
        req.setAttribute("navActive", "user");
        req.getRequestDispatcher("/WEB-INF/views/front/user_center.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        String action = req.getParameter("action");

        if ("password".equals(action)) {
            String oldPwd = req.getParameter("oldPassword");
            String newPwd = req.getParameter("newPassword");
            User dbUser = userDao.findById(loginUser.getUserId());
            if (dbUser == null || !dbUser.getPassword().equals(MD5Util.md5(oldPwd))) {
                req.setAttribute("errorMsg", "原密码输入不正确");
            } else if (StringUtil.isEmpty(newPwd) || newPwd.length() < 6) {
                req.setAttribute("errorMsg", "新密码长度不能少于 6 位");
            } else {
                userDao.updatePassword(loginUser.getUserId(), MD5Util.md5(newPwd));
                req.setAttribute("successMsg", "登录密码修改成功！");
            }
        } else {
            User updated = new User();
            updated.setUserId(loginUser.getUserId());
            updated.setRealName(req.getParameter("realName"));
            updated.setEmail(req.getParameter("email"));
            updated.setPhone(req.getParameter("phone"));
            updated.setAddress(req.getParameter("address"));
            if (userDao.updateProfile(updated)) {
                User refreshed = userDao.findById(loginUser.getUserId());
                req.getSession().setAttribute("loginUser", refreshed);
                req.setAttribute("successMsg", "个人资料与默认收货地址已保存！");
            } else {
                req.setAttribute("errorMsg", "保存失败，请检查填写内容");
            }
        }
        doGet(req, resp);
    }
}
