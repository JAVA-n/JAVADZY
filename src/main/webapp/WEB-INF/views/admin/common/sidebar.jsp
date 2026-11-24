<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<aside class="admin-sidebar">
    <div class="admin-brand">
        <h2>星潮商城管理中心</h2>
        <p>Java Web MVC 运营后台</p>
    </div>
    <nav class="admin-nav">
        <a href="${ctx}/admin/dashboard" class="${adminMenu == 'dashboard' ? 'active' : ''}">销售统计与报表</a>
        <a href="${ctx}/admin/products" class="${adminMenu == 'products' ? 'active' : ''}">商品与折扣管理</a>
        <a href="${ctx}/admin/categories" class="${adminMenu == 'categories' ? 'active' : ''}">商品分类管理</a>
        <a href="${ctx}/admin/orders" class="${adminMenu == 'orders' ? 'active' : ''}">订单状态管理</a>
        <a href="${ctx}/admin/users" class="${adminMenu == 'users' ? 'active' : ''}">注册用户管理</a>
        <a href="${ctx}/admin/banners" class="${adminMenu == 'banners' ? 'active' : ''}">轮播广告管理</a>
        <a href="${ctx}/admin/comments" class="${adminMenu == 'comments' ? 'active' : ''}">商品评论审核</a>
        <a href="${ctx}/admin/logs" class="${adminMenu == 'logs' ? 'active' : ''}">操作审计日志</a>
    </nav>
    <div style="border-top:1px solid rgba(255,255,255,0.12);padding-top:14px;display:grid;gap:6px;font-size:12px;">
        <div style="color:rgba(255,255,255,0.6);padding:0 10px;">管理员：${sessionScope.adminUser.userName}</div>
        <a href="${ctx}/index" style="padding:8px 10px;color:#93c5fd;">&larr; 返回商城前台首页</a>
        <a href="${ctx}/admin/logout" style="padding:8px 10px;color:#fca5a5;">安全退出后台</a>
    </div>
</aside>
