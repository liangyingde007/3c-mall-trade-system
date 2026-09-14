package com.msb.mall.service;

import com.msb.mall.vo.Cart;
import com.msb.mall.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车的Service接口
 */
public interface ICartService {

    public Cart getCartList();

    CartItem addCart(Long skuId, Integer num) throws ExecutionException, InterruptedException, Exception;

    CartItem getCartItem(Long skuId);

    List<CartItem> getUserCartItems();
}
