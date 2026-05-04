package team.anonyms.converter.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import team.anonyms.converter.services.frontend.AuthenticationService;
import team.anonyms.converter.services.frontend.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class RequestFilter extends OncePerRequestFilter {
    // Paths under /api/v1 accessible only for verified users
    private static final String[] RESTRICTED_PATHS = {"/patterns", "/modifications"};

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public RequestFilter(
            AuthenticationService authenticationService,
            JwtService jwtService
    ) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    private String extractJwtToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * <p>
     *     Authenticates JWT token for requests.
     * </p>
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        boolean hasValidToken = false;
        boolean isVerified = false;

        // Authenticate user by JWT token
        String jwtToken = extractJwtToken(request);
        if (jwtService.isValid(jwtToken)) {
            hasValidToken = true;

            String userId = jwtService.extractUserId(jwtToken);
            isVerified = authenticationService.isVerified(UUID.fromString(userId));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(UUID.fromString(userId), null, List.of())
            );
        }

        boolean isConversionRequest = request.getRequestURI().contains("/conversion");
        boolean hasPatternParam = request.getParameter("pattern") != null;

        if (isConversionRequest && hasPatternParam && (!hasValidToken || !isVerified)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!isVerified) {
            for (String restrictedPath : RESTRICTED_PATHS) {
                if (request.getRequestURI().contains(restrictedPath)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
