<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${product.productName} - 星潮数码配件商城</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<jsp:include page="/WEB-INF/views/front/common/header.jsp" />

<main class="container">
    <div style="margin-top:18px;font-size:13px;color:var(--text-secondary);">
        <a href="${ctx}/index">首页</a> &nbsp;/&nbsp;
        <a href="${ctx}/products?categoryId=${product.categoryId}">${product.categoryName}</a> &nbsp;/&nbsp;
        <span style="color:var(--text-primary);font-weight:500;">${product.productName}</span>
    </div>

    <div class="detail-card">
        <div class="detail-gallery">
            <img src="${ctx}/${product.image}" alt="${product.productName}" />
        </div>

        <div class="detail-meta">
            <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
                <span class="status-tag status-1">${product.categoryName}</span>
                <c:if test="${product.hasDiscount}">
                    <span class="status-tag" style="background:var(--accent-sale-bg);color:var(--accent-sale);">限时 ${product.discountLabel} 特惠</span>
                </c:if>
            </div>
            <h1>${product.productName}</h1>
            <p class="detail-subtitle">${product.subtitle}</p>

            <div class="detail-price-box">
                <div class="detail-price-main">
                    <span class="detail-price-now">¥<fmt:formatNumber value="${product.salePrice}" pattern="0.00" /></span>
                    <c:if test="${product.hasDiscount}">
                        <span class="price-old" style="font-size:15px;">原价 ¥<fmt:formatNumber value="${product.price}" pattern="0.00" /></span>
                    </c:if>
                </div>
                <div class="num" style="font-size:13px;color:var(--text-secondary);">
                    累计销量 <strong>${product.sales}</strong> 件 &nbsp;|&nbsp; 现货库存 <strong>${product.stock}</strong> 件
                </div>
            </div>

            <div class="spec-box">
                <div class="spec-row">
                    <span>技术规格</span>
                    <strong>${product.specs}</strong>
                </div>
                <div class="spec-row">
                    <span>服务承诺</span>
                    <span>官方正品直营 · 7天无理由退换 · 18个月只换不修质保</span>
                </div>
            </div>

            <div style="display:flex;align-items:center;gap:14px;flex-wrap:wrap;">
                <form action="${ctx}/cart" method="post" style="display:flex;align-items:center;gap:12px;flex-wrap:wrap;">
                    <input type="hidden" name="action" value="add" />
                    <input type="hidden" name="productId" value="${product.productId}" />
                    <div class="qty-stepper" data-stepper>
                        <button type="button" data-minus>-</button>
                        <input type="number" name="quantity" value="1" min="1" max="${product.stock}" class="num" />
                        <button type="button" data-plus>+</button>
                    </div>
                    <button type="submit" class="btn btn-primary btn-lg">加入购物车</button>
                    <button type="submit" name="buyNow" value="1" class="btn btn-dark btn-lg">立即购买</button>
                </form>

                <form action="${ctx}/favorite" method="post">
                    <input type="hidden" name="action" value="toggle" />
                    <input type="hidden" name="productId" value="${product.productId}" />
                    <input type="hidden" name="from" value="detail" />
                    <button type="submit" class="btn btn-outline btn-lg">
                        ${isFavorited ? '已收藏（点击取消）' : '收藏该商品'}
                    </button>
                </form>
            </div>

            <div style="margin-top:24px;padding-top:18px;border-top:1px solid var(--border-light);">
                <div style="font-weight:700;margin-bottom:6px;">产品详情介绍</div>
                <p style="color:var(--text-secondary);line-height:1.75;">${product.description}</p>
            </div>
        </div>
    </div>

    <!-- 商品评价与发表评论模块（前台扩展功能） -->
    <section id="reviews" class="card-panel">
        <div class="section-head" style="margin-bottom:16px;">
            <div>
                <h2 style="font-size:19px;">用户真实评价（${comments.size()} 条）</h2>
                <p>购买使用心得与星级评分反馈</p>
            </div>
        </div>

        <c:if test="${param.commented == '1'}">
            <div class="alert alert-success">感谢您的评价！评论已发布展示。</div>
        </c:if>

        <c:choose>
            <c:when test="${empty comments}">
                <p style="color:var(--text-muted);padding:16px 0;">暂无用户评价，欢迎成为首位晒单分享的用户。</p>
            </c:when>
            <c:otherwise>
                <div style="display:grid;gap:14px;margin-bottom:24px;">
                    <c:forEach items="${comments}" var="cm">
                        <div style="padding:14px 18px;background:var(--bg-page);border-radius:8px;border:1px solid var(--border-light);">
                            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;">
                                <div style="display:flex;align-items:center;gap:10px;">
                                    <strong>${cm.userName}</strong>
                                    <span style="color:#d97706;font-weight:700;font-size:12px;">${cm.rating} 星好评</span>
                                </div>
                                <span class="num" style="font-size:12px;color:var(--text-muted);">
                                    <fmt:formatDate value="${cm.createTime}" pattern="yyyy-MM-dd HH:mm" />
                                </span>
                            </div>
                            <div style="color:var(--text-primary);line-height:1.65;">${cm.content}</div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <form action="${ctx}/comment" method="post" style="border-top:1px solid var(--border-light);padding-top:18px;">
                    <input type="hidden" name="productId" value="${product.productId}" />
                    <div style="display:flex;align-items:center;gap:14px;margin-bottom:12px;">
                        <label style="font-weight:600;">发表我的评价：</label>
                        <select name="rating" class="form-control" style="width:150px;padding:6px 10px;">
                            <option value="5">5 星 - 非常满意</option>
                            <option value="4">4 星 - 比较满意</option>
                            <option value="3">3 星 - 一般</option>
                            <option value="2">2 星 - 有待改进</option>
                            <option value="1">1 星 - 不满意</option>
                        </select>
                    </div>
                    <div style="display:flex;gap:12px;">
                        <input type="text" name="content" class="form-control" placeholder="分享做工质感、快充速度或使用体验..." required />
                        <button type="submit" class="btn btn-primary">提交评价</button>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <div style="font-size:13px;color:var(--text-secondary);">
                    <a href="${ctx}/login?redirect=/product?id=${product.productId}" style="color:var(--brand-primary);font-weight:600;">登录会员账号</a> 后即可发表商品评价。
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<jsp:include page="/WEB-INF/views/front/common/footer.jsp" />
</body>
</html>
