package com.mall.servlet.front;

import com.mall.bean.User;
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
 * 前台用户注册控制器（Servlet），负责注册表单数据合法性校验、账号查重与持久化入库。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String confirmPwd = req.getParameter("confirmPassword");
        String realName = req.getParameter("realName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");

        req.setAttribute("formUser", userName);
        req.setAttribute("formRealName", realName);
        req.setAttribute("formEmail", email);
        req.setAttribute("formPhone", phone);
        req.setAttribute("formAddress", address);

        if (StringUtil.isEmpty(userName) || userName.trim().length() < 3 || userName.trim().length() > 20) {
            req.setAttribute("errorMsg", "用户名长度须在 3 ~ 20 个字符之间");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }
        if (StringUtil.isEmpty(password) || password.length() < 6) {
            req.setAttribute("errorMsg", "登录密码长度不能少于 6 位");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }
        if (!password.equals(confirmPwd)) {
            req.setAttribute("errorMsg", "两次输入的密码不一致");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }
        if (StringUtil.isNotEmpty(email) && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            req.setAttribute("errorMsg", "电子邮箱格式不正确");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }
        if (StringUtil.isNotEmpty(phone) && !phone.matches("^1\\d{10}$")) {
            req.setAttribute("errorMsg", "请输入有效的 11 位手机号码");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }
        if (userDao.existsByUserName(userName.trim())) {
            req.setAttribute("errorMsg", "该用户名已被注册，请更换其他账号");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
            return;
        }

        User user = new User();
        user.setUserName(userName.trim());
        user.setPassword(MD5Util.md5(password));
        user.setRealName(StringUtil.isNotEmpty(realName) ? realName.trim() : userName.trim());
        user.setEmail(email != null ? email.trim() : "");
        user.setPhone(phone != null ? phone.trim() : "");
        user.setAddress(address != null ? address.trim() : "");

        if (userDao.register(user)) {
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } else {
            req.setAttribute("errorMsg", "系统繁忙，注册失败请稍后重试");
            req.getRequestDispatcher("/WEB-INF/views/front/register.jsp").forward(req, resp);
        }
    }
}
