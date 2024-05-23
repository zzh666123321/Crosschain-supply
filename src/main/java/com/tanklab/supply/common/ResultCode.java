package com.tanklab.supply.common;

public enum ResultCode {
    TEST("0","Test Msg"),
    ERROR("1000","Unknown Error Occurred."),
    SUCCESS("1001","Request Successfully!"),
    USER_EMAIL_EXIST("1002","User Email Exist!"),
    LOGIN_FAIL("1003","Login Failed."),
    PRIPSWD_ERROR("1004","PrivateKey Password Error!"),
    PSWD_ERROR("1005","Password Error!"),
    TOKEN_ERROR("1006","Token Error!"),
    NOT_MATCH_ERROR("1007","Not Match Error!"),

    PROCESS_ERROR("1008","The Ox Cannot Process Again!")
    ;
    public String Code;
    public String Msg;
    ResultCode(String code, String msg) {
        this.Code = code;
        this.Msg = msg;
    }
}
