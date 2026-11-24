<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>个人中心与收货地址管理 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container" style="padding-top:24px;">
    <div class="section-head">
        <div>
            <h2>会员个人中心</h2>
            <p>维护个人联系资料、默认收货地址与账号安全密码</p>
        </div>
    </div>

    <c:if test="${not empty successMsg}">
        <div class="alert alert-success">${successMsg}</div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-error">${errorMsg}</div>
    </c:if>

    <div class="kpi-grid" style="grid-template-columns:repeat(3,1fr);">
        <div class="kpi-card">
            <div class="kpi-label">当前登录账号</div>
            <div class="kpi-value" style="font-size:22px;">${sessionScope.loginUser.userName}</div>
            <div class="kpi-sub">注册时间：<fmt:formatDate value="${sessionScope.loginUser.createTime}" pattern="yyyy-MM-dd" /></div>
        </div>
        <div class="kpi-card">
            <div class="kpi-label">历史累计订单</div>
            <div class="kpi-value num">${orderCount} 笔</div>
            <div class="kpi-sub"><a href="${ctx}/orders" style="color:var(--brand-primary);">查看我的全部订单 &rarr;</a></div>
        </div>
        <div class="kpi-card">
            <div class="kpi-label">收藏夹数码单品</div>
            <div class="kpi-value num">${favCount} 款</div>
            <div class="kpi-sub"><a href="${ctx}/favorite" style="color:var(--brand-primary);">管理收藏商品 &rarr;</a></div>
        </div>
    </div>

    <div style="display:grid;grid-template-columns:1.3fr 1fr;gap:24px;">
        <div class="card-panel">
            <h3 style="font-size:17px;margin-bottom:16px;">个人基本资料与默认收货地址</h3>
            <form action="${ctx}/user" method="post">
                <input type="hidden" name="action" value="profile" />
                <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                    <div class="form-group">
                        <label>收货人姓名 / 昵称</label>
                        <input type="text" name="realName" class="form-control" value="${sessionScope.loginUser.realName}" required />
                    </div>
                    <div class="form-group">
                        <label>联系电话</label>
                        <input type="text" name="phone" class="form-control" value="${sessionScope.loginUser.phone}" />
                    </div>
                </div>
                <div class="form-group">
                    <label>电子邮箱</label>
                    <input type="email" name="email" class="form-control" value="${sessionScope.loginUser.email}" />
                </div>
                <div class="form-group">
                    <label>默认收货详细地址（结算时自动带出）</label>
                    <input type="text" name="address" class="form-control" value="${sessionScope.loginUser.address}" />
                </div>
                <button type="submit" class="btn btn-primary">保存资料与收货地址</button>
            </form>
        </div>

        <div class="card-panel">
            <h3 style="font-size:17px;margin-bottom:16px;">修改登录密码</h3>
            <form action="${ctx}/user" method="post">
                <input type="hidden" name="action" value="password" />
                <div class="form-group">
                    <label>当前原密码</label>
                    <input type="password" name="oldPassword" class="form-control" required />
                </div>
                <div class="form-group">
                    <label>设置新密码（不少于 6 位）</label>
                    <input type="password" name="newPassword" class="form-control" required />
                </div>
                <button type="submit" class="btn btn-dark">确认更新密码</button>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
