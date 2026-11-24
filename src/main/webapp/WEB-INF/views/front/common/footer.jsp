<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<footer class="site-footer">
    <div class="container">
        <div style="display:grid;grid-template-columns:2fr 1fr 1fr 1fr;gap:32px;">
            <div>
                <div style="font-size:16px;font-weight:700;color:var(--text-primary);margin-bottom:8px;">星潮数码配件电子商城</div>
                <p style="max-width:360px;line-height:1.7;">专注声学降噪、第五代氮化镓快充、客制化机械外设与雷电高速扩展存储设备。基于《Java Web 技术与应用》MVC 分层架构（Servlet + JSP + JSTL/EL + JDBC + MySQL）设计与实现。</p>
            </div>
            <div>
                <div style="font-weight:700;color:var(--text-primary);margin-bottom:10px;">热门数码分类</div>
                <div style="display:grid;gap:6px;">
                    <a href="${ctx}/products?categoryId=1">声学音频设备</a>
                    <a href="${ctx}/products?categoryId=2">氮化镓快充矩阵</a>
                    <a href="${ctx}/products?categoryId=3">客制化键鼠外设</a>
                    <a href="${ctx}/products?categoryId=4">雷电高速存储</a>
                </div>
            </div>
            <div>
                <div style="font-weight:700;color:var(--text-primary);margin-bottom:10px;">会员服务中心</div>
                <div style="display:grid;gap:6px;">
                    <a href="${ctx}/cart">我的购物车</a>
                    <a href="${ctx}/orders">订单状态跟踪</a>
                    <a href="${ctx}/favorite">商品收藏夹</a>
                    <a href="${ctx}/user">收货地址管理</a>
                </div>
            </div>
            <div>
                <div style="font-weight:700;color:var(--text-primary);margin-bottom:10px;">系统管理与文档</div>
                <div style="display:grid;gap:6px;">
                    <a href="${ctx}/admin/login">管理员后台登录</a>
                    <a href="${ctx}/download?type=report">下载课程大作业报告</a>
                    <a href="${ctx}/download?type=sql">下载数据库初始化脚本</a>
                </div>
            </div>
        </div>
        <div class="footer-Copy">
            <span>信息工程学院 · 计算机科学与技术专业《Java Web 技术与应用》课程大作业</span>
            <span>技术栈：Java Servlet · JSP · JSTL/EL · Filter · HttpSession · JDBC · MySQL</span>
        </div>
    </div>
</footer>
<script src="${ctx}/static/js/mall.js"></script>
