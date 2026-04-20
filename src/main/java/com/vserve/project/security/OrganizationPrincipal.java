package com.vserve.project.security;

import com.vserve.project.entity.Organization;
import com.vserve.project.enums.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class OrganizationPrincipal implements UserDetails {

    private final Organization organization;

    public OrganizationPrincipal(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ORGANIZATION"));
    }

    @Override
    public String getPassword() {
        return organization.getPassword();
    }

    @Override
    public String getUsername() {
        return organization.getEmail(); // login by email
    }

    public Long getOrgId() {
        return organization.getId();
    }

    @Override
    public boolean isEnabled() {
        return organization.getStatus() == AccountStatus.ACTIVE;
    }
}