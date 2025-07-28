package br.hallel.relational.api.app.security.ministry;

import br.hallel.relational.api.app.ministry.service.MinistryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinistryCoordinatorFilter extends HttpFilter {

    private final TokenCoordinatorMinistry tokenCoordinatorMinistry;
    private final MinistryService ministryService;


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException,
            ServletException {
        String token = tokenCoordinatorMinistry.resolveToken(request);

        if (token != null && tokenCoordinatorMinistry.validateToken(token)) {
            Authentication authentication = tokenCoordinatorMinistry.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
