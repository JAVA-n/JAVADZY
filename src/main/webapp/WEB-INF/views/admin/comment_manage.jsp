<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>商品评价审核 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>用户商品评价审核管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">审核买家发表的星级评价内容，支持隐藏屏蔽不当言论或彻底删除</p>
            </div>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>

        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>评价商品</th>
                    <th>会员账号</th>
                    <th>星级</th>
                    <th>评价内容</th>
                    <th>发表时间</th>
                    <th>状态</th>
                    <th style="text-align:right;">审核操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${comments}" var="cm">
                    <tr>
                        <td class="num">#${cm.commentId}</td>
                        <td style="max-width:200px;font-weight:600;">${cm.productName}</td>
                        <td>${cm.userName}</td>
                        <td class="num" style="color:#d97706;font-weight:700;">${cm.rating} 星</td>
                        <td style="max-width:320px;">${cm.content}</td>
                        <td class="num"><fmt:formatDate value="${cm.createTime}" pattern="yyyy-MM-dd HH:mm" /></td>
                        <td>
                            <span class="status-tag ${cm.status == 1 ? 'status-2' : 'status-0'}">
                                ${cm.status == 1 ? '公开显示' : '已屏蔽'}
                            </span>
                        </td>
                        <td style="text-align:right;">
                            <div style="display:inline-flex;gap:6px;">
                                <form action="${ctx}/admin/comments" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="toggle" />
                                    <input type="hidden" name="commentId" value="${cm.commentId}" />
                                    <input type="hidden" name="newStatus" value="${cm.status == 1 ? 0 : 1}" />
                                    <button type="submit" class="btn btn-outline btn-sm">${cm.status == 1 ? '屏蔽' : '恢复显示'}</button>
                                </form>
                                <form action="${ctx}/admin/comments" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="delete" />
                                    <input type="hidden" name="commentId" value="${cm.commentId}" />
                                    <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                </form>
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
