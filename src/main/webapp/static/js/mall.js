/**
 * 星潮数码配件商城 - 原生 JavaScript 交互脚本（轮播图控制、数量步进器、后台统计图表绘制）
 */
document.addEventListener("DOMContentLoaded", function () {
    // 1. 首页轮播图自动切换与交互
    const carousel = document.querySelector("[data-carousel]");
    if (carousel) {
        const slides = carousel.querySelectorAll(".carousel-slide");
        const dots = carousel.querySelectorAll(".carousel-dot");
        const prevBtn = carousel.querySelector(".carousel-prev");
        const nextBtn = carousel.querySelector(".carousel-next");
        let current = 0;
        let timer = null;

        function showSlide(index) {
            if (!slides.length) return;
            current = (index + slides.length) % slides.length;
            slides.forEach(function (s, idx) {
                s.classList.toggle("active", idx === current);
            });
            dots.forEach(function (d, idx) {
                d.classList.toggle("active", idx === current);
            });
        }

        function startAuto() {
            stopAuto();
            if (slides.length > 1) {
                timer = setInterval(function () {
                    showSlide(current + 1);
                }, 4800);
            }
        }

        function stopAuto() {
            if (timer) clearInterval(timer);
        }

        if (prevBtn) {
            prevBtn.addEventListener("click", function () {
                showSlide(current - 1);
                startAuto();
            });
        }
        if (nextBtn) {
            nextBtn.addEventListener("click", function () {
                showSlide(current + 1);
                startAuto();
            });
        }
        dots.forEach(function (dot, idx) {
            dot.addEventListener("click", function () {
                showSlide(idx);
                startAuto();
            });
        });
        carousel.addEventListener("mouseenter", stopAuto);
        carousel.addEventListener("mouseleave", startAuto);
        startAuto();
    }

    // 2. 商品详情页购买数量加减器
    document.querySelectorAll("[data-stepper]").forEach(function (box) {
        const input = box.querySelector("input[type='number']");
        const minus = box.querySelector("[data-minus]");
        const plus = box.querySelector("[data-plus]");
        if (!input) return;
        const max = parseInt(input.getAttribute("max") || "999", 10);
        if (minus) {
            minus.addEventListener("click", function () {
                const v = Math.max(1, (parseInt(input.value, 10) || 1) - 1);
                input.value = v;
            });
        }
        if (plus) {
            plus.addEventListener("click", function () {
                const v = Math.min(max, (parseInt(input.value, 10) || 1) + 1);
                input.value = v;
            });
        }
    });
});

/**
 * 后台统计报表：使用原生 HTML5 Canvas 绘制每日营收柱状趋势图
 */
