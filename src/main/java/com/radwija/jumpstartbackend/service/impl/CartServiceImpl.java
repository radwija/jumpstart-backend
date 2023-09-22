package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.CartNotFoundException;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.payload.response.CartDto;
import com.radwija.jumpstartbackend.repository.CartItemRepository;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    public static BigDecimal maxAmount = new BigDecimal("9999999.99");

    @Override
    public BigDecimal checkTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.valueOf(0);
        for (CartItem cartItem : items) {
            total = total.add(cartItem.getItemPriceTotal());
        }

        if (total.compareTo(maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }

        return total;
    }

    @Override
    public BaseResponse<?> addProductToCart(String currentUserEmail, Long productId) {
        CartItem cartItem = new CartItem();
        Cart cart = new Cart();
        return null;
    }

    @Override
    public BaseResponse<?> getMyCart(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("current user not found"));
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new CartNotFoundException("cart not found"));
            List<CartItem> cartItems = cart.getCartItems();
            int itemNumbers = 0;

            for (CartItem cartItem : cartItems) {
                itemNumbers += cartItem.getQuantity();
            }

            CartDto result = new CartDto();
            result.setCartId(cart.getCartId());
            result.setUserId(user.getUserId());
            result.setCartSize(cartItems.size());
            result.setItemNumbers(itemNumbers);
            result.setTotal(checkTotal(cartItems));
            result.setCartItems(cartItems);

            return BaseResponse.ok("success", result);
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }
}
