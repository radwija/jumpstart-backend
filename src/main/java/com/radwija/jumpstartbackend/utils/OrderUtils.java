package com.radwija.jumpstartbackend.utils;

import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.exception.OutOfProductStockException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.repository.ItemRepository;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.service.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class OrderUtils extends ServiceUtils {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    protected void checkCartTotal(BigDecimal tempItemTotal, BigDecimal cartTotal) {
        BigDecimal cartTotalValidation = tempItemTotal.add(cartTotal);
        if (cartTotalValidation.compareTo(CartServiceImpl.maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }
    }

    protected void checkProductStockWithCartItem(ItemRequest itemRequest, String message) {
        Product product = productRepository.findByProductId(itemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        Item itemOfProduct = itemRepository.findByProduct(product);

        int quantityRequest = itemRequest.getQuantity();
        Long productStock = product.getStock();

        if (itemOfProduct != null) {
            int quantityOfCartItem = itemOfProduct.getQuantity();
            int checkedQuantity = 0;
            if (itemRequest.getRequestFrom() != null && itemRequest.getRequestFrom().equals("FROM_CART")) {
                checkedQuantity = quantityRequest;
            } else {
                checkedQuantity = quantityOfCartItem + quantityRequest;
            }
            if (checkedQuantity > productStock) {
                throw new OutOfProductStockException(message);
            }
        } else {
            if (quantityRequest > productStock) {
                throw new OutOfProductStockException(message);
            }
        }


    }
}
