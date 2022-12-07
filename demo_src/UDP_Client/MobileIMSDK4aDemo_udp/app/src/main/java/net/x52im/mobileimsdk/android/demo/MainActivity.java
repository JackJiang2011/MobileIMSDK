/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_UDP (MobileIMSDK v6.x UDP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * MainActivity.java at 2022-7-28 17:21:45, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import net.x52im.mobileimsdk.android.core.AutoReLoginDaemon;
import net.x52im.mobileimsdk.android.core.KeepAliveDaemon;
import net.x52im.mobileimsdk.android.core.QoS4ReciveDaemon;
import net.x52im.mobileimsdk.android.core.QoS4SendDaemon;
import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.core.LocalDataSender;
import net.x52im.mobileimsdk.android.demo.service.GeniusService;
import net.x52im.mobileimsdk.android.utils.MBAsyncTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Demo的主界面。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
public class MainActivity extends AppCompatActivity {
	private final static String TAG = MainActivity.class.getSimpleName();
	
	private Button btnLogout = null;
	
	private EditText editId = null;
	private EditText editContent = null;
	private TextView viewStatus = null;
	private ImageView imgStatus = null;
	private TextView viewMyid = null;
	private Button btnSend = null;
	
	private ListView chatInfoListView;
	private MyAdapter chatInfoListAdapter;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.demo_main_activity_layout);
		
		initViews();
		initListeners();
		initOthers();

		// 启动前台服务（注意：该服务仅用于提升Demo的运行优先级，确保在高版本Andriod系统上进程保活和网络保活，此服务与SDK本身无关，也不是必须的）
		doBindService();
	}

	/**
	 * Activity每次从后台回到前台时调用本方法。
	 */
	protected void onResume() {
		super.onResume();

		// just for debug START：Refresh MobileIMSDK background status to show
    	this.refreshMobileIKSDKThreadStatusForDEBUG();
		// just for debug END
	}

	/** 
	 * 捕获back键，实现调用 {@link #doExit()}方法.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		// ** 注意：Android程序要么就别处理，要处理就一定要退干净，否则会有意想不到的问题哦！
		// 退出登陆
		doLogout();
		// 退出程序
		doExit();
	}

	@Override
	protected void onDestroy() {
		// 释放IM占用资源
		IMClientManager.getInstance(this).release();

		// 解绑前台服务（注意：该服务仅用于提升Demo的运行优先级，确保在高版本
		// Andriod系统上进程保活和网络保活，此服务与SDK本身无关，也不是必须的）
		doUnbindService();

		super.onDestroy();
	}
	
	private void initViews() {
		btnLogout = (Button)this.findViewById(R.id.logout_btn);
		
		btnSend = this.findViewById(R.id.send_btn);
		editId = this.findViewById(R.id.id_editText);
		editContent = this.findViewById(R.id.content_editText);
		viewStatus = this.findViewById(R.id.status_view);
		imgStatus = this.findViewById(R.id.status_iconView);
		viewMyid = this.findViewById(R.id.myid_view);
		
		chatInfoListView = this.findViewById(R.id.demo_main_activity_layout_listView);
		chatInfoListAdapter = new MyAdapter(this);
		chatInfoListView.setAdapter(chatInfoListAdapter);

		this.viewMyid.setText(ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId());
		
		this.setTitle("MobileIMSDK_UDP v6 Demo");

		// just for debug START
		this.initObserversForDEBUG();
		// just for debug END
	}
	
	private void initListeners() {
		btnLogout.setOnClickListener(v -> {
			// 退出登陆
			doLogout();
			// 退出程序
			doExit();
		});
		
		btnSend.setOnClickListener(v -> doSendMessage());
	}
	
	private void initOthers() {
		// Refresh userId to show
		refreshMyid();
		
		// Set MainGUI instance refrence to listeners
		// * 说明：正式的APP项目中，建议在Application中管理IMClientManager类，确保SDK的生命周期同步于整个APP的生命周期
		IMClientManager.getInstance(this).getChatMessageListener().setMainGUI(this);
		IMClientManager.getInstance(this).getChatBaseListener().setMainGUI(this);
		IMClientManager.getInstance(this).getMessageQoSListener().setMainGUI(this);
	}
	
	public void refreshMyid() {
		boolean connectedToServer = ClientCoreSDK.getInstance().isConnectedToServer();
		if(connectedToServer) {
			this.viewStatus.setText("通信正常");
			this.viewStatus.setTextColor(getResources().getColor(R.color.common_light_green));
			this.imgStatus.setImageResource(R.drawable.green);
		} else{
			this.viewStatus.setText("连接断开");
			this.viewStatus.setTextColor(getResources().getColor(R.color.common_light_red));
			this.imgStatus.setImageResource(R.drawable.red);
		}
	}
	
	private void doSendMessage() {
		String msg = editContent.getText().toString().trim();
		String friendId = editId.getText().toString().trim();
		if(msg.length() > 0 && friendId.length() > 0) {
			showIMInfo_black("我对"+friendId+"说："+msg);
			
			// 发送消息（Android系统要求必须要在独立的线程中发送哦）
		    new LocalDataSender.SendCommonDataAsync(msg, friendId) {
				@Override
				protected void onPostExecute(Integer code) {
					if(code == 0)
						Log.d(TAG, "2数据已成功发出！");
					else
						Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
				}
			}.execute();  		
		} else {
			showIMInfo_red("接收者id或发送内容为空，发送没有继续!");
			Log.e(TAG, "msg.len="+msg.length()+",friendId.len="+friendId.length());
		}
	}
	
	public void doLogout() {
		// 发出退出登陆请求包（Android系统要求必须要在独立的线程中发送哦）
		new MBAsyncTask() {
			@Override
			protected Integer doInBackground(Object... params) {
				int code = -1;
				try{
					code = LocalDataSender.getInstance().sendLoginout();
				} catch (Exception e){
					Log.w(TAG, e);
				}
				
				//## BUG FIX: 20170713 START by JackJiang
				// 退出登陆时记得一定要调用此行，不然不退出APP的情况下再登陆时会报 code=203错误哦！
				IMClientManager.getInstance(MainActivity.this).resetInitFlag();
				//## BUG FIX: 20170713 END by JackJiang
				
				return code;
			}

			@Override
			protected void onPostExecute(Integer code) {
				refreshMyid();
				if(code == 0)
					Log.d(TAG, "注销登陆请求已完成！");
				else
					Toast.makeText(getApplicationContext(), "注销登陆请求发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}

	public void doExit() {
		finish();
		System.exit(0);
	}
	
	//--------------------------------------------------------------- 各种信息输出方法 START
	public void showIMInfo_black(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.black);
	}

	public void showIMInfo_blue(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.blue);
	}

	public void showIMInfo_brightred(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.brightred);
	}

	public void showIMInfo_red(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.red);
	}

	public void showIMInfo_green(String txt) {
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.green);
	}
	//--------------------------------------------------------------- 各种信息输出方法 END
	
	//--------------------------------------------------------------- inner classes START
	/**
	 * 各种显示列表Adapter实现类。
	 */
	public class MyAdapter extends BaseAdapter {
		private final List<Map<String, Object>> mData;
        private final LayoutInflater mInflater;
        private final SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
         
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            mData = new ArrayList<>();
        }
        
        public void addItem(String content, ChatInfoColorType color) {
        	Map<String, Object> it = new HashMap<String, Object>();
        	it.put("__content__", "["+hhmmDataFormat.format(new Date())+"]"+content);
        	it.put("__color__", color);
        	mData.add(it);
        	this.notifyDataSetChanged();
        	chatInfoListView.setSelection(this.getCount());
        }
        
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();  
                convertView = mInflater.inflate(R.layout.demo_main_activity_list_item_layout, null);
                holder.content = (TextView)convertView.findViewById(R.id.demo_main_activity_list_item_layout_tvcontent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
             
            holder.content.setText((String)mData.get(position).get("__content__"));
            ChatInfoColorType colorType = (ChatInfoColorType)mData.get(position).get("__color__");
            switch(colorType) {
	            case blue:
	            	holder.content.setTextColor(Color.rgb(0,0,255));  
	            	break;
	            case brightred:
	            	holder.content.setTextColor(Color.rgb(255,0,255));  
	            	break;
	            case red:
	            	holder.content.setTextColor(Color.rgb(255,0,0));  
	            	break;
	            case green:
	            	holder.content.setTextColor(Color.rgb(0,128,0));  
	            	break;
	            case black:
	            default:
	            	holder.content.setTextColor(Color.rgb(0, 0, 0));  
	            	break;
            }
             
            return convertView;
        }
        
        public final class ViewHolder {
            public TextView content;
        }
    }
	
	/**
	 * 信息颜色常量定义。
	 */
	public enum ChatInfoColorType {
    	black,
    	blue,
    	brightred,
    	red,
    	green,
    }
	//--------------------------------------------------------------- inner classes END

	//--------------------------------------------------------------- 前台服务相关代码 START
	/** 前台服务对象（绑定MobileIMSDK的Demo后，确保Demo能常驻内存，因为Andriod高版本对于进程保活、网络保活现在限制非常严格） */
	private GeniusService boundService;

	/** 绑定时需要使用的连接对象 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			boundService = ((GeniusService.LocalBinder)service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			boundService = null;
		}
	};

	/**
	 * 将本activity与后台服务绑定起来.
	 */
	protected void doBindService() {
		this.getApplicationContext().bindService(new Intent(this.getApplicationContext(), GeniusService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 解绑服务（服务将失去功能，随时会被系统回收）.
	 */
	protected void doUnbindService() {
		try{
			this.getApplicationContext().unbindService(serviceConnection);
		} catch (Exception e){
//			Log.w(TAG, e);
		}
	}
	//--------------------------------------------------------------- 前台服务相关代码 END

	//--------------------------------------------------------------- just for debug START
	/* 以下代码用于DEBUG时显示各种SDK里的线程状态状态 */

	private void refreshMobileIKSDKThreadStatusForDEBUG() {
		this.showDebugStatusImage(AutoReLoginDaemon.getInstance().isAutoReLoginRunning()?1:0, findViewById(R.id.demo_main_activity_layout_autoLoginFlagIV));
		this.showDebugStatusImage(KeepAliveDaemon.getInstance().isKeepAliveRunning()?1:0, findViewById(R.id.demo_main_activity_layout_keepAliveFlagIV));
		this.showDebugStatusImage(QoS4SendDaemon.getInstance().isRunning()?1:0, findViewById(R.id.demo_main_activity_layout_qosSendFlagIV));
		this.showDebugStatusImage(QoS4ReciveDaemon.getInstance().isRunning()?1:0, findViewById(R.id.demo_main_activity_layout_qosReceiveFlagIV));
	}

	private void initObserversForDEBUG() {
		AutoReLoginDaemon.getInstance().setDebugObserver(createObserverCompletionForDEBUG(findViewById(R.id.demo_main_activity_layout_autoLoginFlagIV)));
		KeepAliveDaemon.getInstance().setDebugObserver(createObserverCompletionForDEBUG(findViewById(R.id.demo_main_activity_layout_keepAliveFlagIV)));
		QoS4SendDaemon.getInstance().setDebugObserver(createObserverCompletionForDEBUG(findViewById(R.id.demo_main_activity_layout_qosSendFlagIV)));
		QoS4ReciveDaemon.getInstance().setDebugObserver(createObserverCompletionForDEBUG(findViewById(R.id.demo_main_activity_layout_qosReceiveFlagIV)));
	}

	private Observer createObserverCompletionForDEBUG(ImageView iv) {
		return (o, arg) -> {
			if(arg != null) {
				int status = (int) arg;
				showDebugStatusImage(status, iv);
			}
		};
	}

	private void showDebugStatusImage(int status , ImageView iv) {
		if(iv.getVisibility() == View.INVISIBLE || iv.getVisibility() == View.GONE)
			iv.setVisibility(View.VISIBLE);

		// 持续运行中
		if(status == 1)
			iv.setImageResource(R.drawable.green);
		// 单次执行
		else if(status == 2) {
			iv.setImageResource(R.drawable.thread_live_anim);
			((AnimationDrawable) iv.getDrawable()).start();
		}
		// 已停止
		else
			iv.setImageResource(R.drawable.gray);
	}
	//--------------------------------------------------------------- just for debug END
}
