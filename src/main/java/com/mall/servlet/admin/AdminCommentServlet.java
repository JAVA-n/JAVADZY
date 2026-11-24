package com.mall.servlet.admin;

import com.mall.bean.User;
import com.mall.dao.CommentDao;
import com.mall.dao.LogDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 后台商品评价审核管理控制器（Servlet），支持评论显示/屏蔽状态切换与违规评论清理。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminCommentServlet", urlPatterns = {"/admin/comments"})
public class AdminCommentServlet extends HttpServlet {

    private final CommentDao commentDao = new CommentDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("comments", commentDao.findAllForAdmin());
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("adminMenu", "comments");
        req.getRequestDispatcher("/WEB-INF/views/admin/comment_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String action = req.getParameter("action");
        int commentId = StringUtil.parseInt(req.getParameter("commentId"), 0);
        String msg = "";

        if ("toggle".equals(action) && commentId > 0) {
            int newStatus = StringUtil.parseInt(req.getParameter("newStatus"), 1);
            if (commentDao.updateStatus(commentId, newStatus)) {
                logDao.record(admin.getUserName(), "评论管理", "审核设置评价 ID=" + commentId + " 为" + (newStatus == 1 ? "公开显示" : "隐藏屏蔽"), req.getRemoteAddr());
                msg = "评价审核状态已更新";
            }
        } else if ("delete".equals(action) && commentId > 0) {
            if (commentDao.deleteById(commentId)) {
                logDao.record(admin.getUserName(), "评论管理", "删除违规商品评价 ID=" + commentId, req.getRemoteAddr());
                msg = "评价记录已删除";
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/comments"
                + (StringUtil.isNotEmpty(msg) ? "?msg=" + URLEncoder.encode(msg, "UTF-8") : ""));
    }
}
