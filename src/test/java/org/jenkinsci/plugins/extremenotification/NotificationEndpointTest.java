package org.jenkinsci.plugins.extremenotification;

import junit.framework.TestCase;
import org.jenkinsci.plugins.extremenotification.testutil.Sample;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by daniel.burgmann on 19.02.16 20:17
 */

public class NotificationEndpointTest extends TestCase {

    NotificationEndpoint notificationEndpoint;
    MyPlugin.Event event;
    Map<String, Object> extraArgs;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        notificationEndpoint = new NotificationEndpoint() {
            @Override
            public void notify(MyPlugin.Event event) {
            }

            @Override
            public void notify(MyPlugin.Event event, EndpointEvent endpointEvent) {
            }
        };

        event = Sample.event();
        extraArgs = Sample.extraArgs();
    }

    @Override
    public void tearDown() throws Exception {
        event = null;
        notificationEndpoint = null;

        super.tearDown();
    }

    public void testInterpolate() {

        String interpolated;

        interpolated = notificationEndpoint.interpolate("value", event);
        assertTrue(interpolated.length() > 0);

        interpolated = notificationEndpoint.interpolate("event ${event.name} (argFoo: ${event.args.argFoo})", event);
        assertEquals("event testEvent (argFoo: foo)", interpolated);

        interpolated = notificationEndpoint.interpolate("event ${event.name} (argBar: ${event.args.argBar}) is now ${what}", event, extraArgs);
        assertEquals("event testEvent (argBar: bar) is now a simple string", interpolated);
    }

}