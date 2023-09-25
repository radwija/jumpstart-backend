package com.radwija.jumpstartbackend.service.impl;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.CartNotFoundException;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.ItemRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.OrderService;
import com.radwija.jumpstartbackend.service.TransactionService;
import com.radwija.jumpstartbackend.utils.OrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public BaseResponse<?> createPayment(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found."));
        List<com.radwija.jumpstartbackend.entity.Item>
                items = itemRepository.findByCartAndProductIsNotNullAndStatus(cart, EItemStatus.IN_CART);
        BigDecimal total = new BigDecimal("0");

        for (com.radwija.jumpstartbackend.entity.Item item : items) {
            total = total.add(item.getItemPriceTotal());
        }

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("USD").value(total.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                // TODO: save token from capture?token=[token] to database so admin can complete and cancel
                // complete = execute this end point (/capture?token)
                // cancel = find out token expiration or set null token in database
                .returnUrl("http://localhost:3000/capture")
                .cancelUrl("http://localhost:3000/cancel");
        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);
        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();

            BaseResponse<?> orderRes = orderService.saveNewOrder(user);
            if (orderRes.getCode() != 200) {
                throw new Exception(orderRes.getMessage());
            }

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return BaseResponse.ok("success", redirectUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> completePayment(User user, String token) {
        OrdersCaptureRequest ordersCreateRequest = new OrdersCaptureRequest(token);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found."));
        List<com.radwija.jumpstartbackend.entity.Item>
                items = itemRepository.findByCartAndProductIsNotNullAndStatus(cart, EItemStatus.IN_CART);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = httpResponse.result();
            List<PurchaseUnit> purchaseUnits = order.purchaseUnits();
            if (httpResponse.result().status() != null) {
                PurchaseUnit purchaseUnit = purchaseUnits.get(0);
                Capture capture = purchaseUnit.payments().captures().get(0);

                for (com.radwija.jumpstartbackend.entity.Item item : items) {
                    item.setStatus(EItemStatus.PURCHASED);
//                    item.setProduct(null);
                    itemRepository.save(item);
                }

                String captureId = capture.id();
                System.out.println("amount() " + capture.amount().value());
                System.out.println("captureStatusDetails() " + capture.captureStatusDetails()); //null
                System.out.println("createTime() " + capture.createTime());
                System.out.println("finalCapture() " + capture.finalCapture());
                System.out.println("id() " + capture.id());
                System.out.println("invoiceId() " + capture.invoiceId());
                System.out.println("links() " + capture.links());
                System.out.println("sellerProtection() " + capture.sellerProtection());
                System.out.println("status() " + capture.status());
                System.out.println("updateTime() " + capture.updateTime());
                System.out.println("captureId: " + captureId);
                return BaseResponse.ok("success", capture);
            }
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
        return BaseResponse.badRequest("error");
    }
}
