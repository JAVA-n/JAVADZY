<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>商品分类与搜索列表 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container">
    <!-- 多维分类筛选与排序工具面板 -->
    <div class="filter-panel">
        <div class="filter-row">
            <span class="filter-label">商品分类：</span>
            <a href="${ctx}/products?keyword=${keyword}&sort=${sort}${discountOnly ? '&discount=1' : ''}"
               class="filter-pill ${selectedCategoryId == 0 ? 'active' : ''}">全部品类</a>
            <c:forEach items="${categories}" var="cat">
                <a href="${ctx}/products?categoryId=${cat.categoryId}&keyword=${keyword}&sort=${sort}${discountOnly ? '&discount=1' : ''}"
                   class="filter-pill ${selectedCategoryId == cat.categoryId ? 'active' : ''}">
                    ${cat.categoryName} (${cat.productCount})
                </a>
            </c:forEach>
        </div>

        <div class="filter-row">
            <span class="filter-label">排序方式：</span>
            <a href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=default${discountOnly ? '&discount=1' : ''}"
               class="filter-pill ${sort == 'default' ? 'active' : ''}">综合推荐</a>
            <a href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=sales${discountOnly ? '&discount=1' : ''}"
               class="filter-pill ${sort == 'sales' ? 'active' : ''}">销量优先</a>
            <a href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=price_asc${discountOnly ? '&discount=1' : ''}"
               class="filter-pill ${sort == 'price_asc' ? 'active' : ''}">价格从低到高</a>
            <a href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=price_desc${discountOnly ? '&discount=1' : ''}"
               class="filter-pill ${sort == 'price_desc' ? 'active' : ''}">价格从高到低</a>
            <a href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=${sort}${discountOnly ? '' : '&discount=1'}"
               class="filter-pill ${discountOnly ? 'active' : ''}" style="margin-left:auto;">
                仅看特惠打折商品
            </a>
        </div>
    </div>

    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <div style="font-size:14px;color:var(--text-secondary);">
            <c:choose>
                <c:when test="${not empty keyword}">
                    关键字“<strong>${keyword}</strong>”共检索到 <strong class="num">${pageBean.totalCount}</strong> 款数码配件
                </c:when>
                <c:when test="${not empty currentCategory}">
                    分类【<strong>${currentCategory.categoryName}</strong>】共 <strong class="num">${pageBean.totalCount}</strong> 款在售商品
                </c:when>
                <c:otherwise>
                    当前共展示 <strong class="num">${pageBean.totalCount}</strong> 款官方自营数码配件（第 ${pageBean.currentPage}/${pageBean.totalPages} 页）
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${not empty keyword or selectedCategoryId > 0 or discountOnly}">
            <a href="${ctx}/products" style="font-size:13px;color:var(--brand-primary);">清空筛选条件</a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty pageBean.list}">
            <div class="card-panel" style="text-align:center;padding:64px 24px;">
                <h3 style="font-size:18px;margin-bottom:8px;">未找到符合条件的数码商品</h3>
                <p style="color:var(--text-secondary);margin-bottom:20px;">建议尝试更换搜索关键词，或浏览全部商品分类。</p>
                <a href="${ctx}/products" class="btn btn-primary">浏览全部在售商品</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach items="${pageBean.list}" var="p">
                    <div class="product-card">
                        <a href="${ctx}/product?id=${p.productId}" class="product-thumb">
                            <img src="${ctx}/${p.image}" alt="${p.productName}" />
                            <c:if test="${p.hasDiscount}">
                                <span class="badge-discount">${p.discountLabel}</span>
                            </c:if>
                            <c:if test="${p.isHot == 1}">
                                <span class="badge-hot">HOT</span>
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
                                    <button type="submit" class="btn btn-outline btn-sm">加入购物车</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- 分页导航条（前台分页浏览扩展功能） -->
            <div class="pagination">
                <a class="page-link ${pageBean.currentPage <= 1 ? 'disabled' : ''}"
                   href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=${sort}${discountOnly ? '&discount=1' : ''}&page=${pageBean.currentPage - 1}">
                    上一页
                </a>
                <c:forEach begin="1" end="${pageBean.totalPages}" var="pg">
                    <a class="page-link num ${pg == pageBean.currentPage ? 'active' : ''}"
                       href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=${sort}${discountOnly ? '&discount=1' : ''}&page=${pg}">
                        ${pg}
                    </a>
                </c:forEach>
                <a class="page-link ${pageBean.currentPage >= pageBean.totalPages ? 'disabled' : ''}"
                   href="${ctx}/products?categoryId=${selectedCategoryId}&keyword=${keyword}&sort=${sort}${discountOnly ? '&discount=1' : ''}&page=${pageBean.currentPage + 1}">
                    下一页
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
