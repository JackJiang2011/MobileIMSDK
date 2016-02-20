/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * Launch.java at 2016-2-20 11:22:59, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.java.demo;

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

	public static void main(final String... args)
	{
//		// init MobileIMSDK first
//		IMClientManager.getInstance().initMobileIMSDK();
		// init gui properties
		Launch.initUserInterface();
		// startup GUI
		SwingUtilities.invokeLater(new Runnable()
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