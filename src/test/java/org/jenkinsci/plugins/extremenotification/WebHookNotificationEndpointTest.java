package org.jenkinsci.plugins.extremenotification;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jenkinsci.plugins.extremenotification.testutil.Sample;
import org.jenkinsci.plugins.extremenotification.testutil.TestLogHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by daniel.burgmann on 20.02.16 11:53
 */
public class WebHookNotificationEndpointTest {

    private TestLogHandler logHandler;
    private WebHookNotificationEndpoint notificationEndpoint;

    @Rule
    public WireMockRule webHook = new WireMockRule(
            WireMockConfiguration.wireMockConfig().port(0)
    );

    @Before
    public void prepareWireMock() throws Exception {
        webHook.stubFor(
                get(urlMatching("/event.*"))
                        .willReturn(aResponse()
                                .withStatus(200))
        );
        webHook.stubFor(
                get(urlMatching("/delayed.*"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(2000)) // 2 seconds delay
        );
    }

    @Before
    public void setUp() {
        notificationEndpoint = new WebHookNotificationEndpoint();
        notificationEndpoint.setTimeout(1); //notification endpoint will timeout after 1 sec

        logHandler = new TestLogHandler();
        //after initializing the notification endpoint class it is save to get the logger
        Logger endpointLogger = LogManager.getLogManager().getLogger(WebHookNotificationEndpoint.class.getName());
        endpointLogger.setLevel(Level.ALL);
        endpointLogger.addHandler(logHandler);
    }

    @After
    public void tearDown() {
        notificationEndpoint = null;
        logHandler = null;
    }

    @Test
    public void expectedNotify() throws Exception {
        notificationEndpoint.setUrl(baseUrl() + "/event?name=${event.name}");
        notificationEndpoint.notify(Sample.event());

        verify(1, getRequestedFor(urlEqualTo("/event?name=testEvent")));

        String log = logHandler.getLog();
        assertTrue(log.contains("200"));
        assertTrue(log.contains("/event?name=testEvent"));
    }

    @Test
    public void notificationFailureIsLogged() {
        notificationEndpoint.setUrl(baseUrl() + "/delayed");
        notificationEndpoint.notify(Sample.event());

        String log = logHandler.getLog();
        assertTrue(log.contains("communication failure"));
    }


    private String baseUrl() {
        return "http://127.0.0.1:" + webHook.port();
    }
}
