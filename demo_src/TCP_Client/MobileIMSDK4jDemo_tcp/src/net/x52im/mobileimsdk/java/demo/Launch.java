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
 * Launch.java at 2022-7-16 16:53:48, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.eva.epc.common.util.OS;

/**
 * Demo程序启动入口类.
 * 
 * @author Jack Jiang
 */
public class Launch
{
	private static void initUserInterface()
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MobileIMSDK4jDemo");
		try
		{
			UIManager.put("RootPane.setupButtonVisible", false);
			BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
			BeautyEyeLNFHelper.frameBorderStyle = 
					BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
			
			if(!(OS.isWindowsXP() || OS.isWindows2003()))
			{
				/** UIManager中UI字体相关的key */
				String[] DEFAULT_FONT  = new String[]{
						"Table.font"
						,"TableHeader.font"
						,"CheckBox.font"
						,"Tree.font"
						,"Viewport.font"
						,"ProgressBar.font"
						,"RadioButtonMenuItem.font"
						,"ToolBar.font"
						,"ColorChooser.font"
						,"ToggleButton.font"
						,"Panel.font"
						,"TextArea.font"
						,"Menu.font"
						,"TableHeader.font"
						// ,"TextField.font"
						,"OptionPane.font"
						,"MenuBar.font"
						,"Button.font"
						,"Label.font"
						,"PasswordField.font"
						,"ScrollPane.font"
						,"MenuItem.font"
						,"ToolTip.font"
						,"List.font"
						,"EditorPane.font"
						,"Table.font"
						,"TabbedPane.font"
						,"RadioButton.font"
						,"CheckBoxMenuItem.font"
						,"TextPane.font"
						,"PopupMenu.font"
						,"TitledBorder.font"
						,"ComboBox.font" 
				};
				// 调整默认字体
				for (int i = 0; i < DEFAULT_FONT.length; i++)
					UIManager.put(DEFAULT_FONT[i],new Font("微软雅黑", Font.PLAIN,12));
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void runOnUiThread(Runnable r)
	{
		SwingUtilities.invokeLater(r);
	}

	public static void main(final String... args)
	{
		// init gui properties
		Launch.initUserInterface();
		// startup GUI
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				LoginGUI loginFrame = new LoginGUI();
				loginFrame.setLocationRelativeTo(null);
				loginFrame.setVisible(true);
			}
		});
	}
}