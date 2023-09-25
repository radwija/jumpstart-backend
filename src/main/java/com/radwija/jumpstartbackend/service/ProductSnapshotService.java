package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface ProductSnapshotService {
    BaseResponse<?> showSnapshotDetailsBySlug(User user, String slug);
}
