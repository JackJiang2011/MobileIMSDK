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
 * ToastPane.java at 2022-7-16 16:53:48, code by Jack Jiang.
 */
package org.jb2011.swing9patch.toast;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jb2011.ninepatch4j.NinePatch;
import org.jb2011.swing9patch.utils.DragToMove;

public class ToastPane extends JPanel
{
	private NinePatch npBackground = null;
	private JComponent content = null;
	
	public ToastPane()
	{
		super(new BorderLayout());
		
		initGUI();
	}
	
	/**
	 * Override to impl ninepatch image background.
	 */
	@Override
	public void paintChildren(Graphics g)
	{
		if(npBackground == null)
			// load the nine patch .PNG
			npBackground = NPIconFactory.getInstance().getToastBg();
		if(npBackground != null)
			// paint background with ninepath
			npBackground.draw((Graphics2D)g, 0, 0, this.getWidth(), this.getHeight());
		super.paintChildren(g);
	}
	
	protected void initGUI()
	{
		this.setOpaque(false);
		
		content = createContent();
		// drag to move
		DragToMove.apply(new Component[]{content});
		
		this.add(content, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(19,20,28,20));
	}
	
	/**
	 * Subclass may be override to implement itself logic.
	 * 
	 * @return
	 */
	protected JComponent createContent()
	{
		JLabel lb = new JLabel("");
		lb.setForeground(new Color(230,230,230));
		return lb;
	}
	
	/**
	 * Subclass may be override to implement itself logic.
	 * 
	 * @param message
	 * @see #createContent()
	 */
	public void setMessage(String message)
	{
		((JLabel)content).setText(message);
	}
	
	public JComponent getContent()
	{
		return content;
	}
}
