package com.mall.servlet.front;

import com.mall.bean.Category;
import com.mall.bean.PageBean;
import com.mall.bean.Product;
import com.mall.dao.CategoryDao;
import com.mall.dao.ProductDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 前台商品列表、分类筛选、关键字模糊搜索与分页浏览控制器（Servlet）。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int categoryId = StringUtil.parseInt(req.getParameter("categoryId"), 0);
        String keyword = req.getParameter("keyword");
        boolean discountOnly = "1".equals(req.getParameter("discount"));
        String sort = req.getParameter("sort");
        if (StringUtil.isEmpty(sort)) {
            sort = "default";
        }
        int page = StringUtil.parseInt(req.getParameter("page"), 1);
        int pageSize = 8;

        List<Category> categories = categoryDao.findAll();
        PageBean<Product> pageBean = productDao.findPageForFront(
                categoryId > 0 ? categoryId : null, keyword, discountOnly, sort, page, pageSize);

        Category currentCategory = categoryId > 0 ? categoryDao.findById(categoryId) : null;

        req.setAttribute("categories", categories);
        req.setAttribute("currentCategory", currentCategory);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        req.setAttribute("discountOnly", discountOnly);
        req.setAttribute("sort", sort);
        req.setAttribute("pageBean", pageBean);
        req.setAttribute("navActive", discountOnly ? "discount" : "products");
        req.getRequestDispatcher("/WEB-INF/views/front/product_list.jsp").forward(req, resp);
    }
}
