package com.mall.servlet.front;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 大作业文档与数据库脚本快捷下载控制器（Servlet），支持从浏览器直接下载报告 .docx 与 MySQL 脚本。
 *
 * @author 张三
 * @studentId 20240301
 */
@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        File targetFile = null;
        String downloadName = null;
        String contentType = "application/octet-stream";

        if ("sql".equals(type)) {
            targetFile = new File("sql/digital_mall.sql");
            downloadName = "digital_mall.sql";
            contentType = "application/sql; charset=UTF-8";
        } else {
            // 查找根目录下生成的最新大作业报告 .docx（排除原始 模板.docx）
            File dir = new File(".");
            File[] docxFiles = dir.listFiles((d, name) -> name.endsWith(".docx") && !name.equals("模板.docx") && !name.startsWith("~$"));
            if (docxFiles != null && docxFiles.length > 0) {
                targetFile = docxFiles[0];
                downloadName = targetFile.getName();
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        }

        if (targetFile == null || !targetFile.exists()) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("请求的文件尚未生成或不存在。");
            return;
        }

        String encoded = URLEncoder.encode(downloadName, "UTF-8").replace("+", "%20");
        resp.setContentType(contentType);
        resp.setContentLengthLong(targetFile.length());
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);

        try (FileInputStream in = new FileInputStream(targetFile);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}
