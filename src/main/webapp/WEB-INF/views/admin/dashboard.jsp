<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>销售统计与运营报表 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>销售统计与经营数据报表</h1>
                <p style="font-size:13px;color:var(--text-secondary);">实时汇总全站成交额、各数码品类占比、订单处理进度与低库存预警</p>
            </div>
            <div style="display:flex;gap:10px;">
                <a href="${ctx}/admin/products" class="btn btn-primary btn-sm">+ 发布新商品</a>
                <a href="${ctx}/admin/orders?state=0" class="btn btn-outline btn-sm">待发货订单 (${pendingOrderCount})</a>
            </div>
        </div>

        <!-- 核心经营 KPI 指标卡 -->
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-label">累计有效成交总额</div>
                <div class="kpi-value" style="color:var(--brand-primary);">¥<fmt:formatNumber value="${totalRevenue}" pattern="0.00" /></div>
                <div class="kpi-sub">不含已取消订单金额</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">累计商城订单总数</div>
                <div class="kpi-value">${totalOrders} 笔</div>
                <div class="kpi-sub">当前待处理发货：<strong>${pendingOrderCount}</strong> 笔</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">在库数码配件 SKU</div>
                <div class="kpi-value">${totalProducts} 款</div>
                <div class="kpi-sub">覆盖五大数码配件细分品类</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-label">注册会员用户数</div>
                <div class="kpi-value">${totalCustomers} 人</div>
                <div class="kpi-sub">实名绑定收货地址会员</div>
            </div>
        </div>

        <!-- 可视化 Canvas 统计报表（后台扩展功能：销售统计与图表展示） -->
        <div class="chart-grid">
            <div class="card-panel" style="margin-bottom:0;">
                <h3 style="font-size:16px;margin-bottom:14px;">近期每日订单成交额趋势图</h3>
                <canvas id="dailyChart" style="width:100%;height:250px;"></canvas>
            </div>
            <div class="card-panel" style="margin-bottom:0;">
                <h3 style="font-size:16px;margin-bottom:14px;">各数码分类销售额占比分布</h3>
                <canvas id="categoryChart" style="width:100%;height:250px;"></canvas>
            </div>
        </div>

        <div style="display:grid;grid-template-columns:1.35fr 1fr;gap:20px;">
            <div class="card-panel" style="padding:0;overflow:hidden;">
                <div style="padding:16px 20px;border-bottom:1px solid var(--border-light);display:flex;justify-content:space-between;align-items:center;">
                    <h3 style="font-size:15px;">最新提交订单动态</h3>
                    <a href="${ctx}/admin/orders" style="font-size:12px;color:var(--brand-primary);">查看全部 &rarr;</a>
                </div>
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>订单号</th>
                        <th>收货人</th>
                        <th>订单金额</th>
                        <th>状态</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${recentOrders}" var="ro">
                        <tr>
                            <td class="num"><strong>${ro.orderId}</strong></td>
                            <td>${ro.consignee}</td>
                            <td class="num">¥<fmt:formatNumber value="${ro.totalPrice}" pattern="0.00" /></td>
                            <td><span class="status-tag status-${ro.orderState}">${ro.stateLabel}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="card-panel" style="padding:0;overflow:hidden;">
                <div style="padding:16px 20px;border-bottom:1px solid var(--border-light);display:flex;justify-content:space-between;align-items:center;">
                    <h3 style="font-size:15px;">库存监控预警（库存 &le; 45 件）</h3>
                    <a href="${ctx}/admin/products" style="font-size:12px;color:var(--brand-primary);">补货维护 &rarr;</a>
                </div>
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>商品名称</th>
                        <th>剩余库存</th>
                        <th>累计销量</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${lowStockProducts}" var="lp">
                        <tr>
                            <td>${lp.productName}</td>
                            <td class="num"><span class="status-tag status-0">${lp.stock} 件</span></td>
                            <td class="num">${lp.sales} 件</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<script src="${ctx}/static/js/mall.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    var dayLabels = [<c:forEach items="${dailyRevenue}" var="entry" varStatus="st">"${entry.key}"${!st.last ? ',' : ''}</c:forEach>];
    var dayValues = [<c:forEach items="${dailyRevenue}" var="entry" varStatus="st">${entry.value}${!st.last ? ',' : ''}</c:forEach>];
    renderDailyRevenueChart("dailyChart", dayLabels, dayValues);

    var catLabels = [<c:forEach items="${categorySales}" var="entry" varStatus="st">"${entry.key}"${!st.last ? ',' : ''}</c:forEach>];
    var catValues = [<c:forEach items="${categorySales}" var="entry" varStatus="st">${entry.value}${!st.last ? ',' : ''}</c:forEach>];
    renderCategoryDonutChart("categoryChart", catLabels, catValues);
});
</script>
</body>
</html>
