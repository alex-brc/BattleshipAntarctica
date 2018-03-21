package org.alien8.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * This class implements the log manager. It is implemented as a singleton class, so it is used by
 * requesting its instance at runtime.
 * <p>
 * Its purpose is to log normal and abnormal behaviour of the game application for debugging and
 * analysis purposes.
 * <p>
 * The content of the logs will be under the control of the developers, but a certain structure is
 * imposed to maintain a level of consistency throughout the log file for overall readability.
 * <p>
 * Logs are unique per application run. The log is created on application start and closed on
 * application end.
 *
 * @version 1.0
 * 
 */
public class LogManager {
  // Scopes for logging messages
  public enum Scope {
    INFO, DEBUG, WARNING, ERROR, CRITICAL
  }

  private static String filePath = "logs/";

  private static LogManager instance = null;

  private LogManager() {

    // Retrieve file names
    File folder = new File(filePath.replace("/", ""));

    folder.mkdir();

    String[] fileNames = folder.list();

    LocalDateTime dateTime = LocalDateTime.now();

    String numString = "000";

    // Compute next log ID
    int max = -1;
    int temp = 0;
    if (fileNames.length != 0)
      for (String name : fileNames) {
        temp = Integer.parseInt(name.substring(0, 3));
        if (temp > max)
          max = temp;
      }

    max++;

    // Pad left with 0's to length 3
    if (max >= 100)
      numString = "" + max;
    else if (max >= 10)
      numString = "0" + max;
    else
      numString = "00" + max;

    // Make the file
    filePath += numString + "_" + dateTime.toString().substring(0, 10) + ".txt";

    try {
      File file = new File(filePath);
      file.createNewFile();

      FileWriter writer = new FileWriter(filePath, true);
      writer.write("******************************************************\r\n"
          + "*                   LOG: " + dateTime.toString().substring(0, 10)
          + "                  *\r\n" + "******************************************************\r\n"
          + dateTime.toString().substring(11)
          + " Log[INFO]: Log manager successfuly initialised.\r\n");

      writer.flush();
      writer.close();

    } catch (IOException e) {
      System.out.println("CRITICAL: Log Manager couldn't initialise. Exception: " + e.toString());
      System.exit(-1);
    }

  }

  /**
   * A standard getInstance() in accordance with the singleton pattern
   * 
   * @return an instance of the active LogManager
   */
  public static LogManager getInstance() {
    // Lazy instantiation
    if (instance == null) {
      instance = new LogManager();
    }
    return instance;
  }

  /**
   * Writes a regular line to the current log file.
   * <p>
   * The format of a line in the log is:
   * <p>
   * {@code <HH:MM:ss.mmm> <Source>[<Scope>]: <Content>}
   * <p>
   * Example of a line in the log file.
   * <p>
   * {@code 22:48:11.010 Audio[ERROR]: sprite3.wav failed to load. File missing? }
   * 
   * @param source The manager that requests the log
   * @param scope The scope of the log
   * @param content The content of the log
   */
  public void log(String source, Scope scope, String content) {
    try {
      FileWriter writer = new FileWriter(LogManager.filePath, true);
      String line = "";

      // Put timestamp
      LocalDateTime dateTime = LocalDateTime.now();
      line += dateTime.toString().substring(11);
      line += " ";

      // Put source
      line += source;

      // Put scope
      line += "[";
      line += scope.toString();

      // Put message
      line += "]: " + content + "\r\n";

      // Log it
      writer.write(line);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.out.println("ERROR: Logging the message failed. Exception: " + e.toString());
    }
  }

  /**
   * Writes a banner style body to the log file.
   * 
   * @param content The content of the banner. Will be automatically turned to upper case.
   */
  public void logBanner(String content) {
    try {
      // Length of the banner
      int banLen = 54;

      FileWriter writer = new FileWriter(LogManager.filePath, true);

      content = content.toUpperCase();

      // Number of spaces pre and post content
      int pre = (banLen - 2 - content.length()) / 2;
      int post = pre;
      // Adjust for odd length
      if (content.length() % 2 != 0)
        post++;

      String wr = "";
      for (int i = 0; i < banLen; i++)
        wr += "*"; // ************************
      wr += "\r\n*"; // * TEXT *
      for (int i = 0; i < pre; i++) // ************************
        wr += " ";
      wr += content;
      for (int i = 0; i < post; i++)
        wr += " ";
      wr += "*\r\n";
      for (int i = 0; i < banLen; i++)
        wr += "*";
      wr += "\r\n";

      writer.write(wr);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.out.println("ERROR: Logging the message failed. Exception: " + e.toString());
    }
  }

  /**
   * Writes a custom line to the log file.
   * 
   * @param content Line to be written
   */
  public void logMeta(String content) {
    try {
      FileWriter writer = new FileWriter(LogManager.filePath, true);

      content += "\r\n";

      writer.write(content);

      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.out.println("ERROR: Logging the message failed. Exception: " + e.toString());
    }
  }
  
  /** 
   * @return the filepath of the current log file as <code> String </code>
   */
  public static String getFilePath() {
	  return filePath;
  }
}
