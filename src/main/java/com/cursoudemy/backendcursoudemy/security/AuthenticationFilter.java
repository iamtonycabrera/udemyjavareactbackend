package com.cursoudemy.backendcursoudemy.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cursoudemy.backendcursoudemy.SpringApplicationContext;
import com.cursoudemy.backendcursoudemy.models.requests.UserLoginRequestModel;
import com.cursoudemy.backendcursoudemy.services.UserServiceInterface;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // Al intentar login primero se ejecuta este metodo
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Copiando todas las propiedas que vienen desde request (email y
            // password) a la clase UserLoginRequestModel
            UserLoginRequestModel userModel = new ObjectMapper().readValue(request.getInputStream(),
                    UserLoginRequestModel.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userModel.getEmail(),
                    userModel.getPassword(),
                    new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Si el login esta bien se ejecuta este segundo metodo
    /// Se crea el token
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        String username = ((User) authentication.getPrincipal()).getUsername();

        String token = Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
            .signWith(SignatureAlgorithm.HS512, SecurityConstants.getSecretToken())
            .compact();

        UserServiceInterface userService = (UserServiceInterface) SpringApplicationContext.getBeans("userService");

        UserDto userDto = userService.getUser(username);

        response.addHeader("Access-Control-Expose-Headers", "Authorization, UserId");
        response.addHeader("UserID", userDto.getUserId());
        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }
}
