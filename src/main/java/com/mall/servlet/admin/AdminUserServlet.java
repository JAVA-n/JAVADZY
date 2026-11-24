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
import java.net.URLEncoder;
import java.util.List;

/**
 * 后台注册用户管理控制器（Servlet），实现用户搜索、资料编辑、密码重置、启用禁用与安全删除校验。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        int editId = StringUtil.parseInt(req.getParameter("editId"), 0);
        if (editId > 0) {
            req.setAttribute("editUser", userDao.findById(editId));
        }
        List<User> users = userDao.findAll(keyword);
        req.setAttribute("users", users);
        req.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("err", req.getParameter("err"));
        req.setAttribute("adminMenu", "users");
        req.getRequestDispatcher("/WEB-INF/views/admin/user_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String action = req.getParameter("action");
        int userId = StringUtil.parseInt(req.getParameter("userId"), 0);
        String msg = "";
        String err = "";

        if ("update".equals(action) && userId > 0) {
            User u = userDao.findById(userId);
            if (u != null) {
                u.setRealName(req.getParameter("realName"));
                u.setEmail(req.getParameter("email"));
                u.setPhone(req.getParameter("phone"));
                u.setAddress(req.getParameter("address"));
                u.setStatus(StringUtil.parseInt(req.getParameter("status"), 1));
                userDao.adminUpdateUser(u);
                logDao.record(admin.getUserName(), "用户管理", "更新用户 [" + u.getUserName() + "] 基本资料与状态", req.getRemoteAddr());
                msg = "用户信息已保存更新";
            }
        } else if ("resetPwd".equals(action) && userId > 0) {
            User u = userDao.findById(userId);
            if (u != null && u.getRole() == 0) {
                userDao.updatePassword(userId, MD5Util.md5("123456"));
                logDao.record(admin.getUserName(), "用户管理", "重置用户 [" + u.getUserName() + "] 密码为默认 123456", req.getRemoteAddr());
                msg = "密码已重置为 123456";
            }
        } else if ("delete".equals(action) && userId > 0) {
            User u = userDao.findById(userId);
            if (u != null && u.getRole() == 1) {
                err = "系统管理员账号不可删除";
            } else if (userDao.hasUnfinishedOrders(userId)) {
                err = "该用户存在未完成的在途订单（待发货/已发货），禁止删除以保障业务数据完整";
            } else if (u != null && userDao.deleteById(userId)) {
                logDao.record(admin.getUserName(), "用户管理", "删除用户账号 [" + u.getUserName() + "]", req.getRemoteAddr());
                msg = "已删除指定用户记录";
            }
        }

        String target = req.getContextPath() + "/admin/users";
        if (StringUtil.isNotEmpty(msg)) {
            target += "?msg=" + URLEncoder.encode(msg, "UTF-8");
        } else if (StringUtil.isNotEmpty(err)) {
            target += "?err=" + URLEncoder.encode(err, "UTF-8");
        }
        resp.sendRedirect(target);
    }
}
