package com.radwija.jumpstartbackend.utils;

import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.entity.*;
import com.radwija.jumpstartbackend.exception.CartNotFoundException;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.exception.OutOfProductStockException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.radwija.jumpstartbackend.service.impl.CartServiceImpl.maxAmount;

public class OrderUtils extends ServiceUtils {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductSnapshotRepository productSnapshotRepository;

    protected void checkCartTotal(BigDecimal tempItemTotal, BigDecimal cartTotal) {
        BigDecimal cartTotalValidation = tempItemTotal.add(cartTotal);
        if (cartTotalValidation.compareTo(maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }
    }

    protected void checkProductStockWithCartItem(ItemRequest itemRequest, String message) {
        Product product = productRepository.findByProductId(itemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("product not found"));
        Item itemOfProduct = itemRepository.findByProductAndStatus(product, EItemStatus.IN_CART);

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

    protected BigDecimal checkTotal(List<Item> items) {
        BigDecimal total = BigDecimal.valueOf(0);
        for (Item item : items) {
            total = total.add(item.getItemPriceTotal());
        }

        if (total.compareTo(maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }

        return total;
    }


    public BaseResponse<?> saveNewOrder(User user) {
        try {
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found."));
            List<Item> items = itemRepository.findAllByCartAndStatus(cart, EItemStatus.IN_CART);
            Order newOrder = new Order();
            newOrder.setUser(user);
            convertCartItemsToSnapshots(newOrder, items);

            newOrder.setCreatedAt(new Date());
            orderRepository.save(newOrder);
            return BaseResponse.ok(newOrder);
        } catch (Exception e) {
            System.out.println("ini error");
            System.out.println(e.getMessage());
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    public void convertCartItemsToSnapshots(Order order, List<Item> items) {
        for (Item item : items) {
            ProductSnapshot snapshot = new ProductSnapshot();
            Product product = item.getProduct();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            Date snapshotAt = new Date();
            String formattedDate = dateFormat.format(snapshotAt);

            snapshot.setOrder(order);
            snapshot.setProduct(product);
            snapshot.setProductName(product.getProductName());
            snapshot.setSlug("snapshot_" +
                    product.getSlug() +
                    item.getItemId() +
                    "_" + formattedDate
            );
            snapshot.setDescription(product.getDescription());
            snapshot.setPrice(product.getPrice());
            snapshot.setWeight(product.getWeight());
            snapshot.setProductCreatedAt(product.getCreatedAt());
            snapshot.setLastUpdatedAt(product.getUpdatedAt());
            snapshot.setSnapshotAt(snapshotAt);

            productSnapshotRepository.save(snapshot);
        }
    }
}
