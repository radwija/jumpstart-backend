package com.radwija.jumpstartbackend.utils;

import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.exception.OutOfProductStockException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.CartItemRequest;
import com.radwija.jumpstartbackend.repository.CartItemRepository;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.service.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class OrderUtils extends ServiceUtils {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    protected void checkCartTotal(BigDecimal tempItemTotal, BigDecimal cartTotal) {
        BigDecimal cartTotalValidation = tempItemTotal.add(cartTotal);
        if (cartTotalValidation.compareTo(CartServiceImpl.maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }
    }

    protected void checkProductStockWithCartItem(CartItemRequest cartItemRequest, String message) {
        Product product = productRepository.findByProductId(cartItemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        CartItem cartItemOfProduct = cartItemRepository.findByProduct(product);

        int quantityRequest = cartItemRequest.getQuantity();
        Long productStock = product.getStock();

        if (cartItemOfProduct != null) {
            int quantityOfCartItem = cartItemOfProduct.getQuantity();
            if (quantityOfCartItem + quantityRequest > productStock) {
                throw new OutOfProductStockException(message);
            }
        } else {
            if (quantityRequest > productStock) {
                throw new OutOfProductStockException(message);
            }
        }


    }
}
