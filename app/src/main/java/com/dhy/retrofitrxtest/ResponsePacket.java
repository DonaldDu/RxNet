package com.dhy.retrofitrxtest;

import com.dhy.retrofitrxutil.IResponseStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class ResponsePacket<DATA extends Serializable> implements Serializable, IResponseStatus {
    public DATA data;

    public void setMessage(String message) {
        if (result == null) result = new Result();
        result.message = message;
        result.error_code = LOCALL_ERROR;
    }

    @Override
    public boolean isSuccess() {
        return result != null && result.error_code == 0;
    }

    @Override
    public int getCode() {
        return result != null ? result.error_code : LOCALL_ERROR;
    }

    @Override
    public String getMessage() {
        return (result != null ? result.message : "");
    }

    @Override
    public String getDebugMessage() {
        return "CODE " + getCode() + ": " + (result != null ? result.message : "");
    }

    private Result result;

    private static class Result {
        @SerializedName("code")
        int error_code;
        @SerializedName("desc")
        String message;
    }

    public Map<String, String> validResult;
}
