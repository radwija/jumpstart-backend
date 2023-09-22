package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.*;
import com.radwija.jumpstartbackend.payload.request.CartItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CartItemRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.CartItemService;
import com.radwija.jumpstartbackend.utils.OrderUtils;
import com.radwija.jumpstartbackend.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CartItemServiceImpl extends OrderUtils implements CartItemService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public BigDecimal checkItemTotal(CartItemRequest cartItemRequest) {
        int quantityRequest = cartItemRequest.getQuantity();
        if (quantityRequest <= 0) {
            throw new InvalidInputException("Quantity input is invalid.");
        }
        Product product = productRepository.findByProductId(cartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantityRequest));

        CartItem cartItemOfProduct = cartItemRepository.findByProduct(product);
        Cart cart = getCurrentUser().getCart();
        List<CartItem> cartItems = cart.getCartItems();
        BigDecimal cartTotal = BigDecimal.valueOf(0);
        for (CartItem cartItem : cartItems) {
            cartTotal = cartTotal.add(cartItem.getItemTotal());
        }

        String errorMessage;
        if (cartItemOfProduct != null) {
            int quantityOfCartItem = cartItemOfProduct.getQuantity();
            errorMessage = "Only " + product.getStock() + " left and you already have " + quantityOfCartItem + " of this item in your cart.";
            checkProductStockWithCartItem(cartItemRequest, errorMessage);
            itemTotal = cartItemOfProduct.getItemTotal().add(BigDecimal.valueOf(quantityRequest).multiply(BigDecimal.valueOf(quantityOfCartItem)));
        } else {
            errorMessage = "Maximum quantityRequest to purchase this item is " + product.getStock();
            checkProductStockWithCartItem(cartItemRequest, errorMessage);
        }

        checkCartTotal(itemTotal, cartTotal);

        return itemTotal;
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
            System.out.println(product.getProductId());
            System.out.println("Owned by: " + cart.getUser().getEmail());

            CartItem cartItem = cartItemRepository.findByProduct(product);
            if (cartItem != null) {
                String requestFrom = "FROM_CART";
                cartItem.setProduct(product);
                cartItem.setItemTotal(checkItemTotal(cartItemRequest));
                if (cartItemRequest.getRequestFrom() != null && cartItemRequest.getRequestFrom().equals(requestFrom)) {
                    cartItem.setQuantity(cartItemRequest.getQuantity());
                } else {
                    cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
                }
                cartItem.setCart(cart);
                cartItem.setUpdatedAt(new Date());

                cartItemRepository.save(cartItem);

                response.setCode(200);
                response.setMessage("Product added to cart successfully!");
                response.setResult(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setProduct(product);
                newCartItem.setQuantity(cartItemRequest.getQuantity());
                newCartItem.setItemTotal(checkItemTotal(cartItemRequest));
                newCartItem.setCart(cart);
                newCartItem.setCreatedAt(new Date());

                cartItemRepository.save(newCartItem);

                response.setCode(200);
                response.setMessage("Product added to cart successfully!");
                response.setResult(newCartItem);
            }

            return response;
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }
}
