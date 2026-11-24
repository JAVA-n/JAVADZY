<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>轮播广告管理 - 星潮商城后台</title>
    <link rel="stylesheet" href="${ctx}/static/css/mall.css" />
</head>
<body>
<div class="admin-shell">
    <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

    <main class="admin-main">
        <div class="admin-topbar">
            <div>
                <h1>首页轮播广告配置管理</h1>
                <p style="font-size:13px;color:var(--text-secondary);">动态配置商城前台首屏焦点海报、促销文案、跳转链接与展示顺序</p>
            </div>
        </div>

        <c:if test="${not empty msg}">
            <div class="alert alert-success">${msg}</div>
        </c:if>

        <div class="card-panel">
            <h3 style="font-size:16px;margin-bottom:14px;">新增首页轮播海报</h3>
            <form action="${ctx}/admin/banners" method="post" style="display:grid;grid-template-columns:1.4fr 1.6fr 1fr 1.3fr 1fr auto;gap:12px;align-items:end;">
                <input type="hidden" name="action" value="add" />
                <div>
                    <label style="font-size:12px;font-weight:600;">主标题 *</label>
                    <input type="text" name="title" class="form-control" placeholder="如：开学季数码焕新专场" required />
                </div>
                <div>
                    <label style="font-size:12px;font-weight:600;">副标题说明</label>
                    <input type="text" name="subtitle" class="form-control" placeholder="活动卖点补充文案" />
                </div>
                <div>
                    <label style="font-size:12px;font-weight:600;">角标文字</label>
                    <input type="text" name="badge" class="form-control" value="官方精选" />
                </div>
                <div>
                    <label style="font-size:12px;font-weight:600;">海报素材选择</label>
                    <select name="imageUrl" class="form-control">
                        <option value="static/images/banner_audio.jpg">声学耳机场景海报</option>
                        <option value="static/images/banner_desk.jpg">极简桌面键鼠海报</option>
                        <option value="static/images/banner_power.jpg">氮化镓快充矩阵海报</option>
                    </select>
                </div>
                <div>
                    <label style="font-size:12px;font-weight:600;">跳转路径</label>
                    <input type="text" name="linkUrl" class="form-control" value="products?discount=1" />
                </div>
                <button type="submit" class="btn btn-primary">添加海报</button>
            </form>
        </div>

        <div class="card-panel" style="padding:0;overflow:hidden;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>海报预览</th>
                    <th>标题与文案</th>
                    <th>跳转链接</th>
                    <th>排序</th>
                    <th>状态</th>
                    <th style="text-align:right;">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${banners}" var="b">
                    <tr>
                        <td class="num">#${b.bannerId}</td>
                        <td>
                            <img src="${ctx}/${b.imageUrl}" alt="${b.title}"
                                 style="width:120px;height:62px;object-fit:cover;border-radius:6px;border:1px solid var(--border-light);" />
                        </td>
                        <td>
                            <div style="font-weight:700;">${b.title}</div>
                            <div style="font-size:12px;color:var(--text-secondary);">${b.subtitle}</div>
                        </td>
                        <td><code>${b.linkUrl}</code></td>
                        <td class="num">${b.sortOrder}</td>
                        <td>
                            <span class="status-tag ${b.status == 1 ? 'status-2' : 'status-3'}">
                                ${b.status == 1 ? '展示中' : '已停用'}
                            </span>
                        </td>
                        <td style="text-align:right;">
                            <div style="display:inline-flex;gap:6px;">
                                <form action="${ctx}/admin/banners" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="toggle" />
                                    <input type="hidden" name="bannerId" value="${b.bannerId}" />
                                    <input type="hidden" name="newStatus" value="${b.status == 1 ? 0 : 1}" />
                                    <button type="submit" class="btn btn-outline btn-sm">${b.status == 1 ? '停用' : '启用'}</button>
                                </form>
                                <form action="${ctx}/admin/banners" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="delete" />
                                    <input type="hidden" name="bannerId" value="${b.bannerId}" />
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
