package com.example.owner.model; // 确保包名正确

public class LoginResponse {
    private boolean authenticated; // 根据后端返回字段定义
    private String userType;       // 添加字段 (如 "user" 或 "restaurant")

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}