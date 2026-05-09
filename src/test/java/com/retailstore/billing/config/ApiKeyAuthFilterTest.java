package com.retailstore.billing.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ApiKeyAuthFilterTest {

    private static final String VALID_API_KEY = "test-api-key-123";
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ApiKeyAuthFilter apiKeyAuthFilter;

    @BeforeEach
    void setUp() {
        apiKeyAuthFilter = new ApiKeyAuthFilter(VALID_API_KEY);

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========== VALID API KEY BRANCH (if condition = true) ==========

    @Test
    void shouldAuthenticateWithValidApiKey() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(API_KEY_HEADER)).thenReturn(VALID_API_KEY);

        // Act
        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert - Filter chain continues
        verify(filterChain).doFilter(request, response);

        // Assert - No error response
        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setContentType(anyString());

        // Assert - Authentication is set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("api-client", authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_API")));
    }

    // ========== INVALID/MISSING API KEY BRANCH (else condition) ==========

    @Test
    void shouldRejectInvalidApiKey() throws ServletException, IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader(API_KEY_HEADER)).thenReturn("wrong-api-key");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert - Filter chain does NOT continue
        verify(filterChain, never()).doFilter(request, response);

        // Assert - Error response sent
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseBody = stringWriter.toString();
        assertEquals("{\"error\": \"Invalid or missing API key\"}", responseBody);

        // Assert - Authentication is cleared
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldRejectMissingApiKey() throws ServletException, IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader(API_KEY_HEADER)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.101");

        // Act
        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert - Filter chain does NOT continue
        verify(filterChain, never()).doFilter(request, response);

        // Assert - Error response sent
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseBody = stringWriter.toString();
        assertEquals("{\"error\": \"Invalid or missing API key\"}", responseBody);

        // Assert - Authentication is cleared
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldRejectEmptyApiKey() throws ServletException, IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getHeader(API_KEY_HEADER)).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Act
        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
