<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>确认订单信息 - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container" style="padding-top:24px;">
    <div class="section-head">
        <div>
            <h2>核对并提交订单</h2>
            <p>采用 JDBC 手动数据库事务同步写入订单主表、商品明细并扣减实时库存</p>
        </div>
        <a href="${ctx}/cart" class="btn btn-outline btn-sm">&larr; 返回修改购物车</a>
    </div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-error">${errorMsg}</div>
    </c:if>

    <form action="${ctx}/checkout" method="post" style="display:grid;grid-template-columns:1.2fr 1fr;gap:24px;align-items:start;">
        <div class="card-panel">
            <h3 style="font-size:17px;margin-bottom:18px;">收货人配送信息</h3>
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;">
                <div class="form-group">
                    <label>收货人姓名 *</label>
                    <input type="text" name="consignee" class="form-control"
                           value="${not empty sessionScope.loginUser.realName ? sessionScope.loginUser.realName : sessionScope.loginUser.userName}" required />
                </div>
                <div class="form-group">
                    <label>收货联系电话 *</label>
                    <input type="text" name="phone" class="form-control" value="${sessionScope.loginUser.phone}" placeholder="请输入 11 位手机号码" required />
                </div>
            </div>
            <div class="form-group">
                <label>详细收货地址 *</label>
                <input type="text" name="address" class="form-control" value="${sessionScope.loginUser.address}" placeholder="省/市/区/街道/校区楼宇门牌号" required />
            </div>
            <div class="form-group">
                <label>订单配送备注（选填）</label>
                <input type="text" name="remark" class="form-control" placeholder="例如：工作日派送、放置快递柜或礼物包装要求" />
            </div>
        </div>

        <div class="card-panel">
            <h3 style="font-size:17px;margin-bottom:16px;">商品清单（共 ${sessionScope.cart.totalQuantity} 件）</h3>
            <div style="display:grid;gap:12px;margin-bottom:18px;max-height:300px;overflow-y:auto;">
                <c:forEach items="${sessionScope.cart.cartItems}" var="ci">
                    <div style="display:flex;justify-content:space-between;align-items:center;gap:10px;padding-bottom:10px;border-bottom:1px dashed var(--border-light);">
                        <div>
                            <div style="font-weight:600;font-size:13px;">${ci.product.productName}</div>
                            <div class="num" style="font-size:12px;color:var(--text-muted);">
                                ¥<fmt:formatNumber value="${ci.product.salePrice}" pattern="0.00" /> &times; ${ci.quantity}
                            </div>
                        </div>
                        <strong class="num">¥<fmt:formatNumber value="${ci.subtotal}" pattern="0.00" /></strong>
                    </div>
                </c:forEach>
            </div>
            <div class="num" style="display:grid;gap:8px;font-size:13px;padding:12px 0;border-top:1px solid var(--border-light);">
                <div style="display:flex;justify-content:space-between;"><span>运费（满99顺丰包邮）</span><span>¥0.00</span></div>
                <div style="display:flex;justify-content:space-between;color:var(--success);"><span>限时折扣立减</span><span>-¥<fmt:formatNumber value="${sessionScope.cart.totalSaved}" pattern="0.00" /></span></div>
                <div style="display:flex;justify-content:space-between;font-size:18px;font-weight:800;padding-top:8px;">
                    <span>实付总金额</span>
                    <span style="color:var(--accent-sale);">¥<fmt:formatNumber value="${sessionScope.cart.totalAmount}" pattern="0.00" /></span>
                </div>
            </div>
            <button type="submit" class="btn btn-primary btn-lg" style="width:100%;margin-top:14px;">确认无误，立即提交订单</button>
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
