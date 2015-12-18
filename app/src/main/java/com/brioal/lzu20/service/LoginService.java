package com.brioal.lzu20.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.brioal.lzu20.tools.GetLzu;
import com.brioal.lzu20.tools.Info;


public class LoginService extends IntentService {
    private IntentFilter wifiIntentFilter;
    private Handler handler ;
    private Toast toast ;
    private String TAG = "LoginService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoginService(String name) {
        super(name);
        wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    //不需要登陆
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), "此wifi不需要登陆", Toast.LENGTH_SHORT);
                    } else {
                        toast.setText("此wifi不需要登陆");
                    }
                    toast.show();
                } else if (msg.what == 1) {
//                    需要登陆
                        String click_url = "http://10.10.1.254/cgi-bin/srun_portal";
                        String parma = "action=login&username=" + username + "@lzu.edu.cn&ac_id=12&type=1&wbaredirect=http://www.nuomi.com/?cid&mac\n" +
                                "=&nas_ip=&password=" + password + "&is_ldap=1";
                        Info from_logo = GetLzu.Post(new Info(click_url, null, parma));
                        String data = from_logo.getData();
                        System.out.println(data);
                        if (data.contains("连接成功")) {
                           //连接成功
                            if (toast == null) {
                                toast = Toast.makeText(getApplicationContext(), "Lzu认证成功", Toast.LENGTH_SHORT);
                            } else {
                                toast.setText("Lzu认证成功");
                            }
                            toast.show();
                        }
                } else {
//                    网络连接错误
                    if (toast == null) {
                        toast = Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT);
                    } else {
                        toast.setText("网络连接错误");
                    }
                    toast.show();
                }
            }
        } ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(myReceiver, wifiIntentFilter);
        Log.i(TAG, "onBind: 绑定成功");

        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 接受到广播,service正在运行");

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                boolean isFirst = true;

                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConcected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态

                    if (isConcected) { // 连接成功
                        //此处判断是否需要登陆


                        Runnable pingRunable = new Runnable() {
                            @Override
                            public void run() {
                                Info pingInfo = new Info("www.baidu.com", null, null);
                                String result = GetLzu.Get(pingInfo);
                                if (result.contains("百度")) {
                                    //说明不需要登陆
                                    handler.sendEmptyMessage(0);
                                } else {
                                    //说明需要登陆
                                    handler.sendEmptyMessage(1);
                                }

                            }
                        } ;
                        try {
                            new Thread(pingRunable).start();
                        } catch (Exception e) {
                            //网络状态不好传2
                            handler.sendEmptyMessage(2);
                        }
                    } else { // 未连接wifi

                    }
                }
            }
        }
    };
}
