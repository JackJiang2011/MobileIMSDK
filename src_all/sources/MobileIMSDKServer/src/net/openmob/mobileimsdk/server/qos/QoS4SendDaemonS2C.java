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
package net.openmob.mobileimsdk.server.qos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import net.openmob.mobileimsdk.server.ServerLauncher;
import net.openmob.mobileimsdk.server.event.MessageQoSEventListenerS2C;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QoS4SendDaemonS2C
{
  private static Logger logger = LoggerFactory.getLogger(QoS4SendDaemonS2C.class);

  public static boolean DEBUG = false;

  private ServerLauncher serverLauncher = null;

  private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap();

  private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap();
  public static final int CHECH_INTERVAL = 5000;
  public static final int MESSAGES_JUST$NOW_TIME = 2000;
  public static final int QOS_TRY_COUNT = 1;
  private boolean running = false;

  private boolean _excuting = false;

  private Timer timer = null;

  private static QoS4SendDaemonS2C instance = null;

  public static QoS4SendDaemonS2C getInstance()
  {
    if (instance == null)
      instance = new QoS4SendDaemonS2C();
    return instance;
  }

  private QoS4SendDaemonS2C()
  {
    init();
  }

  private void init()
  {
    this.timer = new Timer(5000, new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        QoS4SendDaemonS2C.this.run();
      }
    });
  }

  public void run()
  {
    if (!this._excuting)
    {
      ArrayList lostMessages = new ArrayList();
      this._excuting = true;
      try
      {
        if (DEBUG) {
          logger.debug("【IMCORE】【QoS发送方】=========== 消息发送质量保证线程运行中, 当前需要处理的列表长度为" + this.sentMessages.size() + "...");
        }

        for (String key : this.sentMessages.keySet())
        {
          Protocal p = (Protocal)this.sentMessages.get(key);
          if ((p != null) && (p.isQoS()))
          {
            if (p.getRetryCount() >= 1)
            {
              if (DEBUG) {
                logger.debug("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
                  "的消息包重传次数已达" + p.getRetryCount() + "(最多" + 1 + "次)上限，将判定为丢包！");
              }

              lostMessages.add((Protocal)p.clone());

              remove(p.getFp());
            }
            else
            {
              long delta = System.currentTimeMillis() - ((Long)this.sendMessagesTimestamp.get(key)).longValue();

              if (delta <= 2000L)
              {
                if (DEBUG) {
                  logger.warn("【IMCORE】【QoS发送方】指纹为" + key + "的包距\"刚刚\"发出才" + delta + 
                    "ms(<=" + 2000 + "ms将被认定是\"刚刚\"), 本次不需要重传哦.");
                }
              }
              else
              {
                boolean sendOK = ServerLauncher.sendData(p);

                p.increaseRetryCount();

                if (sendOK)
                {
                  if (DEBUG) {
                    logger.debug("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
                      "的消息包已成功进行重传，此次之后重传次数已达" + 
                      p.getRetryCount() + "(最多" + 1 + "次).");
                  }

                }
                else if (DEBUG) {
                  logger.warn("【IMCORE】【QoS发送方】指纹为" + p.getFp() + 
                    "的消息包重传失败，它的重传次数之前已累计为" + 
                    p.getRetryCount() + "(最多" + 1 + "次).");
                }
              }

            }

          }
          else
          {
            remove(key);
          }
        }
      }
      catch (Exception eee)
      {
        if (DEBUG) {
          logger.warn("【IMCORE】【QoS发送方】消息发送质量保证线程运行时发生异常," + eee.getMessage(), eee);
        }
      }
      if ((lostMessages != null) && (lostMessages.size() > 0))
      {
        notifyMessageLost(lostMessages);
      }

      this._excuting = false;
    }
  }

  protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
  {
    if ((this.serverLauncher != null) && (this.serverLauncher.getServerMessageQoSEventListener() != null))
      this.serverLauncher.getServerMessageQoSEventListener().messagesLost(lostMessages);
  }

  public QoS4SendDaemonS2C startup(boolean immediately)
  {
    stop();

    if (immediately)
      this.timer.setInitialDelay(0);
    else
      this.timer.setInitialDelay(5000);
    this.timer.start();

    this.running = true;

    return this;
  }

  public void stop()
  {
    if (this.timer != null) {
      this.timer.stop();
    }

    this.running = false;
  }

  public boolean isRunning()
  {
    return this.running;
  }

  public boolean exist(String fingerPrint)
  {
    return this.sentMessages.get(fingerPrint) != null;
  }

  public void put(Protocal p)
  {
    if (p == null)
    {
      if (DEBUG)
        logger.warn("Invalid arg p==null.");
      return;
    }
    if (p.getFp() == null)
    {
      if (DEBUG)
        logger.warn("Invalid arg p.getFp() == null.");
      return;
    }

    if (!p.isQoS())
    {
      if (DEBUG)
        logger.warn("This protocal is not QoS pkg, ignore it!");
      return;
    }

    if (this.sentMessages.get(p.getFp()) != null)
    {
      if (DEBUG) {
        logger.warn("【IMCORE】【QoS发送方】指纹为" + p.getFp() + "的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");
      }
    }

    this.sentMessages.put(p.getFp(), p);

    this.sendMessagesTimestamp.put(p.getFp(), Long.valueOf(System.currentTimeMillis()));
  }

  public void remove(String fingerPrint)
  {
    new SwingWorker(fingerPrint)
    {
      protected Protocal doInBackground()
      {
        QoS4SendDaemonS2C.this.sendMessagesTimestamp.remove(this.val$fingerPrint);
        return (Protocal)QoS4SendDaemonS2C.this.sentMessages.remove(this.val$fingerPrint);
      }

      protected void done()
      {
        Protocal result = null;
        try
        {
          result = (Protocal)get();
        }
        catch (Exception e)
        {
          if (QoS4SendDaemonS2C.DEBUG) {
            QoS4SendDaemonS2C.logger.warn(e.getMessage(), e);
          }
        }
        if (QoS4SendDaemonS2C.DEBUG)
          QoS4SendDaemonS2C.logger.warn("【IMCORE】【QoS发送方】指纹为" + this.val$fingerPrint + "的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数=" + (
            result != null ? Integer.valueOf(result.getRetryCount()) : "none呵呵.")); 
      }
    }
    .execute();
  }

  public int size()
  {
    return this.sentMessages.size();
  }

  public void setServerLauncher(ServerLauncher serverLauncher)
  {
    this.serverLauncher = serverLauncher;
  }
}