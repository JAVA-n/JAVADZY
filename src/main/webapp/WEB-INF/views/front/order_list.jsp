<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>我的历史订单与状态跟踪 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container" style="padding-top:24px;">
    <div class="section-head">
        <div>
            <h2>我的订单与状态跟踪</h2>
            <p>查看历史购买记录、实时跟踪后台发货进度并确认收货</p>
        </div>
    </div>

    <c:if test="${not empty createdOrderId}">
        <div class="alert alert-success">
            订单提交成功！订单编号：<strong class="num">${createdOrderId}</strong>，仓库正在为您备货发出。
        </div>
    </c:if>

    <div style="display:flex;gap:8px;margin-bottom:20px;flex-wrap:wrap;">
        <a href="${ctx}/orders" class="filter-pill ${stateFilter == -1 ? 'active' : ''}">全部订单</a>
        <a href="${ctx}/orders?state=0" class="filter-pill ${stateFilter == 0 ? 'active' : ''}">待发货</a>
        <a href="${ctx}/orders?state=1" class="filter-pill ${stateFilter == 1 ? 'active' : ''}">已发货</a>
        <a href="${ctx}/orders?state=2" class="filter-pill ${stateFilter == 2 ? 'active' : ''}">已完成</a>
        <a href="${ctx}/orders?state=3" class="filter-pill ${stateFilter == 3 ? 'active' : ''}">已取消</a>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card-panel" style="text-align:center;padding:56px 20px;">
                <h3 style="margin-bottom:8px;">暂无对应状态的订单记录</h3>
                <a href="${ctx}/products" class="btn btn-primary" style="margin-top:12px;">去选购数码好物</a>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach items="${orders}" var="o">
                <div class="card-panel" style="padding:0;overflow:hidden;">
                    <div style="background:var(--bg-page);padding:12px 20px;border-bottom:1px solid var(--border-light);display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;">
                        <div class="num" style="display:flex;gap:20px;font-size:13px;">
                            <span>订单号：<strong>${o.orderId}</strong></span>
                            <span style="color:var(--text-secondary);">下单时间：<fmt:formatDate value="${o.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
                            <span style="color:var(--text-secondary);">收货人：${o.consignee} (${o.phone})</span>
                        </div>
                        <span class="status-tag status-${o.orderState}">${o.stateLabel}</span>
                    </div>

                    <div style="padding:16px 20px;">
                        <c:forEach items="${o.items}" var="it">
                            <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;">
                                <div style="display:flex;align-items:center;gap:12px;">
                                    <img src="${ctx}/${it.productImage}" alt="${it.productName}"
                                         style="width:52px;height:52px;object-fit:cover;border-radius:6px;border:1px solid var(--border-light);" />
                                    <div>
                                        <a href="${ctx}/product?id=${it.productId}" style="font-weight:600;">${it.productName}</a>
                                        <div class="num" style="font-size:12px;color:var(--text-muted);">
                                            成交单价 ¥<fmt:formatNumber value="${it.unitPrice}" pattern="0.00" /> &times; ${it.quantity}
                                        </div>
                                    </div>
                                </div>
                                <div style="display:flex;align-items:center;gap:14px;">
                                    <span class="num" style="font-weight:700;">¥<fmt:formatNumber value="${it.subtotal}" pattern="0.00" /></span>
                                    <c:if test="${o.orderState == 2}">
                                        <a href="${ctx}/product?id=${it.productId}#reviews" class="btn btn-outline btn-sm">评价晒单</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>

                        <div style="margin-top:12px;padding-top:12px;border-top:1px dashed var(--border-light);display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;">
                            <div style="font-size:12px;color:var(--text-secondary);">
                                配送地址：${o.address}
                                <c:if test="${not empty o.remark}"> &nbsp;|&nbsp; 备注：${o.remark}</c:if>
                            </div>
                            <div style="display:flex;align-items:center;gap:12px;">
                                <span class="num">订单总计：<strong style="font-size:18px;color:var(--accent-sale);">¥<fmt:formatNumber value="${o.totalPrice}" pattern="0.00" /></strong></span>
                                <c:if test="${o.orderState == 1}">
                                    <form action="${ctx}/orders" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="confirm" />
                                        <input type="hidden" name="orderId" value="${o.orderId}" />
                                        <button type="submit" class="btn btn-success btn-sm">确认收货</button>
                                    </form>
                                </c:if>
                                <c:if test="${o.orderState == 0}">
                                    <form action="${ctx}/orders" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="cancel" />
                                        <input type="hidden" name="orderId" value="${o.orderId}" />
                                        <button type="submit" class="btn btn-outline btn-sm">取消订单</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
