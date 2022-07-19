/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * LoginGUI.java at 2022-7-16 16:53:48, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import net.x52im.mobileimsdk.java.conf.ConfigEntity;
import net.x52im.mobileimsdk.java.core.LocalDataSender;
import net.x52im.mobileimsdk.java.core.LocalSocketProvider;
import net.x52im.mobileimsdk.server.protocal.c.PLoginInfo;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.swing9patch.toast.Toast;

import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.widget.HardLayoutPane;

public class LoginGUI extends JFrame
{
	private JTextField editServerIp = null;
	private JTextField editServerPort = null;
	private JTextField editLoginName = null;
	private JPasswordField editLoginPsw = null;
	private JButton btnLogin = null;
	/** 登陆进度提示 */
	private OnLoginProgress onLoginProgress = null;
	/** 收到服务端的登陆完成反馈时要通知的观察者（因登陆是异步实现，本观察者将由
	 *  ChatBaseEvent 事件的处理者在收到服务端的登陆反馈后通知之） */
	private Observer onLoginSucessObserver = null;
	
	public LoginGUI()
	{	
		// 界面UI基本设置
		initViews();
		initListeners();
		
		// 确保MobileIMSDK被初始化哦（整个APP生生命周期中只需调用一次哦）
		// 提示：在不退出APP的情况下退出登陆后再重新登陆时，请确保调用本方法一次，不然会报code=203错误哦！
		IMClientManager.getInstance().initMobileIMSDK();
		
		// 登陆有关的初始化工作
		initForLogin();
	}
	
