package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.*;
import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.ItemRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.ItemService;
import com.radwija.jumpstartbackend.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl extends OrderUtils implements ItemService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public BigDecimal checkItemTotal(ItemRequest itemRequest) {
        int quantityRequest = itemRequest.getQuantity();
        if (quantityRequest <= 0) {
            throw new InvalidInputException("Quantity input is invalid.");
        }
        Product product = productRepository.findByProductId(itemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        BigDecimal itemPriceTotal = product.getPrice().multiply(BigDecimal.valueOf(quantityRequest));

        Item itemOfProduct = itemRepository.findByProductAndStatus(product, EItemStatus.IN_CART);
        Cart cart = getCurrentUser().getCart();
        List<Item> items = itemRepository.findByCartAndProductIsNotNullAndStatus(cart, EItemStatus.IN_CART);
        BigDecimal cartTotal = BigDecimal.valueOf(0);
        for (Item item : items) {
            cartTotal = cartTotal.add(item.getItemPriceTotal());
        }

        checkCartTotal(itemPriceTotal, cartTotal);

        String errorMessage;
        if (itemOfProduct != null) {
            int quantityOfCartItem = itemOfProduct.getQuantity();
            errorMessage = "Only " + product.getStock() + " left and you already have " + quantityOfCartItem + " of this item in your cart.";
            checkProductStockWithCartItem(itemRequest, errorMessage);
            itemPriceTotal = itemOfProduct.getItemPriceTotal().add(BigDecimal.valueOf(quantityRequest).multiply(BigDecimal.valueOf(quantityOfCartItem)));
        } else {
            errorMessage = "Maximum quantityRequest to purchase this item is " + product.getStock();
            checkProductStockWithCartItem(itemRequest, errorMessage);
        }

        return itemPriceTotal;
    }

    @Override
    public BaseResponse<?> saveCartItem(String email, ItemRequest itemRequest) {
        BaseResponse<Item> response = new BaseResponse<>();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("current user not found"));
            Cart cart = user.getCart();
            Product product = productRepository.findByProductId(itemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("product not found"));
            if (cart == null) {
                throw new CartNotFoundException("cart not found");
            }
            System.out.println("Cart ID: " + cart.getCartId());
            System.out.println(product.getProductId());
            System.out.println("Owned by: " + cart.getUser().getEmail());

            Item item = itemRepository.findByProductAndStatus(product, EItemStatus.IN_CART);
            if (item != null) {
                String requestFrom = "FROM_CART";
                item.setProduct(product);
                item.setItemPriceTotal(checkItemTotal(itemRequest));
                if (itemRequest.getRequestFrom() != null && itemRequest.getRequestFrom().equals(requestFrom)) {
                    item.setQuantity(itemRequest.getQuantity());
                } else {
//                    if (item.getStatus() == EItemStatus.PURCHASED) {
//                        item = new Item();
//                        item.setStatus(EItemStatus.IN_CART);
//                        item.setProduct(product);
//                        item.setQuantity(0);
//                        item.setCreatedAt(new Date());
//                        itemRepository.save(item);
//                    }
                    item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
                }
                item.setCart(cart);
                item.setUpdatedAt(new Date());
                item.setStatus(EItemStatus.IN_CART);

                itemRepository.save(item);

                response.setCode(200);
                response.setMessage("Product added to cart successfully!");
                response.setResult(item);
            } else {
                Item newItem = new Item();
                newItem.setProduct(product);
                newItem.setQuantity(itemRequest.getQuantity());
                newItem.setItemPriceTotal(checkItemTotal(itemRequest));
                newItem.setCart(cart);
                newItem.setCreatedAt(new Date());
                newItem.setStatus(EItemStatus.IN_CART);

                itemRepository.save(newItem);

                response.setCode(200);
                response.setMessage("Product added to cart successfully!");
                response.setResult(newItem);
            }

            return response;
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> deleteCartItemById(String email, Long cartItemId) {
        try {
            Item deletedItem = itemRepository.findById(cartItemId)
                    .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));
            String ownerEmail = deletedItem.getCart().getUser().getEmail();
            if (email != ownerEmail) {
                throw new RefusedActionException("Access denied");
            }
            itemRepository.deleteById(cartItemId);
            return BaseResponse.ok("Cart item ID " + cartItemId + " deleted successfully.");
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }

    }
}
