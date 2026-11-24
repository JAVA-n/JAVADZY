package com.mall.servlet.admin;

import com.mall.bean.Category;
import com.mall.bean.User;
import com.mall.dao.CategoryDao;
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
 * 后台商品分类管理控制器（Servlet），实现数码配件分类的新增、编辑、排序维护与关联校验删除。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminCategoryServlet", urlPatterns = {"/admin/categories"})
public class AdminCategoryServlet extends HttpServlet {

    private final CategoryDao categoryDao = new CategoryDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int editId = StringUtil.parseInt(req.getParameter("editId"), 0);
        if (editId > 0) {
            req.setAttribute("editCategory", categoryDao.findById(editId));
        }
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("err", req.getParameter("err"));
        req.setAttribute("adminMenu", "categories");
        req.getRequestDispatcher("/WEB-INF/views/admin/category_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String action = req.getParameter("action");
        String msg = "";
        String err = "";

        if ("save".equals(action)) {
            int categoryId = StringUtil.parseInt(req.getParameter("categoryId"), 0);
            String name = req.getParameter("categoryName");
            String desc = req.getParameter("categoryDesc");
            int sortOrder = StringUtil.parseInt(req.getParameter("sortOrder"), 1);

            if (StringUtil.isEmpty(name)) {
                err = "分类名称不能为空";
            } else {
                Category c = new Category();
                c.setCategoryId(categoryId);
                c.setCategoryName(name.trim());
                c.setCategoryDesc(desc != null ? desc.trim() : "");
                c.setSortOrder(sortOrder);
                if (categoryId > 0) {
                    categoryDao.update(c);
                    logDao.record(admin.getUserName(), "分类管理", "修改商品分类 [" + c.getCategoryName() + "]", req.getRemoteAddr());
                    msg = "分类信息更新成功";
                } else {
                    categoryDao.insert(c);
                    logDao.record(admin.getUserName(), "分类管理", "新增商品分类 [" + c.getCategoryName() + "]", req.getRemoteAddr());
                    msg = "新分类创建成功";
                }
            }
        } else if ("delete".equals(action)) {
            int categoryId = StringUtil.parseInt(req.getParameter("categoryId"), 0);
            int prodCount = categoryDao.countProductsInCategory(categoryId);
            if (prodCount > 0) {
                err = "该分类下仍有关联的 " + prodCount + " 件商品，请先转移或删除关联商品";
            } else {
                Category c = categoryDao.findById(categoryId);
                if (c != null && categoryDao.deleteById(categoryId)) {
                    logDao.record(admin.getUserName(), "分类管理", "删除空分类 [" + c.getCategoryName() + "]", req.getRemoteAddr());
                    msg = "分类已删除";
                }
            }
        }

        String target = req.getContextPath() + "/admin/categories";
        if (StringUtil.isNotEmpty(msg)) {
            target += "?msg=" + URLEncoder.encode(msg, "UTF-8");
        } else if (StringUtil.isNotEmpty(err)) {
            target += "?err=" + URLEncoder.encode(err, "UTF-8");
        }
        resp.sendRedirect(target);
    }
}
