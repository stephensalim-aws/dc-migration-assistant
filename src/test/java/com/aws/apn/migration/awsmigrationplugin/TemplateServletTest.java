package com.aws.apn.migration.awsmigrationplugin;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import static org.junit.jupiter.api.Assertions.fail;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemplateServletTest {

    private static final String DUMMY_LOGIN_URL_STRING = "dummy-app.com/login";
    private static final URI DUMMY_LOGIN_URI = URI.create(DUMMY_LOGIN_URL_STRING);

    private TemplateServlet sut;

    @Mock
    private SoyTemplateRenderer mockRenderer;

    @Mock
    private PermissionEnforcer mockPermissionEnforcer;

    @Mock
    private LoginUriProvider mockLoginUriProvider;

    @Spy
    private HttpServletResponse mockResp;

    @Mock
    private HttpServletRequest mockReq;

    @BeforeEach
    public void init() {
        sut = new TemplateServlet(mockRenderer, mockPermissionEnforcer, mockLoginUriProvider);
    }

    @Test
    public void itShouldReturnHtmlPageWhenSuccessful() {
        when(mockPermissionEnforcer.isSystemAdmin()).thenReturn(true);
        try {
            sut.doGet(mockReq, mockResp);
            verify(mockResp).setContentType(TEXT_HTML);
        } catch(IOException ioe) {
            fail();
        }
    }

    @Test
    public void itShouldReturnForbiddenWhenNotSysAdmin() {
        when(mockPermissionEnforcer.isSystemAdmin()).thenReturn(false);
        when(mockPermissionEnforcer.isAuthenticated()).thenReturn(true);

        try {
            sut.doGet(mockReq, mockResp);
            verify(mockResp).sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch(IOException ioe) {
            fail();
        }
    }

    @Test
    public void itShouldRedirectWhenUnauthenticated() {
        when(mockPermissionEnforcer.isSystemAdmin()).thenReturn(false);
        when(mockPermissionEnforcer.isAuthenticated()).thenReturn(false);

        when(mockLoginUriProvider.getLoginUri(DUMMY_LOGIN_URI)).thenReturn(DUMMY_LOGIN_URI);

        when(mockReq.getRequestURL()).thenReturn(new StringBuffer(DUMMY_LOGIN_URL_STRING));

        try {
            sut.doGet(mockReq, mockResp);
            verify(mockResp).sendRedirect(DUMMY_LOGIN_URL_STRING);
        } catch(IOException ioe) {
            fail();
        }
    }

}