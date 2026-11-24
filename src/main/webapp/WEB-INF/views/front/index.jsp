<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>星潮数码配件商城 - 首页</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container">
    <!-- 1. 首页条幅广告轮播图（前台扩展功能） -->
    <section class="hero-section">
        <div class="carousel" data-carousel>
            <c:forEach items="${banners}" var="b" varStatus="st">
                <div class="carousel-slide ${st.first ? 'active' : ''}">
                    <img src="${ctx}/${b.imageUrl}" alt="${b.title}" />
                    <div class="carousel-overlay">
                        <div class="carousel-content">
                            <c:if test="${not empty b.badge}">
                                <span class="carousel-badge">${b.badge}</span>
                            </c:if>
                            <h1>${b.title}</h1>
                            <p>${b.subtitle}</p>
                            <a href="${ctx}/${b.linkUrl}" class="btn btn-primary btn-lg">立即探索选购</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <button type="button" class="carousel-nav carousel-prev" aria-label="上一张">&lsaquo;</button>
            <button type="button" class="carousel-nav carousel-next" aria-label="下一张">&rsaquo;</button>
            <div class="carousel-dots">
                <c:forEach items="${banners}" var="b" varStatus="st">
                    <button type="button" class="carousel-dot ${st.first ? 'active' : ''}" aria-label="切换轮播"></button>
                </c:forEach>
            </div>
        </div>
    </section>

    <!-- 2. 五大数码分类快捷导航 -->
    <section class="category-strip">
        <c:forEach items="${categories}" var="cat">
            <a href="${ctx}/products?categoryId=${cat.categoryId}" class="category-card">
                <div class="category-card-top">
                    <h3>${cat.categoryName}</h3>
                    <span class="category-count num">${cat.productCount} 款</span>
                </div>
                <p>${cat.categoryDesc}</p>
            </a>
        </c:forEach>
    </section>

    <!-- 3. 限时特惠折扣专区（联动后台折扣管理扩展功能） -->
    <section class="section-block">
        <div class="section-head">
            <div>
                <h2>限时特惠专区</h2>
                <p>官方直降折扣实时生效，结算自动按折后价计费</p>
            </div>
            <a href="${ctx}/products?discount=1" class="more-link">查看全部特惠商品 &rarr;</a>
        </div>
        <div class="product-grid">
            <c:forEach items="${discountProducts}" var="p">
                <div class="product-card">
                    <a href="${ctx}/product?id=${p.productId}" class="product-thumb">
                        <img src="${ctx}/${p.image}" alt="${p.productName}" />
                        <c:if test="${p.hasDiscount}">
                            <span class="badge-discount">${p.discountLabel}</span>
                        </c:if>
                    </a>
                    <div class="product-body">
                        <div class="product-cat">${p.categoryName}</div>
                        <a href="${ctx}/product?id=${p.productId}" class="product-title">${p.productName}</a>
                        <div class="product-sub">${p.subtitle}</div>
                        <div class="product-foot">
                            <div class="price-group">
                                <span class="price-now">¥<fmt:formatNumber value="${p.salePrice}" pattern="0.00" /></span>
                                <c:if test="${p.hasDiscount}">
                                    <span class="price-old">¥<fmt:formatNumber value="${p.price}" pattern="0.00" /></span>
                                </c:if>
                            </div>
                            <form action="${ctx}/cart" method="post">
                                <input type="hidden" name="action" value="add" />
                                <input type="hidden" name="productId" value="${p.productId}" />
                                <input type="hidden" name="quantity" value="1" />
                                <button type="submit" class="btn btn-outline btn-sm">加购</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <!-- 4. 热销爆款推荐专区（前台扩展功能） -->
    <section class="section-block">
        <div class="section-head">
            <div>
                <h2>热门人气推荐</h2>
                <p>精选高口碑桌面生产力装备与便携充电配件</p>
            </div>
            <a href="${ctx}/products?sort=sales" class="more-link">按销量排行浏览 &rarr;</a>
        </div>
        <div class="product-grid">
            <c:forEach items="${hotProducts}" var="p">
                <div class="product-card">
                    <a href="${ctx}/product?id=${p.productId}" class="product-thumb">
                        <img src="${ctx}/${p.image}" alt="${p.productName}" />
                        <c:if test="${p.hasDiscount}">
                            <span class="badge-discount">${p.discountLabel}</span>
                        </c:if>
                        <span class="badge-hot">HOT 热销</span>
                    </a>
                    <div class="product-body">
                        <div class="product-cat">${p.categoryName}</div>
                        <a href="${ctx}/product?id=${p.productId}" class="product-title">${p.productName}</a>
                        <div class="product-sub">${p.subtitle}</div>
                        <div class="product-foot">
                            <div class="price-group">
                                <span class="price-now">¥<fmt:formatNumber value="${p.salePrice}" pattern="0.00" /></span>
                                <c:if test="${p.hasDiscount}">
                                    <span class="price-old">¥<fmt:formatNumber value="${p.price}" pattern="0.00" /></span>
                                </c:if>
                            </div>
                            <span class="sales-tag num">已售 ${p.sales}</span>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
