//package net.openmob.mobileimsdk.android.demo;
//
//import android.app.Application;
//
///**
// * 为遵从Android开发最佳实践，APP中的所有全局变量应存放于此类中.
// * 
// * @author Jack Jiang, 2015-11-07
// * @version 1.0
// * @since 2.1.2
// */
//public class MyApplication extends Application
//{
//	private static MyApplication self = null;
//	
////	private IMClientManager IMClientMgr = null;
//	
//	public void onCreate() 
//	{  
//        super.onCreate(); 
//        
//        //
//        self = this;
//        
////        // init MobileIMSDK first
////        IMClientMgr = new IMClientManager(this);
//    } 
//	
//	public IMClientManager getIMClientMgr()
//	{
////		if(IMClientMgr != null && !IMClientMgr.isInit())
////			IMClientMgr.initMobileIMSDK();
////		return IMClientMgr;
//		
//		return IMClientManager.getInstance(this);
//	}
//
//	/**
//	 * <p>
//	 * 一个获得本application对象的方便方法.
//	 * <p>
//	 * 相当于在你的activity中调用：(MyApplication)this.getApplicationContext()
//	 * ，本方法只是为了简化操作而已.
//	 * 
//	 * @param context
//	 * @return
//	 */
//	public static MyApplication getInstance()//Context context)
//	{
////		return (MyApplication)context.getApplicationContext();
//		return self;
//	}
//
//}
