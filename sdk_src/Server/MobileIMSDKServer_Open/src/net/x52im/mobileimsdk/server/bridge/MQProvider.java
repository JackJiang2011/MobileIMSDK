/*
 * Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK v6.x Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“【即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * MQProvider.java at 2021-6-29 10:15:35, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.server.bridge;

import java.io.IOException;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class MQProvider
{
	private static Logger logger = LoggerFactory.getLogger(MQProvider.class);  
	
	public final static String DEFAULT_ENCODE_CHARSET = "UTF-8";
	public final static String DEFAULT_DECODE_CHARSET = "UTF-8";

	protected ConnectionFactory _factory = null;
	protected Connection _connection = null;
	protected Channel _pubChannel = null;
	
	protected final Timer timerForStartAgain = new Timer();
	protected boolean startRunning = false;
	
	protected final Timer timerForRetryWorker = new Timer();
	protected boolean retryWorkerRunning = false;
	
	protected ConcurrentLinkedQueue<String[]> publishTrayAgainCache = new ConcurrentLinkedQueue<String[]>();
	protected boolean publishTrayAgainEnable = false;
	
	protected Observer consumerObserver = null;
	
	protected String encodeCharset = null;
	protected String decodeCharset = null;
	protected String mqURI = null;
	protected String publishToQueue = null;
	protected String consumFromQueue = null;
	
	/** TAG for log */
	protected String TAG = null;
	
	public MQProvider(String mqURI, String publishToQueue, String consumFromQueue, String TAG, boolean publishTrayAgainEnable)
	{
		this(mqURI, publishToQueue, consumFromQueue, null, null, TAG, publishTrayAgainEnable);
	}
	
	public MQProvider(String mqURI, String publishToQueue, String consumFromQueue
			, String encodeCharset, String decodeCharset, String TAG
			, boolean publishTrayAgainEnable)
	{
		this.mqURI = mqURI;
		this.publishToQueue = publishToQueue;
		this.consumFromQueue = consumFromQueue;
		this.encodeCharset = encodeCharset;
		this.decodeCharset = decodeCharset;
		this.TAG = TAG;
		
		if(this.mqURI == null)
			throw new IllegalArgumentException("["+TAG+"]无效的参数mqURI ！");
		
		if(this.publishToQueue == null && this.consumFromQueue == null)
			throw new IllegalArgumentException("["+TAG+"]无效的参数，publishToQueue和" +
					"consumFromQueue至少应设置其一！");
		
		if(this.encodeCharset == null || this.encodeCharset.trim().length() == 0)
			this.encodeCharset = DEFAULT_ENCODE_CHARSET;
		if(this.decodeCharset == null || this.decodeCharset.trim().length() == 0)
			this.decodeCharset = DEFAULT_DECODE_CHARSET;
		
		init();
	}
	
	protected boolean init()
	{
		String uri = this.mqURI;
		_factory = new ConnectionFactory();

		// 设置连接 uri
		try
		{
			_factory.setUri(uri);
		}
		catch (Exception e)
		{
			logger.error("["+TAG+"] - 【严重】factory.setUri()时出错，Uri格式不对哦，uri="+uri, e);
			return false;
		}

		_factory.setAutomaticRecoveryEnabled(true);
		_factory.setTopologyRecoveryEnabled(false);
		_factory.setNetworkRecoveryInterval(5000);

		_factory.setRequestedHeartbeat(30);
		_factory.setConnectionTimeout(30 * 1000);
		
		return true;
	}
	
	protected Connection tryGetConnection()
	{
		if(_connection == null)
		{
			try
			{
				_connection = _factory.newConnection();
				_connection.addShutdownListener(new ShutdownListener() {
					public void shutdownCompleted(ShutdownSignalException cause)
					{
						logger.warn("["+TAG+"] - 连接已经关闭了。。。。【NO】");
					}
				});

				((Recoverable)_connection).addRecoveryListener(new RecoveryListener(){
					@Override
					public void handleRecovery(Recoverable arg0)
					{
						logger.info("["+TAG+"] - 连接已成功自动恢复了！【OK】");
						
						start();
					}
				});
			}
			catch (Exception e)
			{
				logger.error("["+TAG+"] - 【NO】getConnection()时出错了，原因是："+e.getMessage(), e);
				_connection = null;
				return null;
			}
		}
		
		return _connection;
	}

	public void start()
	{
		if(startRunning)
			return;
		
		try
		{
			if(_factory != null)
			{
				Connection conn = tryGetConnection();
				if(conn != null)
				{
					whenConnected(conn);
				}
				else
				{
					logger.error("["+TAG+"-↑] - [start()中]【严重】connction还没有准备好" +
							"，conn.createChannel()失败，start()没有继续！(原因：connction==null)【5秒后重新尝试start】");

					timerForStartAgain.schedule(new TimerTask() {
						public void run() {
							start();
						}
					}, 5 * 1000);// 暂停5秒后重试
				}
			}
			else
			{
				logger.error("["+TAG+"-↑] - [start()中]【严重】factory还没有准备好，start()失败！(原因：factory==null)");
			}
		}
		finally
		{
			startRunning = false;
		}
	}
	
	protected void whenConnected(Connection conn)
	{
		this.startPublisher(conn);
		this.startWorker(conn);
	}
	
	protected void startPublisher(Connection conn)
	{
		if(conn != null)
		{
			if(_pubChannel != null && _pubChannel.isOpen())
			{
				try{
					_pubChannel.close();
				}
				catch (Exception e){
					logger.warn("["+TAG+"-↑] - [startPublisher()中]pubChannel.close()时发生错误。", e);
				}
			}

			try
			{
				_pubChannel = conn.createChannel();

				logger.info("["+TAG+"-↑] - [startPublisher()中] 的channel成功创建了，" +
						"马上开始循环publish消息，当前数组队列长度：N/A！【OK】");//"+offlinePubQueue.size()+"！【OK】");

				String queue = this.publishToQueue;     //queue name 
				boolean durable = true;     //durable - RabbitMQ will never lose the queue if a crash occurs
				boolean exclusive = false;  //exclusive - if queue only will be used by one connection
				boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

				AMQP.Queue.DeclareOk qOK = _pubChannel.queueDeclare(queue, durable, exclusive, autoDelete, null);

				logger.info("["+TAG+"-↑] - [startPublisher中] Queue[当前队列消息数："+qOK.getMessageCount()
						+",消费者："+qOK.getConsumerCount()+"]已成功建立，Publisher初始化成功，"
						+"消息将可publish过去且不怕丢失了。【OK】(当前暂存数组长度:N/A)");//"+offlinePubQueue.size()+")");

				if(publishTrayAgainEnable)
				{
					while(publishTrayAgainCache.size()>0)
					{
						String[] m = publishTrayAgainCache.poll();
						if(m != null && m.length > 0)
						{
							logger.debug("["+TAG+"-↑] - [startPublisher()中] [...]在channel成功创建后，正在publish之前失败暂存的消息 m[0]="+m[0]
									+"、m[1]="+m[1]+",、m[2]="+m[2]+"，[当前数组队列长度："+publishTrayAgainCache.size()+"]！【OK】");
							publish(m[0], m[1], m[2]);
						}
						else
						{
							logger.debug("["+TAG+"-↑] - [startPublisher()中] [___]在channel成功创建后，" +
									"当前之前失败暂存的数据队列已为空，publish没有继续！[当前数组队列长度："+publishTrayAgainCache.size()+"]！【OK】");
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error("["+TAG+"-↑] - [startPublisher()中] conn.createChannel()或pubChannel.queueDeclare()" +
						"出错了，本次startPublisher没有继续！", e);
			}
		}
		else
		{
			logger.error("["+TAG+"-↑] - [startPublisher()中]【严重】connction还没有准备好" +
					"，conn.createChannel()失败！(原因：connction==null)");
		}
	}
	
	public boolean publish(String message)
	{
		return this.publish("", this.publishToQueue, message);
	}
	
	protected boolean publish(String exchangeName, String routingKey, String message)
	{
		boolean ok = false;
		
		try
		{
			_pubChannel.basicPublish(exchangeName, routingKey
					, MessageProperties.PERSISTENT_TEXT_PLAIN
					, message.getBytes(this.encodeCharset));
			logger.info("["+TAG+"-↑] - [startPublisher()中] publish()成功了 ！(数据:"
					+exchangeName+","+routingKey+","+message+")");
			ok = true;
		}
		catch (Exception e)
		{
			if(publishTrayAgainEnable)
			{
				publishTrayAgainCache.add(new String[]{exchangeName, routingKey, message});
			}
			
			logger.error("["+TAG+"-↑] - [startPublisher()中] publish()时Exception了，" +
					"原因："+e.getMessage()+"【数据["+exchangeName+","+routingKey+","+message+"]已重新放回数组首位"+
		            "，当前数组长度：N/A】", e);//"+offlinePubQueue.size()+"】", e);
		}
		return ok;
	}
	
	protected void startWorker(Connection conn)
	{
		if(this.retryWorkerRunning)
			return;

		try
		{
		if(conn != null)
		{
				final Channel resumeChannel = conn.createChannel();
				
				String queueName = this.consumFromQueue;//queue name 
				
				DefaultConsumer dc = new DefaultConsumer(resumeChannel) {
					@Override
					public void handleDelivery(String consumerTag,Envelope envelope,
							AMQP.BasicProperties properties,byte[] body)throws IOException{
						String routingKey = envelope.getRoutingKey();
						String contentType = properties.getContentType();

						long deliveryTag = envelope.getDeliveryTag();
						
						logger.info("["+TAG+"-↓] - [startWorker()中] 收到一条新消息(routingKey="
								+routingKey+",contentType="+contentType+",consumerTag="+consumerTag
								+",deliveryTag="+deliveryTag+")，马上开始处理。。。。");

						boolean workOK = work(body);
						if(workOK){
							resumeChannel.basicAck(deliveryTag, false);
						}
						else{
							resumeChannel.basicReject(deliveryTag, true);
						}
					}
				};
				
				boolean autoAck = false;
				resumeChannel.basicConsume(queueName, autoAck,dc);
				
				logger.info("["+TAG+"-↓] - [startWorker()中] Worker已经成功开启并运行中...【OK】");
			
		}
		else
		{
			throw new Exception("["+TAG+"-↓] - 【严重】connction还没有准备好，conn.createChannel()失败！(原因：connction==null)");
		}
		}
		catch (Exception e)
		{
			logger.error("["+TAG+"-↓] - [startWorker()中] conn.createChannel()或Consumer操作时" +
					"出错了，本次startWorker没有继续【暂停5秒后重试startWorker()】！", e);
			
			this.timerForRetryWorker.schedule(new TimerTask() {
				public void run() {
					startWorker(MQProvider.this._connection);
				}
			}, 5 * 1000);// 暂停5秒后重试
		}
		finally
		{
			retryWorkerRunning = false;
		}
	}
	
	protected boolean work(byte[] contentBody)
	{
		try
		{
			String msg = new String(contentBody, this.decodeCharset);
			// just log for debug
			logger.info("["+TAG+"-↓] - [startWorker()中] Got msg："+msg);
			return true;
		}
		catch (Exception e)
		{
			logger.warn("["+TAG+"-↓] - [startWorker()中] work()出现错误，错误将被记录："+e.getMessage(), e);
//			return false;
			return true;
		}
	}
	
//	public static void main(String[] args)// throws Exception
//	{
//		MQProvider mqp = MQProvider.getInstance();
//		{
//			mqp.start();
//			
//			while(true)
//			{
//				String message = "Hello AMQP!("+(new Date().toLocaleString()+")-from APP Server");
////				String exchangeName = "";
////				String routingKey = IMMQ_QUEUE_APP2WEB;
//				mqp.publish(message);
//				
////				try
////				{
////					Thread.sleep(15*1000);
////				}
////				catch (Exception e)
////				{
////					e.printStackTrace();
////				}
//			}
//		}
//	}
}
