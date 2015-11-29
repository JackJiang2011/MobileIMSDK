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
		String lv="";
		Color fc=Color.black;
		switch(level)
		{
		case INFO:
			fc=new Color(153,204,0);
			lv="INFO";
			break;
		case PROMPT:
			fc=new Color(0, 255, 0);
			lv="PROMPT";
			break;
		case DEBUG:
			fc=new Color(255,204,153);
			lv="DEBUG";
			break;
		case WARN:
			fc=Color.pink;
			lv="WARN";
			break;
		case ERROR:
			fc=Color.red;
			lv="ERROR";
			break;
		case FETAL:
			fc=Color.red;
			lv="FETAL";
			break;
		}

		if(level>logLevel)
		{
			String txt=" "+lv+" - "+msg+(ex == null?"":"("+ex.getMessage()+")")+" ["+(new Date().toLocaleString())+"]\r\n";
			if(logDestType==DEST_RICH_TEXT_COM)
			{
				try
				{
					Log.append(fc, txt, richTextDest);
					richTextDest.setCaretPosition(richTextDest.getDocument().getLength());
				}
				catch(Exception e)
				{
					//					e.printStackTrace();
				}
			}
			else if(logDestType==DEST_TEXT_COM)
				textDest.append(txt);
			else if(logDestType==DEST_CONSOLE)
				System.out.print(txt);
			else
			{
				//未知日志目的地类型
			}

			if(ex != null)
				ex.printStackTrace();
		}
	}

	public static void append(final Color c, final String s, final JTextPane p)
	{
		// 以下代码因会从独立的Thread中调用，所以需要借用SwingUtilities.invokeLater()，
		// 否则会报错：”Exception in thread "Thread-22" java.lang.Error: Interrupted attempt to aquire write lock“
		Runnable  runnable = new Runnable() {
            public void run(){
            	try
        		{
        			MutableAttributeSet sa = new SimpleAttributeSet();
        			StyleConstants.setForeground(sa, c);

        			int len = p.getDocument().getLength();
        			p.getDocument().insertString(len, s, sa);
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