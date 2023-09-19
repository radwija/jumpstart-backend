package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class BaseResponse<DataTypeRoot> {
    private int code = 403;
    String message = "Unauthorized";
    DataTypeRoot result = null;

    public static <DataType> BaseResponse<?> ok (String msg) {
        BaseResponse<DataType> response = new BaseResponse<>();
        response.code = 200;
        response.message = msg;

        return response;
    }

    public static <DataType> BaseResponse<?> ok (DataType result) {
        BaseResponse<DataType> response = new BaseResponse<>();
        response.code = 200;
        response.message = "success";
        response.result = result;

        return response;
    }

    public static <DataType> BaseResponse<?> ok (String msg, DataType result) {
        BaseResponse<DataType> response = new BaseResponse<>();
        response.code = 200;
        response.message = msg;
        response.result = result;

        return response;
    }

    public static BaseResponse<?> badRequest(String msg) {
        BaseResponse<?> response = new BaseResponse<>();
        response.code = 400;
        response.message = msg;
        return response;
    }

    public static BaseResponse<?> unauthorized(String msg) {
        BaseResponse<?> response = new BaseResponse<>();
        response.code = 400;
        response.message = msg;
        return response;
    }

    public static BaseResponse<?> forbidden() {
        BaseResponse<?> response = new BaseResponse<>();
        response.code = 403;
        return response;
    }

    public static BaseResponse<?> notFound(String msg) {
        BaseResponse<?> response = new BaseResponse<>();
        response.code = 404;
        response.message = msg;
        return response;
    }
}
