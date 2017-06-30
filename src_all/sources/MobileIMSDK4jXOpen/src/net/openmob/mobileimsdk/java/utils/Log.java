/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project. 
 * All rights reserved.
 * 
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * Log.java at 2017-5-1 22:14:56, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.utils;

import java.awt.Color;
import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Swing实现的日志显示工具，方便调试代码。
 * 
 * @author Jack Jiang, 2008
 */
public class Log
{
	/** 日志输出级别 */
	public final static int INFO=1,PROMPT=2,DEBUG=3,WARN=4,ERROR=5,FETAL=6;
	public final static int DEST_TEXT_COM = 7,DEST_RICH_TEXT_COM = 8,DEST_CONSOLE = 9;
	
	private int logDestType=-1;
	/** 允许的日志输出级别(该级别及其以下级别的日志都不会被输出) */
	private int logLevel=-1;
	
	private JTextPane richTextDest;
	private JTextArea textDest;
	
	private static Log instance = null;
	
	public static Log getInstance()
	{
		if(instance == null)
			instance = new Log(null, -1);
		return instance;
	}

	public Log(Object dest,int logLevel)
	{
		this.logLevel=logLevel;
		this.setLogDest(dest);
	}
	
	public Log setLogDest(Object dest)
	{
		if(dest==null)
			this.logDestType=DEST_CONSOLE;
		else
		{
			if(dest instanceof JTextPane)
			{
				this.logDestType=DEST_RICH_TEXT_COM;
				richTextDest=(JTextPane)dest;
			}
			else if(dest instanceof JTextArea)
			{
				this.logDestType=DEST_TEXT_COM;
				textDest=(JTextArea)dest;
			}
			else
				System.out.println("不支持的日志输出目的地!");
		}
		
		return this;
	}

	public int getLogLevel()
	{
		return logLevel;
	}
	public Log setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
		return this;
	}

	/**
	 * INFO
	 * 
	 * @param msg
	 */
	public static void i(String tag, String msg)
	{
		i(tag, msg, null);
	}
	public static void i(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.INFO, ex);
	}
	
	/**
	 * PROMPT
	 * 
	 * @param msg
	 */
	public static void p(String tag, String msg)
	{
		p(tag, msg, null);
	}
	public static void p(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.PROMPT, ex);
	}
	
	/**
	 * DEBUG
	 * 
	 * @param msg
	 */
	public static void d(String tag, String msg)
	{
		d(tag, msg, null);
	}
	public static void d(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.DEBUG, ex);
	}
	
	/**
	 * WARN
	 * 
	 * @param msg
	 */
	public static void w(String tag, String msg)
	{
		w(tag, msg, null);
	}
	public static void w(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.WARN, ex);
	}
	
	/**
	 * ERROR
	 * 
	 * @param msg
	 */
	public static void e(String tag, String msg)
	{
		e(tag, msg, null);
	}
	public static void e(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.ERROR, ex);
	}
	
	/**
	 * FETAL
	 * 
	 * @param msg
	 */
	public static void f(String tag, String msg)
	{
		f(tag, msg, null);
	}
	public static void f(String tag, String msg, Exception ex)
	{
		Log.getInstance().log(msg, Log.FETAL, ex);
	}
	
	/**
	 * 日志输出.
	 * 
	 * @param msg
	 * @param level
	 */
	public void log(String msg,int level)
	{
		this.log(msg, level, null);
	}
	
	/**
	 * 日志输出.
	 * 
	 * @param msg
	 * @param level
	 */
	public void log(String msg,int level, Exception ex)
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
	
	/**
	 * 添加字符串到目标JTextPane中,并设定字符串颜色(注意目前仅仅是实现设置颜色).
	 * @param c 文本颜色
	 * @param s 文本内容
	 * @param p 目标面板
	 */
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
