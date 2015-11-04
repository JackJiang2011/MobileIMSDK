/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * DemoMain.java at 2015-10-7 22:03:00, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.openmob.mobileimsdk.java.ClientCoreSDK;
import net.openmob.mobileimsdk.java.conf.ConfigEntity;
import net.openmob.mobileimsdk.java.core.LocalUDPDataSender;
import net.openmob.mobileimsdk.java.utils.Log;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.swing9patch.toast.Toast;
import org.jdesktop.swingworker.SwingWorker;

import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.widget.HardLayoutPane;

public class DemoMain extends JFrame
{
	private final static String TAG = DemoMain.class.getSimpleName();
	
	private JTextField editServerIp = null;
	private JTextField editServerPort = null;
	
	private JTextField editLoginName = null;
	private JPasswordField editLoginPsw = null;
	private JButton btnLogin = null;
	private JButton btnLogout = null;
	
	private JTextField editId = null;
	private JTextField editContent = null;
	private JButton btnSend = null;
	private JLabel viewMyid = null;
	
	private JTextPane debugPane;
	private JTextPane imInfoPane;
	
	private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
	
	public DemoMain()
	{	
		initViews();
		initListeners();
		initOthers();
	}
	
	private void initViews()
	{
		// 登陆组件初始化
		editServerIp = new JTextField(16);
		editServerPort = new JTextField(5);
		editServerIp.setForeground(new Color(13,148,252));
		editServerPort.setForeground(new Color(13,148,252));
		editServerIp.setText("rbcore.openmob.net");	// default value
		editServerPort.setText("7901");	// default value
		btnLogin = new JButton("登陆");
		btnLogin.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
		btnLogin.setForeground(Color.white);
		editLoginName = new JTextField(22);
		editLoginPsw = new JPasswordField(22);
		btnLogout = new JButton("退出");
		viewMyid = new JLabel();
		viewMyid.setForeground(new Color(255,0,255));
		viewMyid.setText("未登陆");
		
		// 消息发送组件初始化
		btnSend = new JButton("发送消息");
		btnSend.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		btnSend.setForeground(Color.white);
		editId = new JTextField(20);
//		editContent = new JTextArea(4,5);
		editContent = new JTextField(20);
		
		// debug信息显示面板初始化
		debugPane=new JTextPane();
		debugPane.setBackground(Color.black);
//		debugPane.setFont(new Font("Verdana",Font.PLAIN,12));
		debugPane.setCaretColor(Color.white);
		//
		Log.getInstance().setLogDest(debugPane);
		
		// 聊天信息显示面板初始化
		imInfoPane=new JTextPane();
//		imInfoPane.setBackground(ColorHelper.LIGHT_YELLOW1);
//		imInfoPane.setFont(new Font("Verdana",Font.PLAIN,10));
//		imInfoPane.setFont(new Font("宋体",Font.PLAIN,12));
		
		// 登陆信息布局
		HardLayoutPane authPanel = new HardLayoutPane();
		JPanel serverInfoPane = new JPanel(new BorderLayout());
		JPanel portInfoPane = new JPanel(new BorderLayout());
		portInfoPane.add(new JLabel("："), BorderLayout.WEST);
		portInfoPane.add(editServerPort, BorderLayout.CENTER);
		serverInfoPane.add(editServerIp, BorderLayout.CENTER);
		serverInfoPane.add(portInfoPane, BorderLayout.EAST);
		authPanel.addTo(serverInfoPane, 2, true);
		authPanel.nextLine();
		authPanel.addTo(new JLabel("用户名："), 1, true);
		authPanel.addTo(editLoginName, 1, true);
		authPanel.nextLine();
		authPanel.addTo(new JLabel("密  码："), 1, true);
		authPanel.addTo(editLoginPsw, 1, true);
		authPanel.nextLine();
		authPanel.addTo(btnLogin, 1, true);
		authPanel.addTo(btnLogout, 1, true);
		authPanel.nextLine();
		authPanel.addTo(new JLabel("我的id："), 1, true);
		JPanel idAndVerPanel = new JPanel();
		idAndVerPanel.setLayout(new BoxLayout(idAndVerPanel, BoxLayout.LINE_AXIS));
		JLabel lbVer= new JLabel("v2.1.1b151104.1O");
		lbVer.setForeground(new Color(184,184,184));
		idAndVerPanel.add(viewMyid);
		idAndVerPanel.add(Box.createHorizontalGlue());
		idAndVerPanel.add(lbVer);
		authPanel.addTo(idAndVerPanel, 1, true);
		authPanel.nextLine();
//		authPanel.addTitledLineSeparator("");
		
		// 消息发送布局
		HardLayoutPane toPanel = new HardLayoutPane();
		toPanel.addTo(new JLabel("对方ID号："), 1, true);
		toPanel.addTo(editId, 1, true);
		toPanel.nextLine();
		toPanel.addTo(new JLabel("发送内容："), 1, true);
		toPanel.addTo(editContent, 1, true);
		toPanel.nextLine();
		toPanel.addTo(btnSend, 4, true);
		toPanel.nextLine();
		
		HardLayoutPane oprPanel = new HardLayoutPane();
		oprPanel.addTitledLineSeparator("登陆认证");
		oprPanel.addTo(authPanel, 1, true);
		oprPanel.addTitledLineSeparator("消息发送");
		oprPanel.addTo(toPanel, 1, true);
		oprPanel.addTitledLineSeparator();
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(oprPanel, BorderLayout.NORTH);
		JScrollPane imInfoSc = new JScrollPane(imInfoPane);
		imInfoSc.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 7, 0, 7), imInfoSc.getBorder()));
		imInfoSc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftPanel.add(imInfoSc, BorderLayout.CENTER);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(leftPanel, BorderLayout.WEST);
		JScrollPane sc = new JScrollPane(debugPane);
		sc.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 0, 0, 2), sc.getBorder()));
		sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(sc, BorderLayout.CENTER);
		
		this.setTitle("MobileIMSDK演示工程 - (作者:Jack Jiang, 讨论区:openmob.net, QQ群:215891622)");
