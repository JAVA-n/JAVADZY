<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="topbar">
    <div class="topbar-inner">
        <div>星潮数码配件官方自营商城 · 全场正品保障 · 满 99 元顺丰包邮</div>
        <div class="topbar-links">
            <a href="${ctx}/download?type=report" class="topbar-pill">下载大作业论文 (.docx)</a>
            <a href="${ctx}/download?type=sql">下载 MySQL 脚本 (.sql)</a>
            <a href="${ctx}/admin/login">后台管理系统入口</a>
        </div>
    </div>
</div>
<header class="site-header">
    <div class="container header-main">
        <a href="${ctx}/index" class="brand">
            <div class="brand-mark">XC</div>
            <div class="brand-text">
                <strong>星潮数码商城</strong>
                <span>XINGCHAO DIGITAL</span>
            </div>
        </a>

        <nav class="nav-links">
            <a href="${ctx}/index" class="${navActive == 'home' ? 'active' : ''}">商城首页</a>
            <a href="${ctx}/products" class="${navActive == 'products' ? 'active' : ''}">全部商品</a>
            <a href="${ctx}/products?discount=1" class="${navActive == 'discount' ? 'active' : ''}">限时折扣</a>
            <c:if test="${not empty sessionScope.loginUser}">
                <a href="${ctx}/favorite" class="${navActive == 'favorite' ? 'active' : ''}">我的收藏</a>
                <a href="${ctx}/orders" class="${navActive == 'orders' ? 'active' : ''}">我的订单</a>
            </c:if>
        </nav>

        <form action="${ctx}/products" method="get" class="search-box">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索氮化镓快充、降噪耳机、机械键盘..." />
            <button type="submit">搜索</button>
        </form>

        <div class="header-actions">
            <a href="${ctx}/cart" class="action-btn">
                <span>购物车</span>
                <span class="cart-badge num">${empty sessionScope.cart ? 0 : sessionScope.cart.totalQuantity}</span>
            </a>
            <c:choose>
                <c:when test="${not empty sessionScope.loginUser}">
                    <a href="${ctx}/user" class="action-btn">${sessionScope.loginUser.userName}</a>
                    <a href="${ctx}/logout" class="action-btn" style="color:var(--text-secondary);">退出</a>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/login" class="action-btn">登录</a>
                    <a href="${ctx}/register" class="btn btn-dark btn-sm" style="border-radius:999px;padding:7px 15px;">免费注册</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>
