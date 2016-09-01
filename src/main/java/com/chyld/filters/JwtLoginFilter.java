package com.chyld.filters;

import com.chyld.dtos.AuthDto;
import com.chyld.services.UserService;
import com.chyld.utilities.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import com.chyld.services.UserService;

import com.chyld.entities.*;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private String username;

    public JwtLoginFilter(String defaultFilterProcessesUrl, JwtUtil jwtUtil, UserService userService, AuthenticationManager authManager) {
        super(defaultFilterProcessesUrl);

        this.jwtUtil = jwtUtil;
        this.userService = userService;
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final AuthDto auth = new ObjectMapper().readValue(request.getInputStream(), AuthDto.class);
        username = auth.getUsername();

        User attemptedUser = (User) userService.loadUserByUsername(username);

        if( attemptedUser != null)
        {
            if(attemptedUser.isEnabled()) {
                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(auth.getUsername(), auth.getPassword());
                return getAuthenticationManager().authenticate(authToken);
            }
            else {
                response.sendError(401, "Account Is Locked");
                throw new LockedException("Account Is Locked");
            }
        }


        throw new AuthenticationServiceException("user not found");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        User authenticatedUser = (User) userService.loadUserByUsername(authResult.getName());
        authenticatedUser.setNumInvalidLogins(0);
        userService.saveUser(authenticatedUser);
        String token = jwtUtil.generateToken(authenticatedUser);
        response.setHeader("Authorization", "Bearer " + token);
        SecurityContextHolder.getContext().setAuthentication(jwtUtil.tokenFromStringJwt(token));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        User unauthenticatedUser = (User) userService.loadUserByUsername(this.username);

        if (unauthenticatedUser != null && unauthenticatedUser.isAccountNonLocked()) {
            int x = unauthenticatedUser.getNumInvalidLogins();
            x++;
            unauthenticatedUser.setNumInvalidLogins(x);
            if (x==3) { unauthenticatedUser.setEnabled(false);}

            userService.saveUser(unauthenticatedUser);
        }

    }
}
