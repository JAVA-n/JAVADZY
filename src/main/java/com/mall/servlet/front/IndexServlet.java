package com.mall.servlet.front;

import com.mall.bean.Banner;
import com.mall.bean.Category;
import com.mall.bean.Product;
import com.mall.dao.BannerDao;
import com.mall.dao.CategoryDao;
import com.mall.dao.ProductDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 商城前台首页控制器（Servlet），加载启用轮播广告、商品分类导航、限时折扣与热销推荐商品。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/index"})
public class IndexServlet extends HttpServlet {

    private final BannerDao bannerDao = new BannerDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Banner> banners = bannerDao.findActiveBanners();
        List<Category> categories = categoryDao.findAll();
        List<Product> discountProducts = productDao.findDiscountProducts(4);
        List<Product> hotProducts = productDao.findHotProducts(8);

        req.setAttribute("banners", banners);
        req.setAttribute("categories", categories);
        req.setAttribute("discountProducts", discountProducts);
        req.setAttribute("hotProducts", hotProducts);
        req.setAttribute("navActive", "home");
        req.getRequestDispatcher("/WEB-INF/views/front/index.jsp").forward(req, resp);
    }
}
