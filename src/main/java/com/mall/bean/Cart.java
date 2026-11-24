package com.mall.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 HttpSession 存储的购物车容器类，内部采用 LinkedHashMap 维护商品编号与明细映射。
 *
 * @author 张三
 * @studentId 20240301
 */
public class Cart implements Serializable {
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    public void addItem(Product product, int count) {
        if (product == null || count <= 0) {
            return;
        }
        CartItem existing = items.get(product.getProductId());
        if (existing == null) {
            int finalQty = Math.min(count, Math.max(1, product.getStock()));
            items.put(product.getProductId(), new CartItem(product, finalQty));
        } else {
            existing.setProduct(product); // 同步最新价格与折扣
            int merged = Math.min(existing.getQuantity() + count, Math.max(1, product.getStock()));
            existing.setQuantity(merged);
        }
    }

    public void updateQuantity(Integer productId, int quantity) {
        CartItem existing = items.get(productId);
        if (existing != null) {
            if (quantity <= 0) {
                items.remove(productId);
            } else {
                int maxStock = existing.getProduct().getStock() != null ? existing.getProduct().getStock() : 999;
                existing.setQuantity(Math.min(quantity, Math.max(1, maxStock)));
            }
        }
    }

    public void removeItem(Integer productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public Collection<CartItem> getCartItems() {
        return items.values();
    }

    public Map<Integer, CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalQuantity() {
        int sum = 0;
        for (CartItem item : items.values()) {
            sum += item.getQuantity();
        }
        return sum;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public BigDecimal getTotalSaved() {
        BigDecimal saved = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            saved = saved.add(item.getSavedAmount());
        }
        return saved;
    }
}
