/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v5.x TCP版) Project. 
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
 * MainActivity.java at 2020-8-8 16:00:38, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.android.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.x52im.mobileimsdk.android.ClientCoreSDK;
import net.x52im.mobileimsdk.android.core.LocalDataSender;
import net.x52im.mobileimsdk.android.demo.R;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Demo的主界面。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 */
public class MainActivity extends AppCompatActivity
{
	private final static String TAG = MainActivity.class.getSimpleName();
	
	private Button btnLogout = null;
	
	private EditText editId = null;
	private EditText editContent = null;
	private TextView viewStatus = null;
	private TextView viewMyid = null;
	private Button btnSend = null;
	
	private ListView chatInfoListView;
	private MyAdapter chatInfoListAdapter;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//
		this.setContentView(R.layout.demo_main_activity_layout);
		
		initViews();
		initListeners();
		initOthers();
	}
	
	/** 
	 * 捕获back键，实现调用 {@link #doExit()}方法.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		
		// ** 注意：Android程序要么就别处理，要处理就一定
		//			要退干净，否则会有意想不到的问题哦！
		// 退出登陆
		doLogout();
		// 退出程序
		doExit();
	}
	
	protected void onDestroy()
	{
		// 释放IM占用资源
		IMClientManager.getInstance(this).release();
		//
		super.onDestroy();
	}
	
	private void initViews()
	{
		btnLogout = (Button)this.findViewById(R.id.logout_btn);
		
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editId = (EditText)this.findViewById(R.id.id_editText);
		editContent = (EditText)this.findViewById(R.id.content_editText);
		viewStatus = (TextView)this.findViewById(R.id.status_view);
		viewMyid = (TextView)this.findViewById(R.id.myid_view);
		
		chatInfoListView = (ListView)this.findViewById(R.id.demo_main_activity_layout_listView);
		chatInfoListAdapter = new MyAdapter(this);
		chatInfoListView.setAdapter(chatInfoListAdapter);

		this.viewMyid.setText(ClientCoreSDK.getInstance().getInstance().getCurrentLoginUserId());
		
		this.setTitle("MobileIMSDK TCP v5 Demo");
	}
	
	private void initListeners()
	{
		btnLogout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				// 退出登陆
				doLogout();
				// 退出程序
				doExit();
			}
		});
		
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				doSendMessage();
			}
		});
	}
	
	private void initOthers()
	{
		// Refresh userId to show
		refreshMyid();

		// Set MainGUI instance refrence to listeners
		// * 说明：正式的APP项目中，建议在Application中管理IMClientManager类，确保SDK的生命周期同步于整个APP的生命周期
		IMClientManager.getInstance(this).getTransDataListener().setMainGUI(this);
		IMClientManager.getInstance(this).getBaseEventListener().setMainGUI(this);
		IMClientManager.getInstance(this).getMessageQoSListener().setMainGUI(this);
	}
	
	public void refreshMyid()
	{
		boolean connectedToServer = ClientCoreSDK.getInstance().isConnectedToServer();
		this.viewStatus.setText(connectedToServer ? "通信正常":"连接断开");
	}
	
	private void doSendMessage()
	{
		String msg = editContent.getText().toString().trim();
		String friendId = editId.getText().toString().trim();
		if(msg.length() > 0 && friendId.length() > 0)
		{
			showIMInfo_black("我对"+friendId+"说："+msg);
			
			// 发送消息（Android系统要求必须要在独立的线程中发送哦）
		    new LocalDataSender.SendCommonDataAsync(msg, friendId)//, true)
			{
				@Override
				protected void onPostExecute(Integer code)
				{
					if(code == 0)
						Log.d(MainActivity.class.getSimpleName(), "2数据已成功发出！");
					else
						Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
				}
			}.execute();  		
		}
		else
		{
			showIMInfo_red("接收者id或发送内容为空，发送没有继续!");
			Log.e(MainActivity.class.getSimpleName(), "msg.len="+msg.length()+",friendId.len="+friendId.length());
		}
	}
	
	private void doLogout()
	{
		// 发出退出登陆请求包（Android系统要求必须要在独立的线程中发送哦）
		new AsyncTask<Object, Integer, Integer>(){
			@Override
			protected Integer doInBackground(Object... params)
			{
				int code = -1;
				try{
					code = LocalDataSender.getInstance().sendLoginout();
				}
				catch (Exception e){
					Log.w(TAG, e);
				}
				
				//## BUG FIX: 20170713 START by JackJiang
				// 退出登陆时记得一定要调用此行，不然不退出APP的情况下再登陆时会报 code=203错误哦！
				IMClientManager.getInstance(MainActivity.this).resetInitFlag();
				//## BUG FIX: 20170713 END by JackJiang
				
				return code;
			}

			@Override
			protected void onPostExecute(Integer code)
			{
				refreshMyid();
				if(code == 0)
					Log.d(MainActivity.class.getSimpleName(), "注销登陆请求已完成！");
				else
					Toast.makeText(getApplicationContext(), "注销登陆请求发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
			}
		}.execute();
	}
	
	private void doExit()
	{
		finish();
		System.exit(0);
	}
	
	//--------------------------------------------------------------- 各种信息输出方法 START
	public void showIMInfo_black(String txt)
	{
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.black);
	}
	public void showIMInfo_blue(String txt)
	{
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.blue);
	}
	public void showIMInfo_brightred(String txt)
	{
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.brightred);
	}
	public void showIMInfo_red(String txt)
	{
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.red);
	}
	public void showIMInfo_green(String txt)
	{
		chatInfoListAdapter.addItem(txt, ChatInfoColorType.green);
	}
	//--------------------------------------------------------------- 各种信息输出方法 END
	
	//--------------------------------------------------------------- inner classes START
	/**
	 * 各种显示列表Adapter实现类。
	 */
	public class MyAdapter extends BaseAdapter
	{
		private List<Map<String, Object>> mData;
        private LayoutInflater mInflater;
        private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
         
        public MyAdapter(Context context)
        {
            this.mInflater = LayoutInflater.from(context);
            mData = new ArrayList<Map<String, Object>>();
        }
        
        public void addItem(String content, ChatInfoColorType color)
        {
        	Map<String, Object> it = new HashMap<String, Object>();
        	it.put("__content__", "["+hhmmDataFormat.format(new Date())+"]"+content);
        	it.put("__color__", color);
        	mData.add(it);
        	this.notifyDataSetChanged();
        	chatInfoListView.setSelection(this.getCount());
        }
        
        @Override
        public int getCount() 
        {
            return mData.size();
        }
 
        @Override
        public Object getItem(int arg0) 
        {
            return null;
        }
 
        @Override
        public long getItemId(int arg0) 
        {
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) 
        {
            ViewHolder holder = null;
            if (convertView == null) 
            {
                holder=new ViewHolder();  
                convertView = mInflater.inflate(R.layout.demo_main_activity_list_item_layout, null);
                holder.content = (TextView)convertView.findViewById(R.id.demo_main_activity_list_item_layout_tvcontent);
                convertView.setTag(holder);
            }
            else 
            {
                holder = (ViewHolder)convertView.getTag();
            }
             
            holder.content.setText((String)mData.get(position).get("__content__"));
            ChatInfoColorType colorType = (ChatInfoColorType)mData.get(position).get("__color__");
            switch(colorType)
            {
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
        
        public final class ViewHolder
        {
            public TextView content;
        }
    }
	
	/**
	 * 信息颜色常量定义。
	 */
	public enum ChatInfoColorType
    {
    	black,
    	blue,
    	brightred,
    	red,
    	green,
    }
	//--------------------------------------------------------------- inner classes END
}
