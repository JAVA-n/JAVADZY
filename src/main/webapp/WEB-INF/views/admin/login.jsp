<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>管理员登录 - 星潮商城运营后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body style="background:#141518;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:24px;">
<div style="width:100%;max-width:440px;background:#fff;border-radius:16px;padding:38px 36px;box-shadow:0 24px 64px rgba(0,0,0,0.4);">
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:22px;">
        <div>
            <div style="font-size:11px;font-weight:700;letter-spacing:0.08em;color:var(--brand-primary);">ADMIN CONSOLE</div>
            <h1 style="font-size:22px;font-weight:800;margin-top:2px;">商城后台管理系统</h1>
        </div>
        <button type="button" class="btn btn-outline btn-sm"
                onclick="document.getElementById('adminName').value='admin';document.getElementById('adminPwd').value='admin123';">
            一键填入管理员
        </button>
    </div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-error">${errorMsg}</div>
    </c:if>

    <form action="${ctx}/admin/login" method="post">
        <div class="form-group">
            <label for="adminName">管理员账号</label>
            <input type="text" id="adminName" name="userName" class="form-control" value="admin" placeholder="默认账号：admin" required />
        </div>
        <div class="form-group">
            <label for="adminPwd">管理员密码</label>
            <input type="password" id="adminPwd" name="password" class="form-control" placeholder="默认密码：admin123" required />
        </div>
        <button type="submit" class="btn btn-dark btn-lg" style="width:100%;margin-top:8px;">登录进入管理后台</button>
    </form>

    <div style="margin-top:20px;padding-top:16px;border-top:1px solid var(--border-light);display:flex;justify-content:space-between;font-size:12px;color:var(--text-secondary);">
        <span>默认账号：<code>admin</code> / <code>admin123</code></span>
        <a href="${ctx}/index" style="color:var(--brand-primary);font-weight:600;">返回商城前台 &rarr;</a>
    </div>
</div>
</body>
</html>
