package com.serverside.responses;

public class LoginResponse extends BasicResponse {
    private int id;

    public LoginResponse(boolean success, Integer errorCode, int id) {
        super(success, errorCode);
        this.id = id;
    }

    public LoginResponse(boolean success, Integer errorCode) {
        super(success, errorCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
