/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * NPIconFactory.java at 2016-2-20 11:23:00, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package org.jb2011.swing9patch.toast;

import org.jb2011.ninepatch4j.NinePatch;
import org.jb2011.swing9patch.utils.NPHelper;
import org.jb2011.swing9patch.utils.RawCacheRoot;

/**
 * Object factory of NinePatch pictures(*.9.png).
 * 
 * @author Jack Jiang
 * @version 1.0
 */
public class NPIconFactory extends RawCacheRoot<NinePatch>
{
	/** root path(relative this NPIconFactory.class). */
	public final static String IMGS_ROOT="imgs/np";

	/** The instance. */
	private static NPIconFactory instance = null;

	/**
	 * Gets the single instance of __Icon9Factory__.
	 *
	 * @return single instance of __Icon9Factory__
	 */
	public static NPIconFactory getInstance()
	{
		if(instance==null)
			instance = new NPIconFactory();
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.jb2011.lnf.beautyeye.utils.RawCache#getResource(java.lang.String, java.lang.Class)
	 */
	@Override
	protected NinePatch getResource(String relativePath, Class baseClass)
	{
		return NPHelper.createNinePatch(baseClass.getResource(relativePath), false);
	}

	/**
	 * Gets the raw.
	 *
	 * @param relativePath the relative path
	 * @return the raw
	 */
	public NinePatch getRaw(String relativePath)
	{
		return  getRaw(relativePath,this.getClass());
	}
	
	/**
	 * Gets the tooltip bg.
	 *
	 * @return the tooltip bg
	 */
	public NinePatch getToastBg()
	{
		return getRaw(IMGS_ROOT+"/toast_bg.9.png");
	}
	
}