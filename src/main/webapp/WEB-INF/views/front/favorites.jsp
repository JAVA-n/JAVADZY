<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>我的商品收藏夹 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container" style="padding-top:24px;">
    <div class="section-head">
        <div>
            <h2>我的商品收藏夹</h2>
            <p>已收藏心仪数码单品共 ${favorites.size()} 款，支持一键加入购物车</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty favorites}">
            <div class="card-panel" style="text-align:center;padding:56px 20px;">
                <h3 style="margin-bottom:8px;">收藏夹暂无商品</h3>
                <p style="color:var(--text-secondary);margin-bottom:18px;">浏览商品详情页时点击“收藏该商品”即可保存在此处。</p>
                <a href="${ctx}/products" class="btn btn-primary">去逛逛数码新品</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach items="${favorites}" var="fav">
                    <c:set var="p" value="${fav.product}" />
                    <div class="product-card">
                        <a href="${ctx}/product?id=${p.productId}" class="product-thumb">
                            <img src="${ctx}/${p.image}" alt="${p.productName}" />
                            <c:if test="${p.hasDiscount}">
                                <span class="badge-discount">${p.discountLabel}</span>
                            </c:if>
                        </a>
                        <div class="product-body">
                            <a href="${ctx}/product?id=${p.productId}" class="product-title">${p.productName}</a>
                            <div class="product-sub">${p.subtitle}</div>
                            <div class="product-foot">
                                <span class="price-now">¥<fmt:formatNumber value="${p.salePrice}" pattern="0.00" /></span>
                                <div style="display:flex;gap:6px;">
                                    <form action="${ctx}/cart" method="post">
                                        <input type="hidden" name="action" value="add" />
                                        <input type="hidden" name="productId" value="${p.productId}" />
                                        <button type="submit" class="btn btn-primary btn-sm">加购</button>
                                    </form>
                                    <form action="${ctx}/favorite" method="post">
                                        <input type="hidden" name="action" value="remove" />
                                        <input type="hidden" name="productId" value="${p.productId}" />
                                        <button type="submit" class="btn btn-outline btn-sm">取消</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
