package com.vserve.project.security;

import com.vserve.project.entity.Organization;
import com.vserve.project.repository.OrganizationRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrganizationDetailsService implements UserDetailsService {

    private final OrganizationRepository organizationRepository;

    public OrganizationDetailsService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Organization organization =
                organizationRepository.findByEmail(email);

        if (organization == null) {
            throw new UsernameNotFoundException("Organization not found");
        }

        return new OrganizationPrincipal(organization);
    }
}