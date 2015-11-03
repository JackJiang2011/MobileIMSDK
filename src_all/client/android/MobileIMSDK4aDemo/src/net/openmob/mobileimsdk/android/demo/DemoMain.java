/*
 * Copyright (C) 2015 Jack Jiang The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * openmob.net PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * DemoMain.java at 2015-10-7 22:01:48, code by Jack Jiang.
 * You can contact author with jack.jiang@openmob.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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

public class DemoMain extends Activity
{
	private final static String TAG = DemoMain.class.getSimpleName();
	
	private EditText editServerIp = null;
	private EditText editServerPort = null;
	
	private EditText editLoginName = null;
	private EditText editLoginPsw = null;
	private Button btnLogin = null;
	private Button btnLogout = null;
	
	private EditText editId = null;
	private EditText editContent = null;
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
		initIMCore();
	}
	
	private void initViews()
	{
		editServerIp = (EditText)this.findViewById(R.id.serverIP_editText);
		editServerPort = (EditText)this.findViewById(R.id.serverPort_editText);
		
		btnLogin = (Button)this.findViewById(R.id.login_btn);
		editLoginName = (EditText)this.findViewById(R.id.loginName_editText);
		editLoginPsw = (EditText)this.findViewById(R.id.loginPsw_editText);
		btnLogout = (Button)this.findViewById(R.id.logout_btn);
		
		btnSend = (Button)this.findViewById(R.id.send_btn);
		editId = (EditText)this.findViewById(R.id.id_editText);
		editContent = (EditText)this.findViewById(R.id.content_editText);
		viewMyid = (TextView)this.findViewById(R.id.myid_view);
		
		chatInfoListView = (ListView)this.findViewById(R.id.demo_main_activity_layout_listView);
		chatInfoListAdapter = new MyAdapter(this);
		chatInfoListView.setAdapter(chatInfoListAdapter);
	}
	
	private void initListeners()
	{
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(!CheckNetworkState())
					return;
				
				// 设置服务器地址和端口号
				String serverIP = editServerIp.getText().toString();
				String serverPort = editServerPort.getText().toString();
				if(!(serverIP.trim().length() <= 0)
					&& !(serverPort.trim().length() <= 0))
				{
					ConfigEntity.serverIP = serverIP.trim();
					try
					{
						ConfigEntity.serverUDPPort = Integer.parseInt(serverPort.trim());
					}
					catch (Exception e2)
					{
						Toast.makeText(getApplicationContext(), "请输入合法的端口号！", Toast.LENGTH_SHORT).show();
						showIMInfo_red("请输入合法的端口号！");
						return;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "请确保服务端地址和端口号都不为空！", Toast.LENGTH_SHORT).show();
					showIMInfo_red("请确保服务端地址和端口号都不为空！");
					return;
				}
				
				// 发送登陆数据包
				if(editLoginName.getText().toString().trim().length() > 0)
				{
					// 提交登陆名和密码
					new LocalUDPDataSender.SendLoginDataAsync(DemoMain.this
							, editLoginName.getText().toString().trim()
							, editLoginPsw.getText().toString().trim())
					{
						@Override
						protected void fireAfterSendLogin(int code)
						{
							if(code == 0)
							{
								//
								Toast.makeText(getApplicationContext(), "数据发送成功！", Toast.LENGTH_SHORT).show();
								Log.d(DemoMain.class.getSimpleName(), "登陆信息已成功发出！");
							}
							else
								Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
						}
					}.execute();
				}
				else
					Log.e(DemoMain.class.getSimpleName()
							, "txt.len="+(editLoginName.getText().toString().trim().length()));
			}
		});
		
		btnLogout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				// 发出退出登陆请求包
//				new LocalUDPDataSender.SendDataAsync(DemoMain.this){
				new AsyncTask<Object, Integer, Integer>(){
					@Override
					protected Integer doInBackground(Object... params)
					{
						int code = -1;
						try{
							code = LocalUDPDataSender.getInstance(DemoMain.this).sendLoginout();
						}
						catch (Exception e){
							Log.w(TAG, e);
						}
						
						return code;
					}

					@Override
					protected void onPostExecute(Integer code)
					{
						setMyid(-1);
						if(code == 0)
							Log.d(DemoMain.class.getSimpleName(), "注销登陆请求已完成！");
						else
							Toast.makeText(getApplicationContext(), "注销登陆请求发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
					}
				}.execute();
			}
		});
		
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				String msg = editContent.getText().toString().trim();
				if(msg.length() > 0)
				{
					int friendId = Integer.parseInt(editId.getText().toString().trim());
					showIMInfo_black("我对"+friendId+"说："+msg);
					
					//** [1] 发送不需要QoS支持的消息
//					new LocalUDPDataSender.SendCommonDataAsync(DemoMain.this, msg, friendId)
//					{
//						@Override
//						protected void onPostExecute(Integer code)
//						{
//							if(code == 0)
//								Log.d(DemoMain.class.getSimpleName(), "2数据已成功发出！");
//							else
//								Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
//						}
//					}.execute();
					
					//** [2] 发送需要QoS支持的消息
//				    String fingerPring = Protocal.genFingerPrint();
//				    Protocal p = ProtocalFactory.createCommonData(msg
//				    		, ClientCoreSDK.getInstance().getCurrentUserId()
//				    		, friendId, true, fingerPring);
				    new LocalUDPDataSender.SendCommonDataAsync(DemoMain.this, msg, friendId, true)
					{
						@Override
						protected void onPostExecute(Integer code)
						{
							if(code == 0)
								Log.d(DemoMain.class.getSimpleName(), "2数据已成功发出！");
							else
								Toast.makeText(getApplicationContext(), "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
						}
					}.execute();  		
				}
				else
					Log.e(DemoMain.class.getSimpleName(), "txt2.len="+(msg.length()));
			}
		});
	}
	
	private void initIMCore()
	{
		// 设置AppKey
	    ConfigEntity.appKey = "5418023dfd98c579b6001741";
	    
	    // 设置服务器ip和服务器端口
//		ConfigEntity.server_ip = "192.168.82.138";
//		ConfigEntity.serverIP = "rbcore.openmob.com";
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
	
	protected void onDestroy()
	{
		ClientCoreSDK.getInstance().release();
		//
		super.onDestroy();
	}
	
	public void setMyid(int myid)
	{
		this.viewMyid.setText(myid == -1 ? "未登陆" :""+myid);
	}
	
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
	
	//-------------------------------------------------------------
	private boolean CheckNetworkState()
	{
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager)getSystemService(
				Context.CONNECTIVITY_SERVICE);
		if(manager.getActiveNetworkInfo() != null)
		{
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		if(!flag)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle("Network not avaliable");//
			builder.setMessage("Current network is not avaliable, set it?");//
			builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.create();
			builder.show();
		}

		return flag;
	}
	
	//------------------------------------------------------------------- inner classes
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
	
	public enum ChatInfoColorType
    {
    	black,
    	blue,
    	brightred,
    	red,
    	green,
    }
}
