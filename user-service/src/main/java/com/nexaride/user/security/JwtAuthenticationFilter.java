package com.nexaride.user.security;

import com.nexaride.user.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Inside doFilterInternal method...");

        //Getting Header
        final String authHeader = request.getHeader("Authorization");
        log.info("AuthHeader value: "+authHeader);

        //If no header or invalid → pass request
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        //Extract token
        String token = authHeader.substring(7);

        //Extract email
        String email = jwtService.extractUsername(token);
        Claims claims = jwtService.getClaims(token);
        String role = claims.get("role",String.class);
        log.info("Extracted email form token is : "+email);

        //Validate and set auth
        if(email != null && SecurityContextHolder.getContext().getAuthentication()==null){
            log.info("Inside if condition to validate token and set auth object in Security context ");
            if(jwtService.validateToken(token,email)){
                log.info("Validation of token successful so inside condition");

                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email,null,authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //Continue filter chain
        filterChain.doFilter(request,response);

    }
}
