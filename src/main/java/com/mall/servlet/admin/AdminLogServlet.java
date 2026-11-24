package com.mall.servlet.admin;

import com.mall.dao.LogDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台系统操作审计日志查看控制器（Servlet）。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminLogServlet", urlPatterns = {"/admin/logs"})
public class AdminLogServlet extends HttpServlet {

    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("logs", logDao.findLatest(100));
        req.setAttribute("adminMenu", "logs");
        req.getRequestDispatcher("/WEB-INF/views/admin/log_manage.jsp").forward(req, resp);
    }
}
