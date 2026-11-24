package com.mall.server;

import com.mall.util.DBUtil;
import com.mall.util.StringUtil;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.scan.StandardJarScanner;

import java.io.File;
import java.sql.Connection;

/**
 * 嵌入式 Tomcat 9 启动器，支持通过 $PORT 环境变量直接启动 Servlet/JSP 容器，兼容本地与云端部署。
 *
 * @author 张三
 * @studentId 20240301
 */
public class EmbeddedTomcatServer {

    public static void main(String[] args) throws Exception {
        int port = StringUtil.parseInt(System.getenv("PORT"), 8080);

        // 预热数据库连接与表结构种子数据初始化
        try (Connection ignored = DBUtil.getConnection()) {
            System.out.println("[MallServer] 数据库连接就绪，准备启动 Tomcat Web 容器...");
        }

        File baseDir = new File("target/tomcat-work-" + port);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(port);

        Connector connector = tomcat.getConnector();
        connector.setURIEncoding("UTF-8");
        connector.setProperty("address", "0.0.0.0");

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir.mkdirs();
        }

        Context ctx = tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setRequestCharacterEncoding("UTF-8");
        ctx.setResponseCharacterEncoding("UTF-8");

        // 开启类路径扫描以便 Jasper 自动加载 JSTL 1.2 标签库 TLD 文件
        if (ctx.getJarScanner() instanceof StandardJarScanner) {
            StandardJarScanner scanner = (StandardJarScanner) ctx.getJarScanner();
            scanner.setScanClassPath(true);
            scanner.setScanAllDirectories(true);
        }

        // 挂载编译后的 target/classes 至 /WEB-INF/classes 以识别 @WebServlet 与 @WebFilter 注解
        File classesDir = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        if (classesDir.exists()) {
            resources.addPreResources(new DirResourceSet(
                    resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"));
        }
        ctx.setResources(resources);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (Exception ignored) {
            }
        }));

        tomcat.start();
        System.out.println("=============================================================");
        System.out.println(" 星潮数码配件电子商城 (Java Web MVC) 已成功启动！");
        System.out.println(" 前台访问地址: http://localhost:" + port + "/");
        System.out.println(" 后台管理地址: http://localhost:" + port + "/admin/login");
        System.out.println(" 演示会员账号: zhangsan / 123456");
        System.out.println(" 后台管理账号: admin / admin123");
        System.out.println("=============================================================");
        tomcat.getServer().await();
    }
}
