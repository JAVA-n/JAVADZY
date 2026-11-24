<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>我的购物车 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container" style="padding-top:24px;">
    <div class="section-head">
        <div>
            <h2>我的购物车</h2>
            <p>基于 HttpSession 会话实时维护选购商品数量与折后优惠总额</p>
        </div>
        <a href="${ctx}/products" class="btn btn-outline btn-sm">&larr; 继续选购商品</a>
    </div>

    <c:if test="${param.added == '1'}">
        <div class="alert alert-success">商品已成功加入购物车！</div>
    </c:if>

    <c:choose>
        <c:when test="${empty sessionScope.cart or sessionScope.cart.empty}">
            <div class="card-panel" style="text-align:center;padding:64px 20px;">
                <h3 style="font-size:18px;margin-bottom:8px;">当前购物车暂无商品</h3>
                <p style="color:var(--text-secondary);margin-bottom:20px;">快去挑选心仪的降噪耳机、氮化镓快充或客制化机械键盘吧。</p>
                <a href="${ctx}/products" class="btn btn-primary">前往数码商品专区</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card-panel" style="padding:0;overflow:hidden;">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th style="width:42%;">商品信息</th>
                        <th>成交单价</th>
                        <th style="width:170px;">购买数量</th>
                        <th>金额小计</th>
                        <th style="text-align:right;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${sessionScope.cart.cartItems}" var="ci">
                        <tr>
                            <td>
                                <div style="display:flex;align-items:center;gap:14px;">
                                    <img src="${ctx}/${ci.product.image}" alt="${ci.product.productName}"
                                         style="width:64px;height:64px;object-fit:cover;border-radius:8px;border:1px solid var(--border-light);" />
                                    <div>
                                        <a href="${ctx}/product?id=${ci.product.productId}" style="font-weight:600;font-size:14px;">
                                            ${ci.product.productName}
                                        </a>
                                        <div style="font-size:12px;color:var(--text-muted);margin-top:3px;">${ci.product.specs}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="num">
                                <strong style="color:var(--text-primary);">¥<fmt:formatNumber value="${ci.product.salePrice}" pattern="0.00" /></strong>
                                <c:if test="${ci.product.hasDiscount}">
                                    <div class="price-old">¥<fmt:formatNumber value="${ci.product.price}" pattern="0.00" /></div>
                                </c:if>
                            </td>
                            <td>
                                <form action="${ctx}/cart" method="post" style="display:inline-flex;align-items:center;gap:6px;">
                                    <input type="hidden" name="action" value="update" />
                                    <input type="hidden" name="productId" value="${ci.product.productId}" />
                                    <button type="submit" name="quantity" value="${ci.quantity - 1}" class="btn btn-outline btn-sm">-</button>
                                    <span class="num" style="min-width:28px;text-align:center;font-weight:700;">${ci.quantity}</span>
                                    <button type="submit" name="quantity" value="${ci.quantity + 1}" class="btn btn-outline btn-sm">+</button>
                                </form>
                            </td>
                            <td class="num">
                                <strong style="font-size:15px;color:var(--accent-sale);">¥<fmt:formatNumber value="${ci.subtotal}" pattern="0.00" /></strong>
                            </td>
                            <td style="text-align:right;">
                                <form action="${ctx}/cart" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="remove" />
                                    <input type="hidden" name="productId" value="${ci.product.productId}" />
                                    <button type="submit" class="btn btn-danger btn-sm">移除</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <div style="padding:20px 24px;background:var(--bg-page);display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:16px;">
                    <form action="${ctx}/cart" method="post">
                        <input type="hidden" name="action" value="clear" />
                        <button type="submit" class="btn btn-outline btn-sm">清空购物车</button>
                    </form>

                    <div style="display:flex;align-items:center;gap:24px;">
                        <div class="num" style="text-align:right;">
                            <div>共选 <strong>${sessionScope.cart.totalQuantity}</strong> 件商品，折扣共省 <span style="color:var(--success);font-weight:600;">¥<fmt:formatNumber value="${sessionScope.cart.totalSaved}" pattern="0.00" /></span></div>
                            <div style="margin-top:2px;">应付总额：<strong style="font-size:24px;color:var(--accent-sale);">¥<fmt:formatNumber value="${sessionScope.cart.totalAmount}" pattern="0.00" /></strong></div>
                        </div>
                        <a href="${ctx}/checkout" class="btn btn-primary btn-lg">去结算提交订单</a>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
