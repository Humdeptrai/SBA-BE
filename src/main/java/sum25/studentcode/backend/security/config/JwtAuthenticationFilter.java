package sum25.studentcode.backend.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sum25.studentcode.backend.security.jwt.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Extract JWT token from Authorization header
            final String authHeader = request.getHeader("Authorization");

            // 2. Check if header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Extract token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(7);

            // 4. Extract username from token
            final String username = jwtUtils.extractUsername(jwt);

            // 5. Validate token and authenticate user
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token
                if (jwtUtils.validateToken(jwt)) {

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Set additional details
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("User '{}' authenticated successfully", username);
                }
            }

        } catch (Exception e) {
            // Log exception and add to request attribute for error handling
            log.error("JWT Authentication failed: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