//		this.pack();
		this.setLocationRelativeTo(null);
		this.setSize(1000,700);
	}
	
	public void showToast(String text)
	{
		Toast.showTost(3000, text, new Point((int)(this.getLocationOnScreen().getX())+50,
				(int)(this.getLocationOnScreen().getY())+400));
	}
	
	private void initListeners()
	{
		btnLogin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// 设置服务器地址和端口号
				String serverIP = editServerIp.getText();
				String serverPort = editServerPort.getText();
				if(!CommonUtils.isStringEmpty(serverIP, true)
					&& !CommonUtils.isStringEmpty(serverPort, true))
				{
					ConfigEntity.serverIP = serverIP.trim();
					try
					{
						ConfigEntity.serverUDPPort = Integer.parseInt(serverPort.trim());
					}
					catch (Exception e2)
					{
						showToast("请输入合法的端口号！");
						Log.e(DemoMain.class.getSimpleName(), "请输入合法的端口号！");
						return;
					}
				}
				else
				{
					showToast("请确保服务端地址和端口号都不为空！");
					Log.e(DemoMain.class.getSimpleName(), "请确保服务端地址和端口号都不为空！");
					return;
				}
				
				// 发送登陆数据包
				if(editLoginName.getText().toString().trim().length() > 0)
				{
					// 提交登陆名和密码
					new LocalUDPDataSender.SendLoginDataAsync(editLoginName.getText()
							, editLoginPsw.getText())
					{
						protected void fireAfterSendLogin(int code)
						{
							if(code == 0)
							{
								showToast("数据发送成功！");
								Log.i(DemoMain.class.getSimpleName(), "登陆信息已成功发出！");
							}
							else
							{
								showToast("数据发送失败。错误码是："+code+"！");
								Log.w(DemoMain.class.getSimpleName(), "数据发送失败。错误码是："+code+"！");
							}
						}
					}.execute();
				}
				else
					Log.e(DemoMain.class.getSimpleName()
							, "登陆名长度="+(editLoginName.getText().toString().trim().length()));
			}
		});
		
		btnLogout.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// 发出退出登陆请求包
