package support;

import java.io.*;
import networks.*;
import structures.*;


// Create an output writer that will write to that file.
// FileWriter handles international characters encoding conversions.

public class Logger {

  private File logfile;
  private BufferedWriter bfwlog;

  public Logger(String s) {
    init();
    this.append(s);
  }

  public Logger() {
    init();
  }


  public void append(String s) {
    try {
      bfwlog.write(s);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  

  public void close() {
    try {
      bfwlog.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }  


  private void init() {
    try {
      logfile = new File("run.log");
      bfwlog = new BufferedWriter(new FileWriter(logfile, true));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }  

}
