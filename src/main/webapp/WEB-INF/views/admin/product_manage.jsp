<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>商品与折扣管理 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>商品上下架与价格折扣管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">支持 Multipart 图片上传发布、库存维护、实时折扣率调节与一键上下架</p>
            </div>
            <form action="${ctx}/admin/products" method="get" style="display:flex;gap:8px;align-items:center;">
                <select name="categoryId" class="form-control" style="width:140px;">
                    <option value="0">全部分类</option>
                    <c:forEach items="${categories}" var="c">
                        <option value="${c.categoryId}" ${selectedCategoryId == c.categoryId ? 'selected' : ''}>${c.categoryName}</option>
                    </c:forEach>
                </select>
                <select name="status" class="form-control" style="width:120px;">
                    <option value="-1" ${selectedStatus == -1 ? 'selected' : ''}>全部状态</option>
                    <option value="1" ${selectedStatus == 1 ? 'selected' : ''}>上架销售中</option>
                    <option value="0" ${selectedStatus == 0 ? 'selected' : ''}>已下架</option>
                </select>
                <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="商品名称关键字..." style="width:180px;" />
                <button type="submit" class="btn btn-dark btn-sm">筛选</button>
            </form>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>
        <c:if test="${not empty err}">
            <div class="alert alert-error">${err}</div>
        </c:if>

        <!-- 新增 / 编辑商品表单面板 -->
        <div class="card-panel">
            <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px;">
                <h3 style="font-size:16px;">${not empty editProduct ? '编辑商品详情：'.concat(editProduct.productName) : '快速发布新数码商品'}</h3>
                <c:if test="${not empty editProduct}">
                    <a href="${ctx}/admin/products" class="btn btn-outline btn-sm">切换为新增模式</a>
                </c:if>
            </div>
            <form action="${ctx}/admin/products" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="save" />
                <input type="hidden" name="productId" value="${editProduct.productId}" />
                <input type="hidden" name="existingImage" value="${editProduct.image}" />
                <div style="display:grid;grid-template-columns:2fr 1.2fr 1fr 1fr 1fr;gap:12px;margin-bottom:12px;">
                    <div>
                        <label style="font-size:12px;font-weight:600;">商品名称 *</label>
                        <input type="text" name="productName" value="${editProduct.productName}" class="form-control" placeholder="输入商品完整标题" required />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">所属分类 *</label>
                        <select name="categoryId" class="form-control">
                            <c:forEach items="${categories}" var="c">
                                <option value="${c.categoryId}" ${editProduct.categoryId == c.categoryId ? 'selected' : ''}>${c.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">商品原价 (元) *</label>
                        <input type="number" step="0.01" name="price" value="${not empty editProduct ? editProduct.price : '299.00'}" class="form-control num" required />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">折扣率 (0.10~1.00)</label>
                        <input type="number" step="0.01" min="0.10" max="1.00" name="discount" value="${not empty editProduct ? editProduct.discount : '0.90'}" class="form-control num" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">现货库存</label>
                        <input type="number" name="stock" value="${not empty editProduct ? editProduct.stock : '80'}" class="form-control num" />
                    </div>
                </div>
                <div style="display:grid;grid-template-columns:1.5fr 1.5fr 1.2fr 0.8fr 0.8fr auto;gap:12px;align-items:end;">
                    <div>
                        <label style="font-size:12px;font-weight:600;">卖点副标题</label>
                        <input type="text" name="subtitle" value="${editProduct.subtitle}" class="form-control" placeholder="核心卖点简述" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">技术规格参数</label>
                        <input type="text" name="specs" value="${editProduct.specs}" class="form-control" placeholder="如：PD3.1 / 蓝牙5.4 / CNC铝合金" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">上传主图 (@MultipartConfig)</label>
                        <input type="file" name="imageFile" accept="image/*" class="form-control" style="padding:7px;" />
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">热销推荐</label>
                        <select name="isHot" class="form-control">
                            <option value="1" ${editProduct.isHot == 1 ? 'selected' : ''}>首页推荐</option>
                            <option value="0" ${editProduct.isHot == 0 ? 'selected' : ''}>普通</option>
                        </select>
                    </div>
                    <div>
                        <label style="font-size:12px;font-weight:600;">上架状态</label>
                        <select name="status" class="form-control">
                            <option value="1" ${empty editProduct or editProduct.status == 1 ? 'selected' : ''}>立即上架</option>
                            <option value="0" ${not empty editProduct and editProduct.status == 0 ? 'selected' : ''}>暂存下架</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">${not empty editProduct ? '保存商品修改' : '发布上架'}</button>
                </div>
                <input type="hidden" name="description" value="${not empty editProduct.description ? editProduct.description : '星潮官方自营严选数码配件，支持全国联保与 7 天无理由退换。'}" />
            </form>
        </div>

        <!-- 商品与快捷折扣调节列表 -->
        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>商品图文信息</th>
                    <th>分类</th>
                    <th>原价 / 折后价</th>
                    <th>快捷折扣率设置</th>
                    <th>库存 / 销量</th>
                    <th>状态</th>
                    <th style="text-align:right;">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${products}" var="p">
                    <tr>
                        <td class="num">#${p.productId}</td>
                        <td>
                            <div style="display:flex;align-items:center;gap:12px;">
                                <img src="${ctx}/${p.image}" alt="${p.productName}"
                                     style="width:48px;height:48px;object-fit:cover;border-radius:6px;border:1px solid var(--border-light);" />
                                <div>
                                    <div style="font-weight:600;max-width:250px;">${p.productName}</div>
                                    <div style="font-size:11px;color:var(--text-muted);">${p.subtitle}</div>
                                </div>
                            </div>
                        </td>
                        <td>${p.categoryName}</td>
                        <td class="num">
                            <div style="font-weight:700;color:var(--accent-sale);">¥<fmt:formatNumber value="${p.salePrice}" pattern="0.00" /></div>
                            <div style="font-size:11px;color:var(--text-muted);">原价 ¥<fmt:formatNumber value="${p.price}" pattern="0.00" /></div>
                        </td>
                        <td>
                            <form action="${ctx}/admin/products" method="post" style="display:inline-flex;align-items:center;gap:6px;">
                                <input type="hidden" name="action" value="quickDiscount" />
                                <input type="hidden" name="productId" value="${p.productId}" />
                                <input type="hidden" name="isHot" value="${p.isHot}" />
                                <input type="number" step="0.05" min="0.10" max="1.00" name="discount" value="${p.discount}"
                                       class="form-control num" style="width:74px;padding:5px 8px;font-size:12px;" />
                                <button type="submit" class="btn btn-outline btn-sm">改折扣</button>
                            </form>
                        </td>
                        <td class="num">
                            <div>库存 <strong>${p.stock}</strong></div>
                            <div style="font-size:11px;color:var(--text-muted);">已售 ${p.sales}</div>
                        </td>
                        <td>
                            <span class="status-tag ${p.status == 1 ? 'status-2' : 'status-3'}">
                                ${p.status == 1 ? '在售' : '已下架'}
                            </span>
                        </td>
                        <td style="text-align:right;">
                            <div style="display:inline-flex;gap:6px;">
                                <a href="${ctx}/admin/products?editId=${p.productId}" class="btn btn-outline btn-sm">编辑</a>
                                <form action="${ctx}/admin/products" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="toggleStatus" />
                                    <input type="hidden" name="productId" value="${p.productId}" />
                                    <input type="hidden" name="newStatus" value="${p.status == 1 ? 0 : 1}" />
                                    <button type="submit" class="btn btn-outline btn-sm">${p.status == 1 ? '下架' : '上架'}</button>
                                </form>
                                <form action="${ctx}/admin/products" method="post" style="display:inline;"
                                      onsubmit="return confirm('确认删除该商品？');">
                                    <input type="hidden" name="action" value="delete" />
                                    <input type="hidden" name="productId" value="${p.productId}" />
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
