package com.mall.servlet.admin;

import com.mall.bean.Banner;
import com.mall.bean.User;
import com.mall.dao.BannerDao;
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
 * 后台轮播广告管理控制器（Servlet），维护前台首页轮播海报的新增、启停与移除。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminBannerServlet", urlPatterns = {"/admin/banners"})
public class AdminBannerServlet extends HttpServlet {

    private final BannerDao bannerDao = new BannerDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("banners", bannerDao.findAll());
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("adminMenu", "banners");
        req.getRequestDispatcher("/WEB-INF/views/admin/banner_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String action = req.getParameter("action");
        String msg = "";

        if ("add".equals(action)) {
            Banner b = new Banner();
            b.setTitle(req.getParameter("title"));
            b.setSubtitle(req.getParameter("subtitle"));
            b.setBadge(req.getParameter("badge"));
            String img = req.getParameter("imageUrl");
            b.setImageUrl(StringUtil.isNotEmpty(img) ? img.trim() : "static/images/banner_audio.jpg");
            String link = req.getParameter("linkUrl");
            b.setLinkUrl(StringUtil.isNotEmpty(link) ? link.trim() : "products");
            b.setSortOrder(StringUtil.parseInt(req.getParameter("sortOrder"), 1));
            b.setStatus(1);
            if (StringUtil.isNotEmpty(b.getTitle()) && bannerDao.insert(b)) {
                logDao.record(admin.getUserName(), "轮播图管理", "新增首页轮播广告 [" + b.getTitle() + "]", req.getRemoteAddr());
                msg = "新轮播海报已添加并启用";
            }
        } else if ("toggle".equals(action)) {
            int bannerId = StringUtil.parseInt(req.getParameter("bannerId"), 0);
            int newStatus = StringUtil.parseInt(req.getParameter("newStatus"), 1);
            if (bannerDao.toggleStatus(bannerId, newStatus)) {
                logDao.record(admin.getUserName(), "轮播图管理", "切换轮播广告 ID=" + bannerId + " 状态为 " + (newStatus == 1 ? "启用" : "停用"), req.getRemoteAddr());
                msg = "轮播状态已更新";
            }
        } else if ("delete".equals(action)) {
            int bannerId = StringUtil.parseInt(req.getParameter("bannerId"), 0);
            if (bannerDao.deleteById(bannerId)) {
                logDao.record(admin.getUserName(), "轮播图管理", "删除轮播广告 ID=" + bannerId, req.getRemoteAddr());
                msg = "轮播广告已删除";
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/banners"
                + (StringUtil.isNotEmpty(msg) ? "?msg=" + URLEncoder.encode(msg, "UTF-8") : ""));
    }
}
