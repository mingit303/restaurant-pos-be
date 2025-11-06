package com.example.restaurant.config;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class NgrokBypassFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("ngrok-skip-browser-warning", "true");
        chain.doFilter(request, response);
    }
}