function renderDailyRevenueChart(canvasId, labels, values) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !canvas.getContext) return;
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    const width = rect.width || 560;
    const height = rect.height || 260;
    canvas.width = width * dpr;
    canvas.height = height * dpr;
    const ctx = canvas.getContext("2d");
    ctx.scale(dpr, dpr);

    const padL = 58, padR = 24, padT = 28, padB = 38;
    const plotW = width - padL - padR;
    const plotH = height - padT - padB;
    const maxVal = Math.max.apply(null, values.concat([500])) * 1.18;

    ctx.clearRect(0, 0, width, height);
    ctx.font = "12px -apple-system, 'PingFang SC', sans-serif";

    // 网格水平参考线
    const steps = 4;
    for (let i = 0; i <= steps; i++) {
        const y = padT + (plotH / steps) * i;
        const val = Math.round(maxVal * (1 - i / steps));
        ctx.strokeStyle = "#eceae4";
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(padL, y);
        ctx.lineTo(width - padR, y);
        ctx.stroke();

        ctx.fillStyle = "#8b8d94";
        ctx.textAlign = "right";
        ctx.fillText("¥" + val, padL - 8, y + 4);
    }

    if (!labels.length) return;
    const stepX = plotW / labels.length;
    const barW = Math.min(34, stepX * 0.48);
    const points = [];

    labels.forEach(function (label, idx) {
        const val = values[idx] || 0;
        const barH = (val / maxVal) * plotH;
        const cx = padL + stepX * idx + stepX / 2;
        const x = cx - barW / 2;
        const y = padT + plotH - barH;
        points.push({ x: cx, y: y, val: val });

        // 绘制圆角柱体
        ctx.fillStyle = "#dce7ff";
        ctx.fillRect(x, y, barW, barH);
        ctx.fillStyle = "#1a56db";
        ctx.fillRect(x, y, barW, Math.min(4, barH));

        // X 轴日期
        ctx.fillStyle = "#5c5e64";
        ctx.textAlign = "center";
        ctx.fillText(label, cx, height - 12);
    });

    // 叠加趋势折线与数值点
    ctx.strokeStyle = "#1a56db";
    ctx.lineWidth = 2.2;
    ctx.beginPath();
    points.forEach(function (pt, idx) {
        if (idx === 0) ctx.moveTo(pt.x, pt.y);
        else ctx.lineTo(pt.x, pt.y);
    });
    ctx.stroke();

    points.forEach(function (pt) {
        ctx.fillStyle = "#ffffff";
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, 4, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = "#1a56db";
        ctx.lineWidth = 2;
        ctx.stroke();

        ctx.fillStyle = "#151619";
        ctx.textAlign = "center";
        ctx.fillText("¥" + Math.round(pt.val), pt.x, pt.y - 9);
    });
}

/**
 * 后台统计报表：使用原生 HTML5 Canvas 绘制各数码分类销售额环形占比图
 */
function renderCategoryDonutChart(canvasId, labels, values) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !canvas.getContext) return;
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    const width = rect.width || 420;
    const height = rect.height || 260;
    canvas.width = width * dpr;
    canvas.height = height * dpr;
    const ctx = canvas.getContext("2d");
    ctx.scale(dpr, dpr);

    const colors = ["#1a56db", "#16794b", "#d97706", "#c84228", "#6d28d9", "#0f766e"];
    const total = values.reduce(function (a, b) { return a + b; }, 0) || 1;
    const cx = 120;
    const cy = height / 2;
    const outerR = Math.min(88, height / 2 - 22);
    const innerR = outerR * 0.58;

    let startAngle = -Math.PI / 2;
    values.forEach(function (val, idx) {
        const slice = (val / total) * Math.PI * 2;
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, outerR, startAngle, startAngle + slice);
        ctx.closePath();
        ctx.fillStyle = colors[idx % colors.length];
        ctx.fill();
        startAngle += slice;
    });

    // 环形中心镂空
    ctx.beginPath();
    ctx.arc(cx, cy, innerR, 0, Math.PI * 2);
    ctx.fillStyle = "#ffffff";
    ctx.fill();

    ctx.fillStyle = "#8b8d94";
    ctx.font = "11px -apple-system, 'PingFang SC', sans-serif";
    ctx.textAlign = "center";
    ctx.fillText("分类累计营收", cx, cy - 4);
    ctx.fillStyle = "#151619";
    ctx.font = "bold 14px -apple-system, 'PingFang SC', sans-serif";
    ctx.fillText("¥" + Math.round(total), cx, cy + 15);

    // 右侧图例与百分比
    const legendX = 232;
    let legendY = cy - (labels.length * 28) / 2 + 10;
    ctx.font = "12px -apple-system, 'PingFang SC', sans-serif";
    labels.forEach(function (name, idx) {
        const val = values[idx] || 0;
        const pct = ((val / total) * 100).toFixed(1) + "%";
        ctx.fillStyle = colors[idx % colors.length];
        ctx.fillRect(legendX, legendY - 9, 10, 10);

        ctx.fillStyle = "#151619";
        ctx.textAlign = "left";
        ctx.fillText(name, legendX + 16, legendY);

        ctx.fillStyle = "#5c5e64";
        ctx.textAlign = "right";
        ctx.fillText("¥" + val.toFixed(0) + " (" + pct + ")", width - 12, legendY);
        legendY += 28;
    });
}
