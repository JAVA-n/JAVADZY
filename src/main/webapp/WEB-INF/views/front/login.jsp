<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>会员登录 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container">
    <div class="auth-wrap">
        <div class="auth-aside">
            <div>
                <div style="font-size:12px;letter-spacing:0.08em;opacity:0.7;margin-bottom:12px;">MEMBER ACCESS</div>
                <h2 style="font-size:26px;line-height:1.3;margin-bottom:14px;">欢迎回到星潮数码商城</h2>
                <p style="font-size:14px;color:rgba(255,255,255,0.75);line-height:1.7;">登录后即可同步商品收藏夹、管理常用收货地址、实时追踪订单发货状态并发表真实购物评价。</p>
            </div>
            <div style="background:rgba(255,255,255,0.08);border-radius:10px;padding:16px;font-size:12px;line-height:1.8;">
                <div style="font-weight:700;margin-bottom:4px;">内置测试会员账号（点击右侧一键填入）：</div>
                <div>账号：<code>zhangsan</code> &nbsp;&nbsp; 密码：<code>123456</code></div>
                <div>后台管理员请前往：<a href="${ctx}/admin/login" style="text-decoration:underline;color:#93c5fd;">后台管理端登录</a></div>
            </div>
        </div>

        <div class="auth-form-pane">
            <div style="display:flex;justify-content:space-between;align-items:baseline;margin-bottom:24px;">
                <h1 style="font-size:22px;font-weight:700;">用户账号登录</h1>
                <button type="button" class="btn btn-outline btn-sm"
                        onclick="document.getElementById('userName').value='zhangsan';document.getElementById('password').value='123456';">
                    一键填入演示账号
                </button>
            </div>

            <c:if test="${param.registered == '1'}">
                <div class="alert alert-success">账号注册成功，请使用刚注册的用户名和密码登录！</div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-error">${errorMsg}</div>
            </c:if>

            <form action="${ctx}/login" method="post">
                <input type="hidden" name="redirect" value="${redirect}" />
                <div class="form-group">
                    <label for="userName">登录用户名</label>
                    <input type="text" id="userName" name="userName" class="form-control"
                           value="${rememberedUser}" placeholder="请输入用户名（如 zhangsan）" required />
                </div>
                <div class="form-group">
                    <label for="password">登录密码</label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="请输入登录密码" required />
                </div>
                <div style="display:flex;justify-content:space-between;align-items:center;margin:14px 0 22px;font-size:13px;">
                    <label style="display:inline-flex;align-items:center;gap:6px;cursor:pointer;color:var(--text-secondary);">
                        <input type="checkbox" name="remember" value="1" ${not empty rememberedUser ? 'checked' : ''} />
                        记住用户名（Cookie 保存 7 天）
                    </label>
                    <a href="${ctx}/register" style="color:var(--brand-primary);font-weight:600;">还没有账号？立即注册</a>
                </div>
                <button type="submit" class="btn btn-primary btn-lg" style="width:100%;">立即登录</button>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
