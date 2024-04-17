package com.crmmarketingdigitalback2024.securityConfig.filter;

import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.IRepository;
import com.crmmarketingdigitalback2024.repository.user.IUserRepository;
import com.crmmarketingdigitalback2024.service.Login.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    IUserRepository repository;
    JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = authHeader.split(" ")[1];

        String email = jwtService.extractUsername(jwt);

        UserEntity user = repository.findByEmail(email).get();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                email, null, user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);

    }


}