//				new LocalUDPDataSender.SendDataAsync(){
				new SwingWorker<Integer, Object>(){
					@Override
					protected Integer doInBackground()
					{
						int code = LocalUDPDataSender.getInstance().sendLoginout();
						return code;
					}
					
					@Override
					protected void done()
					{
						int code = -1;
						try
						{
							code = get();
						}
						catch (Exception e)
						{
							Log.w(TAG, e.getMessage());
						}
						
						onPostExecute(code);
					}

					protected void onPostExecute(Integer code)
					{
						setMyid(-1);
						if(code == 0)
							Log.i(DemoMain.class.getSimpleName(), "注销登陆请求已完成！");
						else
							showToast("注销登陆请求发送失败。错误码是："+code+"！");
					}
				}.execute();
			}
		});
		
		btnSend.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				final String msg = editContent.getText().toString().trim();
				if(msg.length() > 0)
				{
					int friendId = Integer.parseInt(editId.getText().toString().trim());
					DemoMain.this.showIMInfo_black("我对"+friendId+"说："+msg);
					
					//** [1] 发送不需要QoS支持的消息
//					new LocalUDPDataSender.SendCommonDataAsync(msg, friendId)
//					{
//						@Override
//						protected void onPostExecute(Integer code)
//						{
//							if(code == 0)
//								Log.d(DemoMain.class.getSimpleName(), "2数据已成功发出！");
//							else
//								showToast("数据发送失败。错误码是："+code+"！");
//						}
//					}.execute();
					
					//** [2] 发送需要QoS支持的消息
//				    String fingerPring = Protocal.genFingerPrint();
//				    Protocal p = ProtocalFactory.createCommonData(msg
//				    		, ClientCoreSDK.getInstance().getCurrentUserId()
//				    		, friendId, true, fingerPring);
				    new LocalUDPDataSender.SendCommonDataAsync(msg, friendId, true)
					{
						@Override
						protected void onPostExecute(Integer code)
						{
							if(code == 0)
							{
								Log.i(DemoMain.class.getSimpleName(), "2数据已成功发出！");
							}
							else
								showToast("数据发送失败。错误码是："+code+"！");
						}
					}.execute();  		
				}
				else
					Log.e(DemoMain.class.getSimpleName(), "消息内容长度="+(msg.length()));
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				// 释放IM占用资源
				ClientCoreSDK.getInstance().release();
				// JVM退出
				System.exit(0);
			}
		});
	}
	
	private void initOthers()
	{
		// 设置AppKey
	    ConfigEntity.appKey = "5418023dfd98c579b6001741";
		
	    // 设置服务器ip和服务器端口
//		ConfigEntity.serverIP = "192.168.82.138";
//		ConfigEntity.serverIP = "rbcore.cngeeker.com";
//		ConfigEntity.serverUDPPort = 7901;
	    
		// MobileIMSDK核心IM框架的敏感度模式设置
//		ConfigEntity.setSenseMode(SenseMode.MODE_10S);
	    
	    // 开启/关闭DEBUG信息输出
//	    ClientCoreSDK.DEBUG = false;
		
	    // 设置事件回调
		ClientCoreSDK.getInstance().setChatTransDataEvent(new ChatTransDataEventImpl().set____temp(this));
		ClientCoreSDK.getInstance().setChatBaseEvent(new ChatBaseEventImpl().set____temp(this));
		ClientCoreSDK.getInstance().setMessageQoSEvent(new MessageQoSEventImpl().set____temp(this));
	}
	
	public void setMyid(int myid)
	{
//		_myid = (myid == -1 ? "" :""+myid);
		this.viewMyid.setText(myid == -1 ? "未登陆" :""+myid);
	}
	
	public void showIMInfo_black(String txt)
	{
		showIMInfo(new Color(0,0,0), txt);
	}
	public void showIMInfo_blue(String txt)
	{
		showIMInfo(new Color(0,0,255), txt);
	}
	public void showIMInfo_brightred(String txt)
	{
		showIMInfo(new Color(255,0,255), txt);
	}
	public void showIMInfo_red(String txt)
	{
		showIMInfo(new Color(255,0,0), txt);
	}
	public void showIMInfo_green(String txt)
	{
		showIMInfo(new Color(0,128,0), txt);
	}
	public void showIMInfo(Color c, String txt)
	{
		try
		{
			Log.append(c, "["+hhmmDataFormat.format(new Date())+"]"+txt+"\r\n", this.imInfoPane);
			imInfoPane.setCaretPosition(imInfoPane.getDocument().getLength());
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
	}
}
