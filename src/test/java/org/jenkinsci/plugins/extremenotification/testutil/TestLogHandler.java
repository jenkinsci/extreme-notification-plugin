package org.jenkinsci.plugins.extremenotification.testutil;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Created by daniel.burgmann on 20.02.16 19:32
 */
public class TestLogHandler extends StreamHandler {
    private static final String UTF_8 = "UTF-8";
    private ByteArrayOutputStream out;

    public TestLogHandler() {
        super();
        try {
            setEncoding(UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding error while initializing test getLog. " + UTF_8 + " not supported?", e);
        }
        setLevel(Level.ALL);
        out = new ByteArrayOutputStream();
        setOutputStream(out);
    }

    public String getLog() {
        flush();
        try {
            return new String(out.toByteArray(), getEncoding());
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding error while getting test getLog. Check logging configuration.", e);
        }
    }

    public void reset() {
        out = new ByteArrayOutputStream();
        setOutputStream(out);
    }

}
