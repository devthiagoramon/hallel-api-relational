package br.hallel.relational.api.app.security.ministry;

import br.hallel.relational.api.app.ministry.service.MinistryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (request.getRequestURI()
                   .contains("/coordinator")) {
            String token = request.getHeader("coordenador-token");

            if (token == null || !tokenCoordinatorMinistry.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token for a coordinator or isn't a coordinator");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
