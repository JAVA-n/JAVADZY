<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>商品分类管理 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>数码商品分类管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">维护前台展示分类名称、导购简介与排序权重</p>
            </div>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>
        <c:if test="${not empty err}">
            <div class="alert alert-error">${err}</div>
        </c:if>

        <div style="display:grid;grid-template-columns:1fr 1.6fr;gap:22px;align-items:start;">
            <div class="card-panel">
                <h3 style="font-size:16px;margin-bottom:16px;">${not empty editCategory ? '修改商品分类' : '新增商品分类'}</h3>
                <form action="${ctx}/admin/categories" method="post">
                    <input type="hidden" name="action" value="save" />
                    <input type="hidden" name="categoryId" value="${editCategory.categoryId}" />
                    <div class="form-group">
                        <label>分类名称 *</label>
                        <input type="text" name="categoryName" value="${editCategory.categoryName}" class="form-control" placeholder="例如：桌面无线充电" required />
                    </div>
                    <div class="form-group">
                        <label>分类简介描述</label>
                        <input type="text" name="categoryDesc" value="${editCategory.categoryDesc}" class="form-control" placeholder="简要说明该分类包含的数码品类" />
                    </div>
                    <div class="form-group">
                        <label>展示排序权重（数字越小越靠前）</label>
                        <input type="number" name="sortOrder" value="${not empty editCategory ? editCategory.sortOrder : 6}" class="form-control num" />
                    </div>
                    <div style="display:flex;gap:10px;">
                        <button type="submit" class="btn btn-primary">${not empty editCategory ? '保存修改' : '确认创建分类'}</button>
                        <c:if test="${not empty editCategory}">
                            <a href="${ctx}/admin/categories" class="btn btn-outline">取消编辑</a>
                        </c:if>
                    </div>
                </form>
            </div>

            <div class="card-panel" style="padding:0;overflow:hidden;">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>分类编号</th>
                        <th>分类名称</th>
                        <th>分类导购描述</th>
                        <th>关联商品</th>
                        <th>排序</th>
                        <th style="text-align:right;">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${categories}" var="cat">
                        <tr>
                            <td class="num">#${cat.categoryId}</td>
                            <td><strong>${cat.categoryName}</strong></td>
                            <td>${cat.categoryDesc}</td>
                            <td class="num"><span class="status-tag status-1">${cat.productCount} 款</span></td>
                            <td class="num">${cat.sortOrder}</td>
                            <td style="text-align:right;">
                                <div style="display:inline-flex;gap:6px;">
                                    <a href="${ctx}/admin/categories?editId=${cat.categoryId}" class="btn btn-outline btn-sm">编辑</a>
                                    <form action="${ctx}/admin/categories" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="categoryId" value="${cat.categoryId}" />
                                        <button type="submit" class="btn btn-danger btn-sm">删除</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>
