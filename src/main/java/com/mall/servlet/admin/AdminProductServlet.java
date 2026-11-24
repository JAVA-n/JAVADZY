package com.mall.servlet.admin;

import com.mall.bean.Product;
import com.mall.bean.User;
import com.mall.dao.CategoryDao;
import com.mall.dao.LogDao;
import com.mall.dao.ProductDao;
import com.mall.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 后台商品管理与折扣设置控制器（Servlet），支持商品增删改查、上下架切换、折扣率设置与 @MultipartConfig 图片上传。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class AdminProductServlet extends HttpServlet {

    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final LogDao logDao = new LogDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int categoryId = StringUtil.parseInt(req.getParameter("categoryId"), 0);
        int status = StringUtil.parseInt(req.getParameter("status"), -1);
        String keyword = req.getParameter("keyword");
        int editId = StringUtil.parseInt(req.getParameter("editId"), 0);

        if (editId > 0) {
            req.setAttribute("editProduct", productDao.findById(editId));
        }
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("products", productDao.findAllForAdmin(
                categoryId > 0 ? categoryId : null, status >= 0 ? status : null, keyword));
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("err", req.getParameter("err"));
        req.setAttribute("adminMenu", "products");
        req.getRequestDispatcher("/WEB-INF/views/admin/product_manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("adminUser");
        String action = req.getParameter("action");
        String msg = "";
        String err = "";

        if ("save".equals(action)) {
            int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
            String name = req.getParameter("productName");
            String subtitle = req.getParameter("subtitle");
            int categoryId = StringUtil.parseInt(req.getParameter("categoryId"), 1);
            double priceVal = StringUtil.parseDouble(req.getParameter("price"), 99.0);
            double discountVal = StringUtil.parseDouble(req.getParameter("discount"), 1.0);
            discountVal = Math.min(1.0, Math.max(0.1, discountVal));
            int stock = StringUtil.parseInt(req.getParameter("stock"), 50);
            String specs = req.getParameter("specs");
            String description = req.getParameter("description");
            int isHot = StringUtil.parseInt(req.getParameter("isHot"), 0);
            int status = StringUtil.parseInt(req.getParameter("status"), 1);
            String imagePath = req.getParameter("existingImage");

            // 处理 Multipart 商品图片上传
            try {
                Part filePart = req.getPart("imageFile");
                if (filePart != null && filePart.getSize() > 0 && StringUtil.isNotEmpty(filePart.getSubmittedFileName())) {
                    String orig = filePart.getSubmittedFileName();
                    String ext = orig.contains(".") ? orig.substring(orig.lastIndexOf('.')).toLowerCase() : ".jpg";
                    String newFileName = "prod_" + UUID.randomUUID().toString().substring(0, 8) + ext;
                    String uploadRealPath = req.getServletContext().getRealPath("/uploads");
                    if (uploadRealPath != null) {
                        File uploadDir = new File(uploadRealPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(new File(uploadDir, newFileName).getAbsolutePath());
                        imagePath = "uploads/" + newFileName;
                    }
                }
            } catch (Exception ignored) {
            }

            if (StringUtil.isEmpty(imagePath)) {
                imagePath = "static/images/prod_gan140.jpg";
            }

            Product p = new Product();
            p.setProductId(productId);
            p.setProductName(name != null ? name.trim() : "未命名数码配件");
            p.setSubtitle(subtitle != null ? subtitle.trim() : "");
            p.setCategoryId(categoryId);
            p.setPrice(BigDecimal.valueOf(priceVal));
            p.setDiscount(BigDecimal.valueOf(discountVal));
            p.setStock(stock);
            p.setImage(imagePath);
            p.setSpecs(specs != null ? specs.trim() : "");
            p.setDescription(description != null ? description.trim() : "");
            p.setIsHot(isHot);
            p.setStatus(status);

            if (productId > 0) {
                productDao.update(p);
                logDao.record(admin.getUserName(), "商品管理", "编辑商品 [" + p.getProductName() + "]（折扣：" + discountVal + "，库存：" + stock + "）", req.getRemoteAddr());
                msg = "商品信息修改成功";
            } else {
                productDao.insert(p);
                logDao.record(admin.getUserName(), "商品管理", "新增上架商品 [" + p.getProductName() + "]", req.getRemoteAddr());
                msg = "新商品发布成功";
            }
        } else if ("toggleStatus".equals(action)) {
            int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
            int newStatus = StringUtil.parseInt(req.getParameter("newStatus"), 1);
            Product p = productDao.findById(productId);
            if (p != null && productDao.updateStatus(productId, newStatus)) {
                logDao.record(admin.getUserName(), "商品管理",
                        (newStatus == 1 ? "上架" : "下架") + "商品 [" + p.getProductName() + "]", req.getRemoteAddr());
                msg = newStatus == 1 ? "商品已上架销售" : "商品已下架停售";
            }
        } else if ("quickDiscount".equals(action)) {
            int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
            double discountVal = Math.min(1.0, Math.max(0.1, StringUtil.parseDouble(req.getParameter("discount"), 1.0)));
            int isHot = StringUtil.parseInt(req.getParameter("isHot"), 0);
            Product p = productDao.findById(productId);
            if (p != null && productDao.updateDiscount(productId, BigDecimal.valueOf(discountVal), isHot)) {
                logDao.record(admin.getUserName(), "折扣管理",
                        "设置商品 [" + p.getProductName() + "] 折扣率为 " + discountVal, req.getRemoteAddr());
                msg = "商品折扣与推荐状态已生效";
            }
        } else if ("delete".equals(action)) {
            int productId = StringUtil.parseInt(req.getParameter("productId"), 0);
            Product p = productDao.findById(productId);
            if (p != null && productDao.deleteById(productId)) {
                logDao.record(admin.getUserName(), "商品管理", "删除商品 [" + p.getProductName() + "]", req.getRemoteAddr());
                msg = "商品已移除";
            } else {
                err = "该商品已被历史订单明细引用，建议执行“下架”保留历史财务凭证";
            }
        }

        String target = req.getContextPath() + "/admin/products";
        if (StringUtil.isNotEmpty(msg)) {
            target += "?msg=" + URLEncoder.encode(msg, "UTF-8");
        } else if (StringUtil.isNotEmpty(err)) {
            target += "?err=" + URLEncoder.encode(err, "UTF-8");
        }
        resp.sendRedirect(target);
    }
}
