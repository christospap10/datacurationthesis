package com.datacurationthesis.datacurationthesis.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerController {

  private static final Logger logger = LogManager.getLogger(
    LoggerController.class
  );

  public static void info(String message) {
    logger.info(colorize(message, AnsiColor.GREEN));
  }

  public static void debug(String message) {
    logger.debug(colorize(message, AnsiColor.BLUE));
  }

  public static void warn(String message) {
    logger.warn(colorize(message, AnsiColor.YELLOW));
  }

  public static void error(String message) {
    logger.error(colorize(message, AnsiColor.RED));
  }

  public static void trace(String message) {
    logger.trace(colorize(message, AnsiColor.CYAN));
  }

  public static void logException(String message, Throwable throwable) {
    logger.error(colorize(message, AnsiColor.RED), throwable);
  }

  public static void formattedInfo(String format, Object... args) {
    logger.info(colorize(String.format(format, args), AnsiColor.GREEN));
  }

  public static void formattedDebug(String format, Object... args) {
    logger.debug(colorize(String.format(format, args), AnsiColor.BLUE));
  }

  public static void formattedWarn(String format, Object... args) {
    logger.warn(colorize(String.format(format, args), AnsiColor.YELLOW));
  }

  public static void formattedError(String format, Object... args) {
    logger.error(colorize(String.format(format, args), AnsiColor.RED));
  }

  public static void formattedTrace(String format, Object... args) {
    logger.trace(colorize(String.format(format, args), AnsiColor.CYAN));
  }

  public static String colorize(String message, String color) {
    return color + message + AnsiColor.RESET;
  }

  public class AnsiColor {

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
  }
}
