package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.exception.RefusedActionException;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.ProductSnapshotRepository;
import com.radwija.jumpstartbackend.service.ProductSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSnapshotServiceImpl implements ProductSnapshotService {
    @Autowired
    private ProductSnapshotRepository productSnapshotRepository;

    @Override
    public BaseResponse<?> showSnapshotDetailsBySlug(User user, String slug) {
        try {
            ProductSnapshot detailedSnapshot = productSnapshotRepository.findBySlug(slug);
            if (detailedSnapshot == null) {
                throw new ProductNotFoundException("Snapshot not found.");
            }
            if (user != detailedSnapshot.getOrder().getUser()) {
                throw new RefusedActionException("Access denied.");
            }
            return BaseResponse.ok(detailedSnapshot);
        } catch (ProductNotFoundException e) {
            return BaseResponse.notFound(e.getMessage());
        }
    }
}
