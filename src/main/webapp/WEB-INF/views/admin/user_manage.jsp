<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>用户管理 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>注册用户信息管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">支持按账号/姓名/手机号检索、修改资料、重置默认密码及在途订单保护删除</p>
            </div>
            <form action="${ctx}/admin/users" method="get" style="display:flex;gap:8px;">
                <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="搜索用户名、姓名或手机..." style="width:240px;" />
                <button type="submit" class="btn btn-dark btn-sm">查询</button>
                <c:if test="${not empty keyword}">
                    <a href="${ctx}/admin/users" class="btn btn-outline btn-sm">重置</a>
                </c:if>
            </form>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>
        <c:if test="${not empty err}">
            <div class="alert alert-error">${err}</div>
        </c:if>

        <c:if test="${not empty editUser}">
            <div class="card-panel">
                <h3 style="font-size:16px;margin-bottom:14px;">编辑用户资料：${editUser.userName}</h3>
                <form action="${ctx}/admin/users" method="post" style="display:grid;grid-template-columns:repeat(5,1fr);gap:12px;align-items:end;">
                    <input type="hidden" name="action" value="update" />
                    <input type="hidden" name="userId" value="${editUser.userId}" />
                    <div>
                        <label style="font-size:12px;font-weight:600;">姓名/昵称</label>
                        <input type="text" name="realName" value="${editUser.realName}" class="form-control" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">手机号</label>
                        <input type="text" name="phone" value="${editUser.phone}" class="form-control" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">邮箱</label>
                        <input type="text" name="email" value="${editUser.email}" class="form-control" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">收货地址</label>
                        <input type="text" name="address" value="${editUser.address}" class="form-control" />
                    </div>
                    <div style="display:flex;gap:8px;">
                        <select name="status" class="form-control">
                            <option value="1" ${editUser.status == 1 ? 'selected' : ''}>正常启用</option>
                            <option value="0" ${editUser.status == 0 ? 'selected' : ''}>冻结禁用</option>
                        </select>
                        <button type="submit" class="btn btn-primary">保存</button>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>登录账号</th>
                    <th>真实姓名</th>
                    <th>联系电话 / 邮箱</th>
                    <th>默认收货地址</th>
                    <th>角色</th>
                    <th>状态</th>
                    <th>注册时间</th>
                    <th style="text-align:right;">管理操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${users}" var="u">
                    <tr>
                        <td class="num">${u.userId}</td>
                        <td><strong>${u.userName}</strong></td>
                        <td>${u.realName}</td>
                        <td class="num">
                            <div>${u.phone}</div>
                            <div style="font-size:11px;color:var(--text-muted);">${u.email}</div>
                        </td>
                        <td style="max-width:210px;">${u.address}</td>
                        <td>
                            <span class="status-tag ${u.role == 1 ? 'status-1' : 'status-3'}">
                                ${u.role == 1 ? '系统管理员' : '注册会员'}
                            </span>
                        </td>
                        <td>
                            <span class="status-tag ${u.status == 1 ? 'status-2' : 'status-0'}">
                                ${u.status == 1 ? '正常' : '禁用'}
                            </span>
                        </td>
                        <td class="num"><fmt:formatDate value="${u.createTime}" pattern="yyyy-MM-dd" /></td>
                        <td style="text-align:right;">
                            <div style="display:inline-flex;gap:6px;">
                                <a href="${ctx}/admin/users?editId=${u.userId}" class="btn btn-outline btn-sm">编辑</a>
                                <c:if test="${u.role == 0}">
                                    <form action="${ctx}/admin/users" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="resetPwd" />
                                        <input type="hidden" name="userId" value="${u.userId}" />
                                        <button type="submit" class="btn btn-outline btn-sm">重置密码</button>
                                    </form>
                                    <form action="${ctx}/admin/users" method="post" style="display:inline;"
                                          onsubmit="return confirm('确定要删除用户 ${u.userName} 吗？');">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="userId" value="${u.userId}" />
                                        <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                    </form>
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
