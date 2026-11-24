package com.mall.servlet.admin;

import com.mall.bean.User;
import com.mall.dao.LogDao;
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
 * 后台管理员登录与安全注销控制器（Servlet），校验管理员角色并记录审计日志。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin", "/admin/login", "/admin/logout"})
public class AdminLoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/logout".equals(servletPath)) {
            req.getSession().removeAttribute("adminUser");
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        if ("/admin".equals(servletPath)) {
            User admin = (User) req.getSession().getAttribute("adminUser");
            if (admin != null && admin.getRole() != null && admin.getRole() == 1) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");

        if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(password)) {
            req.setAttribute("errorMsg", "请输入管理员账号与密码");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        User user = userDao.login(userName.trim(), MD5Util.md5(password));
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            req.setAttribute("errorMsg", "管理员账号密码错误，或该账号无后台管理权限");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        req.getSession().setAttribute("adminUser", user);
        logDao.record(user.getUserName(), "系统认证", "管理员 " + user.getUserName() + " 登录后台管理系统", req.getRemoteAddr());
        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
