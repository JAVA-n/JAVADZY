<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>订单管理 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>商城订单处理与状态管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">按订单状态分类查询订单明细，执行确认发货、订单完结与异常取消操作</p>
            </div>
            <form action="${ctx}/admin/orders" method="get" style="display:flex;gap:8px;">
                <input type="hidden" name="state" value="${stateFilter}" />
                <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="搜索订单号、收货人或手机号..." style="width:240px;" />
                <button type="submit" class="btn btn-dark btn-sm">检索订单</button>
            </form>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>

        <div style="display:flex;gap:8px;margin-bottom:18px;">
            <a href="${ctx}/admin/orders" class="filter-pill ${stateFilter == -1 ? 'active' : ''}">全部订单 (${orders.size()})</a>
            <a href="${ctx}/admin/orders?state=0" class="filter-pill ${stateFilter == 0 ? 'active' : ''}">待发货</a>
            <a href="${ctx}/admin/orders?state=1" class="filter-pill ${stateFilter == 1 ? 'active' : ''}">已发货</a>
            <a href="${ctx}/admin/orders?state=2" class="filter-pill ${stateFilter == 2 ? 'active' : ''}">已完成</a>
            <a href="${ctx}/admin/orders?state=3" class="filter-pill ${stateFilter == 3 ? 'active' : ''}">已取消</a>
        </div>

        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>订单编号 / 下单时间</th>
                    <th>购买商品明细</th>
                    <th>收货人及配送地址</th>
                    <th>实付总额</th>
                    <th>当前状态</th>
                    <th style="text-align:right;">流转处理</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${orders}" var="o">
                    <tr>
                        <td class="num">
                            <div style="font-weight:700;">${o.orderId}</div>
                            <div style="font-size:11px;color:var(--text-muted);margin-top:3px;">
                                下单：<fmt:formatDate value="${o.createTime}" pattern="yyyy-MM-dd HH:mm" />
                            </div>
                            <div style="font-size:11px;color:var(--text-muted);">会员账号：${o.userName}</div>
                        </td>
                        <td>
                            <c:forEach items="${o.items}" var="it">
                                <div style="font-size:12px;margin-bottom:4px;">
                                    · ${it.productName} <span class="num" style="color:var(--text-muted);">&times; ${it.quantity} (¥${it.unitPrice})</span>
                                </div>
                            </c:forEach>
                        </td>
                        <td style="max-width:240px;">
                            <div><strong>${o.consignee}</strong> (${o.phone})</div>
                            <div style="font-size:12px;color:var(--text-secondary);">${o.address}</div>
                            <c:if test="${not empty o.remark}">
                                <div style="font-size:11px;color:var(--warning);">备注：${o.remark}</div>
                            </c:if>
                        </td>
                        <td class="num"><strong style="color:var(--accent-sale);font-size:15px;">¥<fmt:formatNumber value="${o.totalPrice}" pattern="0.00" /></strong></td>
                        <td>
                            <span class="status-tag status-${o.orderState}">${o.stateLabel}</span>
                        </td>
                        <td style="text-align:right;">
                            <div style="display:inline-flex;gap:6px;">
                                <c:if test="${o.orderState == 0}">
                                    <form action="${ctx}/admin/orders" method="post" style="display:inline;">
                                        <input type="hidden" name="orderId" value="${o.orderId}" />
                                        <input type="hidden" name="newState" value="1" />
                                        <button type="submit" class="btn btn-primary btn-sm">立即发货</button>
                                    </form>
                                    <form action="${ctx}/admin/orders" method="post" style="display:inline;">
                                        <input type="hidden" name="orderId" value="${o.orderId}" />
                                        <input type="hidden" name="newState" value="3" />
                                        <button type="submit" class="btn btn-outline btn-sm">取消</button>
                                    </form>
                                </c:if>
                                <c:if test="${o.orderState == 1}">
                                    <form action="${ctx}/admin/orders" method="post" style="display:inline;">
                                        <input type="hidden" name="orderId" value="${o.orderId}" />
                                        <input type="hidden" name="newState" value="2" />
                                        <button type="submit" class="btn btn-success btn-sm">标记完成</button>
                                    </form>
                                </c:if>
                                <c:if test="${o.orderState >= 2}">
                                    <span style="font-size:12px;color:var(--text-muted);">已归档</span>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
