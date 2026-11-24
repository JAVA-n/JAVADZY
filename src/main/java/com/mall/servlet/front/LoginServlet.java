package com.mall.servlet.front;

import com.mall.bean.User;
import com.mall.dao.UserDao;
import com.mall.util.MD5Util;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 前台用户登录与安全退出控制器（Servlet），支持身份校验、Session 会话管理与 Cookie 记住用户名。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/logout".equals(req.getServletPath())) {
            req.getSession().removeAttribute("loginUser");
            resp.sendRedirect(req.getContextPath() + "/index");
            return;
        }

        String remembered = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberUser".equals(cookie.getName())) {
                    remembered = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    break;
                }
            }
        }
        req.setAttribute("rememberedUser", remembered);
        req.setAttribute("redirect", req.getParameter("redirect"));
        req.getRequestDispatcher("/WEB-INF/views/front/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String remember = req.getParameter("remember");
        String redirect = req.getParameter("redirect");

        if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(password)) {
            req.setAttribute("errorMsg", "请输入登录用户名和密码");
            req.setAttribute("rememberedUser", userName);
            req.getRequestDispatcher("/WEB-INF/views/front/login.jsp").forward(req, resp);
            return;
        }

        User user = userDao.login(userName.trim(), MD5Util.md5(password));
        if (user == null) {
            req.setAttribute("errorMsg", "用户名或密码错误，请重新输入");
            req.setAttribute("rememberedUser", userName);
            req.getRequestDispatcher("/WEB-INF/views/front/login.jsp").forward(req, resp);
            return;
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            req.setAttribute("errorMsg", "当前账号已被冻结禁用，请联系管理员");
            req.getRequestDispatcher("/WEB-INF/views/front/login.jsp").forward(req, resp);
            return;
        }

        // 登录成功，将用户对象写入 Session
        req.getSession().setAttribute("loginUser", user);

        // 处理“记住用户名” Cookie（有效期 7 天）
        Cookie cookie = new Cookie("rememberUser", URLEncoder.encode(user.getUserName(), "UTF-8"));
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        cookie.setMaxAge("1".equals(remember) ? 7 * 24 * 3600 : 0);
        resp.addCookie(cookie);

        if (StringUtil.isNotEmpty(redirect) && redirect.startsWith("/")) {
            resp.sendRedirect(req.getContextPath() + redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/index");
        }
    }
}
