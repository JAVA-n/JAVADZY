package com.mall.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBC 数据库连接与初始化工具类。
 * 支持标准 MySQL 5.7/8.0 数据库连接，并在未检测到外部 MySQL 服务时自动回退至兼容 MySQL 语法的本地持久化库并完成初始化。
 *
 * @author 张三
 * @studentId 20240301
 */
public class DBUtil {

    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static boolean usingEmbeddedMySqlCompat = false;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
        }

        String mode = System.getenv("DB_MODE") != null ? System.getenv("DB_MODE") : props.getProperty("db.mode", "auto");
        String mysqlUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : props.getProperty("mysql.url");
        String mysqlUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("mysql.username", "root");
        String mysqlPwd = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : props.getProperty("mysql.password", "123456");

        if (!"embedded".equalsIgnoreCase(mode) && StringUtil.isNotEmpty(mysqlUrl)) {
            try {
                Class.forName(props.getProperty("mysql.driver", "com.mysql.cj.jdbc.Driver"));
                DriverManager.setLoginTimeout(2);
                String probeUrl = mysqlUrl.contains("connectTimeout=")
                        ? mysqlUrl
                        : mysqlUrl + (mysqlUrl.contains("?") ? "&" : "?") + "connectTimeout=1500&socketTimeout=5000";
                try (Connection testConn = DriverManager.getConnection(probeUrl, mysqlUser, mysqlPwd)) {
                    jdbcUrl = probeUrl;
                    jdbcUser = mysqlUser;
                    jdbcPassword = mysqlPwd;
                    usingEmbeddedMySqlCompat = false;
                    System.out.println("[DBUtil] 已成功连接外部 MySQL 数据库: " + jdbcUrl);
                    return;
                }
            } catch (Exception e) {
                if ("mysql".equalsIgnoreCase(mode)) {
                    throw new RuntimeException("连接外部 MySQL 失败，请检查 db.properties 配置与 MySQL 服务状态: " + e.getMessage(), e);
                }
                System.out.println("[DBUtil] 未检测到运行中的本地 MySQL 实例，自动启用内置 MySQL 兼容模式持久化数据库。");
            }
        }

        try {
            Class.forName("org.h2.Driver");
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            String dbFile = new File(dataDir, "digital_mall").getAbsolutePath();
            jdbcUrl = "jdbc:h2:file:" + dbFile + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;AUTO_SERVER=TRUE";
            jdbcUser = "root";
            jdbcPassword = "";
            usingEmbeddedMySqlCompat = true;
            ensureSchemaInitialized();
        } catch (Exception e) {
            throw new RuntimeException("初始化内置 JDBC 数据库失败", e);
        }
    }

    /**
     * 首次启动时自动读取 sql/digital_mall.sql 初始化表结构与种子数据
     */
    private static void ensureSchemaInitialized() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            } catch (SQLException tableMissing) {
                // 表不存在，继续执行 SQL 初始化脚本
            }

            File sqlFile = new File("sql/digital_mall.sql");
            if (!sqlFile.exists()) {
                return;
            }
            StringBuilder cleanSql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    cleanSql.append(line).append("\n");
                }
            }

            String[] statements = cleanSql.toString().split(";\\s*(?:\\r?\\n|$)");
            for (String sql : statements) {
                String s = sql.trim();
                if (s.isEmpty()) {
                    continue;
                }
                String upper = s.toUpperCase();
                if (upper.startsWith("CREATE DATABASE") || upper.startsWith("USE ")
                        || upper.startsWith("SET FOREIGN_KEY_CHECKS")) {
                    continue;
                }
                String adapted = s
                        .replaceAll("(?i)ENGINE=InnoDB\\s+DEFAULT\\s+CHARSET=utf8mb4(\\s+COMMENT='[^']*')?", "")
                        .replaceAll("(?i)COLLATE\\s+utf8mb4_unicode_ci", "");
                stmt.execute(adapted);
            }
            System.out.println("[DBUtil] 已根据 sql/digital_mall.sql 自动完成数据库表结构与初始商品数据加载。");
        } catch (Exception e) {
            System.err.println("[DBUtil] 执行数据库自动初始化脚本异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取 JDBC 数据库连接对象
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    public static boolean isUsingEmbeddedMySqlCompat() {
        return usingEmbeddedMySqlCompat;
    }

    /**
     * 统一释放 JDBC 资源
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
