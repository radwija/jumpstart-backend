package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.CartNotFoundException;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.exception.OutOfProductStockException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.CartItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CartItemRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.CartItemService;
import com.radwija.jumpstartbackend.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartItemServiceImpl extends ServiceUtils implements CartItemService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public BigDecimal checkItemTotal(CartItemRequest cartItemRequest) {
//        try {
        BigDecimal itemTotal;
        int quantity = cartItemRequest.getQuantity();
        Product product = productRepository.findByProductId(cartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        CartItem cartItem = cartItemRepository.findByProduct(product);
        Cart cart = getCurrentUser().getCart();
        if (cartItem != null) {
            itemTotal = cartItem.getItemTotal();
            if ((cartItem.getQuantity() + cartItemRequest.getQuantity()) > product.getStock()) {
                throw new OutOfProductStockException("Your item quantity in your cart is out of stock.");
            }
        } else {
            itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            if (cartItemRequest.getQuantity() > product.getStock()) {
                throw new OutOfProductStockException("Quantity is out of stock.");
            }
        }

        if ((itemTotal.add(cart.getTotal())).compareTo(CartServiceImpl.maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }

        return itemTotal;
//        } catch (RuntimeException e) {
//            System.out.println(e.getMessage());
//            return null;
//        }
    }

    @Override
    public BaseResponse<?> saveCartItem(String email, CartItemRequest cartItemRequest) {
        BaseResponse<CartItem> response = new BaseResponse<>();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("current user not found"));
            Cart cart = user.getCart();
            Product product = productRepository.findByProductId(cartItemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("product not found"));
            if (cart == null) {
                throw new CartNotFoundException("cart not found");
            }
            System.out.println("Cart ID: " + cart.getCartId());
            System.out.println("Owned by: " + cart.getUser().getUserId());

            CartItem cartItem = cartItemRepository.findByProduct(product);
            if (cartItem == null) {
                CartItem newCartItem = new CartItem();

            } else {

            }
            checkItemTotal(cartItemRequest);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItem.getQuantity());
            cartItem.setCart(cart);

            cartItemRepository.save(cartItem);

            return response;
        } catch (RuntimeException e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }
}
