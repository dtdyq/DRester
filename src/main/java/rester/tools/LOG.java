package rester.tools;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class LOG {
    private static final List<Logger> LOGGERS = new ArrayList<>();
    private static final AtomicInteger seq = new AtomicInteger(0);
    static {
        IntStream.range(0, 100).forEach(value -> LOGGERS.add(initLog(value)));
    }

    private static Logger initLog(int value) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        try {
            fileAppender.setFile(new File(".").getCanonicalPath() + "/rester.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileAppender.setEncoder(getPle(lc));
        fileAppender.setContext(lc);
        fileAppender.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(getPle(lc));
        consoleAppender.setContext(lc);
        consoleAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger("default" + value);
        logger.addAppender(fileAppender);
        logger.addAppender(consoleAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);

        return logger;
    }

    public static Logger getLog(String name) {
        Logger logger = LOGGERS.get(seq.getAndIncrement());
        chgName(logger, name);
        return logger;
    }

    private static void chgName(Logger logger, String name) {
        try {
            Field f = logger.getClass().getDeclaredField("name");
            f.setAccessible(true);
            f.set(logger, name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setLevel(Level level) {
        LOGGERS.forEach(logger -> logger.setLevel(level));
    }

    private static PatternLayoutEncoder getPle(LoggerContext lc) {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d{'yyyy-MM-dd HH:mm:ss'} | %level | %thread{40} | %msg%n");
        ple.setContext(lc);
        ple.start();
        return ple;
    }
}
