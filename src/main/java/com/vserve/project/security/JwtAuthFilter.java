package com.vserve.project.security;

import com.vserve.project.entity.Organization;
import com.vserve.project.entity.User;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationActorRepository;

    public JwtAuthFilter(
            JwtService jwtService,
            UserRepository userRepository,
            OrganizationRepository organizationActorRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.organizationActorRepository = organizationActorRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        // If no token, continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {

            // Prevent re-authentication
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtService.parseClaims(token);

            String username = claims.getSubject();
            String accountType = claims.get("accountType", String.class);

            if (username == null || accountType == null) {
                filterChain.doFilter(request, response);
                return;
            }

            switch (accountType) {

                case "USER" -> authenticateUser(token, username);

                case "ORGANIZATION" -> authenticateOrganization(token, username);

                default -> SecurityContextHolder.clearContext();
            }

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token, String username) {

        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserPrincipal principal = new UserPrincipal(user);

        if (!jwtService.isValid(token, principal)) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void authenticateOrganization(String token, String username) {

        Organization organization =
                organizationActorRepository.findByEmail(username);

        if (organization == null) {
            throw new UsernameNotFoundException("Organization not found");
        }

        OrganizationPrincipal principal =
                new OrganizationPrincipal(organization);

        if (!jwtService.isValid(token, principal)) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}