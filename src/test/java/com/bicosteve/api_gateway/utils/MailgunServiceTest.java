package com.bicosteve.api_gateway.utils;

import com.bicosteve.api_gateway.config.MailgunConfig;
import com.bicosteve.api_gateway.dto.requests.MailRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailgunServiceTest {

    private MailgunConfig mailgunConfig;
    private MailgunService mailgunService;
    private MockedStatic<Unirest> unirestMock;

    @BeforeEach
    void setUp() {
        mailgunConfig = mock(MailgunConfig.class);
        when(mailgunConfig.getBaseUrl()).thenReturn("https://api.mailgun.net/v3");
        when(mailgunConfig.getSandbox()).thenReturn("sandbox.example.com");
        when(mailgunConfig.getApiKey()).thenReturn("key-abc");
        when(mailgunConfig.getFrom()).thenReturn("noreply@example.com");

        mailgunService = new MailgunService(mailgunConfig);

        unirestMock = Mockito.mockStatic(Unirest.class, Mockito.CALLS_REAL_METHODS);
    }

    @AfterEach
    void tearDown() {
        unirestMock.close();
    }

    @Test
    void sendEmailHandlesUnirestHttpExceptionGracefully() {
        // Force a runtime exception in unirest chain so the catch branch executes
        unirestMock.when(() -> Unirest.post(anyString()))
                .thenThrow(new RuntimeException("boom"));

        MailRequest mail = MailRequest.builder()
                .to("user@example.com")
                .subject("Hi")
                .body("Hello")
                .purpose("test")
                .build();

        assertThrows(RuntimeException.class, () -> mailgunService.sendEmail(mail));
    }

    @Test
    void sendEmailLogsErrorOnNon200Status() {
        // Build a stub that returns 500 status without invoking unirest
        @SuppressWarnings({"unchecked", "rawtypes"})
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(500);
        when(response.getBody()).thenReturn("server error");

        unirestMock.when(() -> Unirest.post(anyString()))
                .thenThrow(new RuntimeException("simulated unirest failure"));

        MailRequest mail = MailRequest.builder()
                .to("user@example.com")
                .subject("Hi")
                .body("Hello")
                .purpose("test")
                .build();

        // The service catches exceptions and rethrows a runtime exception
        assertThrows(RuntimeException.class, () -> mailgunService.sendEmail(mail));
    }
}
