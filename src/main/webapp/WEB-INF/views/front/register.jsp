<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>新会员注册 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container">
    <div class="auth-wrap" style="max-width:960px;">
        <div class="auth-aside">
            <div>
                <div style="font-size:12px;letter-spacing:0.08em;opacity:0.7;margin-bottom:12px;">CREATE ACCOUNT</div>
                <h2 style="font-size:26px;line-height:1.3;margin-bottom:14px;">注册星潮数码会员</h2>
                <p style="font-size:14px;color:rgba(255,255,255,0.75);line-height:1.7;">填写基本信息即可完成账号开通，享受会员专属限时折扣、快速结算与全程订单物流状态跟踪服务。</p>
            </div>
            <div style="font-size:12px;color:rgba(255,255,255,0.65);line-height:1.8;">
                <div>· 密码采用 MD5 消息摘要加密安全存储</div>
                <div>· 注册同时配置默认收货地址，下单免重复填写</div>
                <div>· 已有账号？<a href="${ctx}/login" style="color:#fff;text-decoration:underline;">返回直接登录</a></div>
            </div>
        </div>

        <div class="auth-form-pane">
            <h1 style="font-size:22px;font-weight:700;margin-bottom:20px;">填写会员注册信息</h1>
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-error">${errorMsg}</div>
            </c:if>
            <form action="${ctx}/register" method="post">
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                    <div class="form-group">
                        <label>登录用户名 *</label>
                        <input type="text" name="userName" class="form-control" value="${formUser}" placeholder="3~20位字母或数字" required />
                    </div>
                    <div class="form-group">
                        <label>真实姓名 / 昵称</label>
                        <input type="text" name="realName" class="form-control" value="${formRealName}" placeholder="用于收货显示" />
                    </div>
                </div>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                    <div class="form-group">
                        <label>登录密码 *</label>
                        <input type="password" name="password" class="form-control" placeholder="不少于 6 位字符" required />
                    </div>
                    <div class="form-group">
                        <label>确认登录密码 *</label>
                        <input type="password" name="confirmPassword" class="form-control" placeholder="再次输入一致密码" required />
                    </div>
                </div>
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                    <div class="form-group">
                        <label>电子邮箱</label>
                        <input type="email" name="email" class="form-control" value="${formEmail}" placeholder="example@stu.edu.cn" />
                    </div>
                    <div class="form-group">
                        <label>手机号码</label>
                        <input type="text" name="phone" class="form-control" value="${formPhone}" placeholder="11 位大陆手机号" />
                    </div>
                </div>
                <div class="form-group">
                    <label>默认收货地址</label>
                    <input type="text" name="address" class="form-control" value="${formAddress}" placeholder="如：信息工程学院学生公寓 3 号楼" />
                </div>
                <button type="submit" class="btn btn-primary btn-lg" style="width:100%;margin-top:6px;">完成注册并前往登录</button>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
