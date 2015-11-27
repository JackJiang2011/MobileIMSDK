/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Archived at 2015-11-27 14:02:01, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.utils;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Date;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Log
{
  public static final int INFO = 1;
  public static final int PROMPT = 2;
  public static final int DEBUG = 3;
  public static final int WARN = 4;
  public static final int ERROR = 5;
  public static final int FETAL = 6;
  public static final int DEST_TEXT_COM = 7;
  public static final int DEST_RICH_TEXT_COM = 8;
  public static final int DEST_CONSOLE = 9;
  private int logDestType = -1;

  private int logLevel = -1;
  private JTextPane richTextDest;
  private JTextArea textDest;
  private static Log instance = null;

  public static Log getInstance()
  {
    if (instance == null)
      instance = new Log(null, -1);
    return instance;
  }

  public Log(Object dest, int logLevel)
  {
    this.logLevel = logLevel;
    setLogDest(dest);
  }

  public Log setLogDest(Object dest)
  {
    if (dest == null) {
      this.logDestType = 9;
    }
    else if ((dest instanceof JTextPane))
    {
      this.logDestType = 8;
      this.richTextDest = ((JTextPane)dest);
    }
    else if ((dest instanceof JTextArea))
    {
      this.logDestType = 7;
      this.textDest = ((JTextArea)dest);
    }
    else {
      System.out.println("不支持的日志输出目的地!");
    }

    return this;
  }

  public int getLogLevel()
  {
    return this.logLevel;
  }

  public Log setLogLevel(int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public static void i(String tag, String msg)
  {
    i(tag, msg, null);
  }

  public static void i(String tag, String msg, Exception ex) {
    getInstance().log(msg, 1, ex);
  }

  public static void p(String tag, String msg)
  {
    p(tag, msg, null);
  }

  public static void p(String tag, String msg, Exception ex) {
    getInstance().log(msg, 2, ex);
  }

  public static void d(String tag, String msg)
  {
    d(tag, msg, null);
  }

  public static void d(String tag, String msg, Exception ex) {
    getInstance().log(msg, 3, ex);
  }

  public static void w(String tag, String msg)
  {
    w(tag, msg, null);
  }

  public static void w(String tag, String msg, Exception ex) {
    getInstance().log(msg, 4, ex);
  }

  public static void e(String tag, String msg)
  {
    e(tag, msg, null);
  }

  public static void e(String tag, String msg, Exception ex) {
    getInstance().log(msg, 5, ex);
  }

  public static void f(String tag, String msg)
  {
    f(tag, msg, null);
  }

  public static void f(String tag, String msg, Exception ex) {
    getInstance().log(msg, 6, ex);
  }

  public void log(String msg, int level)
  {
    log(msg, level, null);
  }

  public void log(String msg, int level, Exception ex)
  {
    String lv = "";
    Color fc = Color.black;
    switch (level)
    {
    case 1:
      fc = new Color(153, 204, 0);
      lv = "INFO";
      break;
    case 2:
      fc = new Color(0, 255, 0);
      lv = "PROMPT";
      break;
    case 3:
      fc = new Color(255, 204, 153);
      lv = "DEBUG";
      break;
    case 4:
      fc = Color.pink;
      lv = "WARN";
      break;
    case 5:
      fc = Color.red;
      lv = "ERROR";
      break;
    case 6:
      fc = Color.red;
      lv = "FETAL";
    }

    if (level > this.logLevel)
    {
      String txt = " " + lv + " - " + msg + (ex == null ? "" : new StringBuilder("(").append(ex.getMessage()).append(")").toString()) + " [" + new Date().toLocaleString() + "]\r\n";
      if (this.logDestType == 8)
      {
        try
        {
          append(fc, txt, this.richTextDest);
          this.richTextDest.setCaretPosition(this.richTextDest.getDocument().getLength());
        }
        catch (Exception localException)
        {
        }

      }
      else if (this.logDestType == 7)
        this.textDest.append(txt);
      else if (this.logDestType == 9) {
        System.out.print(txt);
      }

      if (ex != null)
        ex.printStackTrace();
    }
  }

  public static void append(Color c, String s, JTextPane p)
  {
    Runnable runnable = new Runnable(c, p, s)
    {
      public void run() {
        try {
          MutableAttributeSet sa = new SimpleAttributeSet();
          StyleConstants.setForeground(sa, Log.this);

          int len = this.val$p.getDocument().getLength();
          this.val$p.getDocument().insertString(len, this.val$s, sa);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    };
    SwingUtilities.invokeLater(runnable);
  }
}