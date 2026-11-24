<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>系统操作日志 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>后台关键操作审计日志</h1>
                <p style="font-size:13px;color:var(--text-secondary);">自动记录管理员登录、价格折扣调整、商品上下架与订单状态变更轨迹</p>
            </div>
        </div>

        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>日志编号</th>
                    <th>操作管理员</th>
                    <th>所属模块</th>
                    <th>操作详情描述</th>
                    <th>来源 IP</th>
                    <th>记录时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${logs}" var="lg">
                    <tr>
                        <td class="num">#${lg.logId}</td>
                        <td><strong>${lg.adminName}</strong></td>
                        <td><span class="status-tag status-1">${lg.moduleName}</span></td>
                        <td>${lg.actionDesc}</td>
                        <td class="num">${lg.ipAddr}</td>
                        <td class="num"><fmt:formatDate value="${lg.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>
