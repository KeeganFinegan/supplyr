package com.supplyr.supplyr.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.supplyr.supplyr.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    USER(Sets.newHashSet(
            USER_READ, USER_WRITE,
            ORGANISATIONAL_UNIT_READ,
            ASSET_READ, TRADE_READ, TRADE_WRITE,
            TRADE_HISTORY_READ, TRADE_HISTORY_WRITE
    )),
    ADMIN(Sets.newHashSet(
            USER_READ, USER_WRITE, ORGANISATIONAL_UNIT_READ, ORGANISATIONAL_UNIT_WRITE,
            ASSET_READ, ASSET_WRITE, TRADE_READ, TRADE_WRITE, TRADE_HISTORY_READ,
            TRADE_HISTORY_WRITE
    ));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
