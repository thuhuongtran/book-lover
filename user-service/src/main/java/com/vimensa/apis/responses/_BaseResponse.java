package com.vimensa.apis.responses;

import com.vimensa.config.ErrorCode;
import lombok.Data;

@Data
public abstract class _BaseResponse {
    private int error = ErrorCode.SUCCESS;
    public boolean isSuccess(){
        return error == ErrorCode.SUCCESS;
    }
    public abstract String toJonString();
}
