package rester.boot;

import rester.tools.LOG;

import org.slf4j.Logger;

import java.io.IOException;

public class Rester {
    private static final Logger LOGGER = LOG.getLog("Rester");

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.warn("please specify config json or yaml");
        } else {
            String path = args[0];
            try {
                new Configure().config(path).start();
            } catch (IOException | IllegalArgumentException e) {
                LOGGER.error("config failed.{}", e.getMessage());
            }
        }
    }
}
