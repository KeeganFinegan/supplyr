package com.supplyr.supplyr.security;

public enum ApplicationUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    ORGANISATIONAL_UNIT_READ("organisational_unit:read"),
    ORGANISATIONAL_UNIT_WRITE("organisational_unit:write"),
    ASSET_READ("asset:read"),
    ASSET_WRITE("asset:write"),
    TRADE_READ("trade:read"),
    TRADE_WRITE("trade:write"),
    TRADE_HISTORY_READ("trade_history:read"),
    TRADE_HISTORY_WRITE("trade_history:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
