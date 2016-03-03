package org.jenkinsci.plugins.extremenotification.testutil;

import org.jenkinsci.plugins.extremenotification.MyPlugin;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by daniel.burgmann on 20.02.16 12:33
 */
public final class Sample {
    private Sample() {
    }

    public static MyPlugin.Event event() {
        return new MyPlugin.Event("testEvent", "argFoo", "foo", "argBar", "bar");
    }

    public static Map<String, Object> extraArgs() {
        Map<String, Object> extraArgs = new Hashtable<String, Object>();
        extraArgs.put("what", "a simple string");
        extraArgs.put("howMuch", 42);
        return extraArgs;
    }
}
