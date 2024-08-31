package com.wenqi.tech.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liangwenqi
 * @date 2024/4/16
 */
public class TestLogger extends Logger {

    private static final long serialVersionUID = 1L;


    private final List<String> list = new ArrayList<>();

    protected TestLogger(LoggerContext context, String name, MessageFactory messageFactory) {
        super(context, name, messageFactory);
    }

    public List<String> getEntries() {
        return list;
    }

    @Override
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message msg,
                           final Throwable throwable) {
        log(level, marker, fqcn, (StackTraceElement) null, msg, throwable);
    }

    @Override
    protected void log(final Level level, final Marker marker, final String fqcn, final StackTraceElement location,
                       final Message message, final Throwable throwable) {
        final StringBuilder sb = new StringBuilder();
        if (marker != null) {
            sb.append(marker);
        }
        sb.append(' ');
        sb.append(level.toString());
        sb.append(' ');
        if (location != null) {
            sb.append(location.toString());
            sb.append(' ');
        }
        sb.append(message.getFormattedMessage());
        final Map<String, String> mdc = ThreadContext.getImmutableContext();
        if (mdc.size() > 0) {
            sb.append(' ');
            sb.append(mdc.toString());
            sb.append(' ');
        }
        final Object[] params = message.getParameters();
        Throwable t;
        if (throwable == null && params != null && params.length > 0 && params[params.length - 1] instanceof Throwable) {
            t = (Throwable) params[params.length - 1];
        } else {
            t = throwable;
        }
        if (t != null) {
            sb.append(' ');
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(baos));
            sb.append(baos.toString());
        }
        list.add(sb.toString());
        System.out.println(sb.toString());
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String msg) {
        return true;
    }


    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String msg, final Throwable t) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String msg, final Object... p1) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6,
                             final Object p7) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6,
                             final Object p7, final Object p8) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
                             final Object p1, final Object p2, final Object p3,
                             final Object p4, final Object p5, final Object p6,
                             final Object p7, final Object p8, final Object p9) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence msg, final Throwable t) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Object msg, final Throwable t) {
        return true;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Message msg, final Throwable t) {
        return true;
    }

    @Override
    public Level getLevel() {
        return Level.ALL;
    }
}