	private void initViews()
	{
		// 登陆组件初始化
		editServerIp = new JTextField(16);
		editServerPort = new JTextField(5);
		editServerIp.setForeground(new Color(13,148,252));
		editServerPort.setForeground(new Color(13,148,252));
		editServerIp.setText("rbcore.52im.net");// default value 
		editServerPort.setText("8901");	// default value
		btnLogin = new JButton("  登 陆  ");
		btnLogin.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.blue));
		btnLogin.setForeground(Color.white);
		editLoginName = new JTextField(30);
		editLoginPsw = new JPasswordField(30);
		
		// 登陆信息主布局
		HardLayoutPane mainPanel = new HardLayoutPane();
		mainPanel.setComponentInsets(new Insets(4,10,4,10));
		JPanel serverInfoPane = new JPanel(new BorderLayout());
		JPanel portInfoPane = new JPanel(new BorderLayout());
		portInfoPane.add(new JLabel("："), BorderLayout.WEST);
		portInfoPane.add(editServerPort, BorderLayout.CENTER);
		serverInfoPane.add(editServerIp, BorderLayout.CENTER);
		serverInfoPane.add(portInfoPane, BorderLayout.EAST);
		mainPanel.addTo(serverInfoPane, 2, true);
		mainPanel.nextLine();
		mainPanel.addTo(new JLabel("用户名："), 1, true);
		mainPanel.addTo(editLoginName, 1, true);
		mainPanel.nextLine();
		mainPanel.addTo(new JLabel("密  码："), 1, true);
		mainPanel.addTo(editLoginPsw, 1, true);
		mainPanel.addTitledLineSeparator("");
		JPanel btnAndVerPanel = new JPanel();
		btnAndVerPanel.setLayout(new BoxLayout(btnAndVerPanel, BoxLayout.LINE_AXIS));
		JLabel lbVer= new JLabel("v6.2b220716.1");
		lbVer.setForeground(new Color(184,184,184));
		btnAndVerPanel.add(lbVer);
		btnAndVerPanel.add(Box.createHorizontalGlue());
		btnAndVerPanel.add(btnLogin);
		mainPanel.addTo(btnAndVerPanel, 2, true);
		
		// 下方的copyright面板
		LineBorder bottomPabelTopBorder = new LineBorder(new Color(235,235,235)){
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
			{
		        Color oldColor = g.getColor();
		        g.setColor(lineColor);
		        g.drawLine(x, y, width, y);
		        g.setColor(oldColor);
		    }
		};
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(Color.white);
		bottomPanel.setBorder(BorderFactory.createCompoundBorder(
				bottomPabelTopBorder, BorderFactory.createEmptyBorder(5, 0, 5, 0)));
		bottomPanel.add(
			new JLabel(new ImageIcon(LoginGUI.class.getResource("res/copyright_img.png")))
			, BorderLayout.CENTER);
		
		// 总体界面布局
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		// 窗体设置
		this.setTitle("MobileIMSDK_TCP v6 - Demo登陆");
		this.setResizable(false);
		this.pack();
	}
	
	private void initListeners()
	{
		btnLogin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				doLogin();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				MainGUI.doExit();
			}
		});
	}
	
	private void initForLogin()
	{
		// 实例化登陆进度提示封装类
		onLoginProgress = new OnLoginProgress();
		// 准备好异步登陆结果回调观察者（将在登陆方法中使用）
		onLoginSucessObserver = new Observer(){
			@Override
			public void update(Observable observable, Object data)
			{
				// * 已收到服务端登陆反馈则当然应立即取消显示登陆进度条
				onLoginProgress.showProgressing(false);
				// 服务端返回的登陆结果值
				int code = (Integer)data;
				// 登陆成功
				if(code == 0)
				{
					//## BUG FIX START: 20170718 by Jack Jiang 
					//## 让以下代码异步运行于EDT线程，从而解决登陆界面切到主界面时偶尔卡死问题
					// startup GUI
					Launch.runOnUiThread(new Runnable()
					{
						public void run()
						{
							//** 提示：登陆MobileIMSDK服务器成功后的事情在此实现即可
							// 进入主界面
							MainGUI frame = new MainGUI();
							frame.setLocationRelativeTo(null);
							frame.setVisible(true);

							// 同时关闭登陆界面
							LoginGUI.this.dispose();
						}
					});
					//## BUG FIX END: 20170718 by Jack Jiang 
				}
				// 登陆失败
				else
					JOptionPane.showMessageDialog(LoginGUI.this, "Sorry，登陆失败，错误码="+code
							, "友情提示",JOptionPane.ERROR_MESSAGE);  
			}
		};
	}
	
	/**
	 * 登陆处理。
	 */
	private void doLogin()
	{
		//** 设置服务器地址和端口号
		String serverIP = editServerIp.getText();
		String serverPort = editServerPort.getText();
		if(!CommonUtils.isStringEmpty(serverIP, true)
			&& !CommonUtils.isStringEmpty(serverPort, true))
		{
			// 无条件重置socket，防止首次登陆时用了错误的ip或域名，下次登陆时sendData中仍然使用老的ip
			// 说明：本行代码建议仅用于Demo时，生产环境下是没有意义的，因为你的APP里不可能连IP都搞错了
			LocalSocketProvider.getInstance().closeLocalSocket();

			// 设置好服务端的连接地址
			ConfigEntity.serverIP = serverIP.trim();
			
			try{
				// 设置好服务端的UDP监听端口号
				ConfigEntity.serverPort = Integer.parseInt(serverPort.trim());
			}
			catch (Exception e2){
				showToast("请输入合法的端口号！");
				return;
			}
		}
		else
		{
			showToast("请确保服务端地址和端口号都不为空！");
			return;
		}
		
		// 开始发送登陆信息
		if(editLoginName.getText().toString().trim().length() > 0
			&& editLoginPsw.getText().toString().trim().length() > 0)
		{
			doLoginImpl();
		}
		else
		{
			JOptionPane.showMessageDialog(LoginGUI.this
					, "帅哥，登陆用户名和密码不能为空，Demo运行时请随便输入^_^!", "友情提示", JOptionPane.WARNING_MESSAGE);  
		}
	}
	/**
	 * 真正的登陆信息发送实现方法。
	 */
	private void doLoginImpl()
	{
		// * 立即显示登陆处理进度提示（并将同时启动超时检查线程）
		onLoginProgress.showProgressing(true);
		// * 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
		IMClientManager.getInstance().getBaseEventListener()
			.setLoginOkForLaunchObserver(onLoginSucessObserver);

		PLoginInfo loginInfo = new PLoginInfo(editLoginName.getText(), editLoginPsw.getText());
		// * 异步提交登陆名和密码
		new LocalDataSender.SendLoginDataAsync(loginInfo){
			/**
			 * 登陆信息发送完成后将调用本方法（注意：此处仅是登陆信息发送完成
			 * ，真正的登陆结果要在异步回调中处理哦）。
			 * 
			 * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
			 */
			protected void fireAfterSendLogin(int code)
			{
				if(code == 0)
					showToast("数据发送成功！");
				else
				{
					showToast("数据发送失败。错误码是："+code+"！");

					// * 登陆信息没有成功发出时当然无条件取消显示登陆进度条
					onLoginProgress.showProgressing(false);
				}
			}
		}.execute();
	}
	
	public void showToast(String text)
	{
		Toast.showTost(3000, text, new Point((int)(this.getLocationOnScreen().getX()),
				(int)(this.getLocationOnScreen().getY())));
	}
	
	//-------------------------------------------------------------------------- inner classes
	/**
	 * 登陆进度提示和超时检测封装实现类.
	 */
	private class OnLoginProgress 
	{
		/** 登陆的超时时间定义 */
		private final static int RETRY_DELAY = 6000;
		/** 登陆超时计时器 */
		private Timer timer = null;
		
		public OnLoginProgress()
		{
			init();
		}
		
		private void init()
		{
			timer = new Timer(RETRY_DELAY, new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					onTimeout();
				}
			});
		}
		
		/**
		 * 登陆超时后要调用的方法。
		 */
		private void onTimeout()
		{
			Object[] options ={ "重试！", "取消" };  
			int n = JOptionPane.showOptionDialog(LoginGUI.this
					, "登陆超时，可能是网络故障或服务器无法连接，是否重试？", "超时了"
					, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);  
			// 确认要重试时（再次尝试登陆哦）
			if(n == JOptionPane.YES_OPTION)
				doLogin();
			// 不需要重试则要停止“登陆中”的进度提示哦
			else
				this.showProgressing(false);
		}
		
		/**
		 * 显示进度提示.
		 * 
		 * @param show
		 */
		public void showProgressing(boolean show)
		{
			// 显示进度提示的同时即启动超时提醒线程
			if(show)
			{
				showLoginProgressGUI(true);
				
				// 先无论如何保证timer在启动前肯定是处于停止状态
				if(timer != null)
					timer.stop();
				// 启动
				timer.start();
			}
			// 关闭进度提示
			else
			{
				// 无条件停掉延迟重试任务
				if(timer != null)
					timer.stop();
				
				showLoginProgressGUI(false);
			}
		}
		
		/**
		 * 进度提示时要显示或取消显示的GUI内容。
		 * 
		 * @param show true表示显示gui内容，否则表示结速gui内容显示
		 */
		private void showLoginProgressGUI(boolean show)
		{
			// 显示登陆提示信息
			if(show)
			{
				btnLogin.setText("登陆中,请稍候..");
				btnLogin.setEnabled(false);
			}
			// 关闭登陆提示信息
			else
			{
				btnLogin.setText("  登 陆  ");
				btnLogin.setEnabled(true);
			}
		}
	}
}
