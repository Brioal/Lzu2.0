package com.brioal.lzu20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.brioal.lzu20.service.LoginService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private WifiManager wifiManager = null;
    private Toast toast;
    private Handler handler;
    private WifiInfo wifiInfo;
    private IntentFilter wifiIntentFilter;
    private String TAG = "MainActivity";
    private boolean isConcected = false ; // wifi连接是否成功

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewById(R.id.nav_view)
    NavigationView navigationView;
    @ViewById(R.id.fab)
    FloatingActionButton fab;
    @ViewById(R.id.main_switch)
    Switch wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @AfterViews
    public void afterViews() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        wifiSwitch.setOnCheckedChangeListener(this);

        wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        绑定Service接收wifi广播,当连接wifi之后拼网站,登陆
        Intent intent = new Intent(MainActivity.this, LoginService.class);
        intent.putExtra("username", "huangj2013");
        intent.putExtra("password", "199501");
        startService(intent);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    initWhenOpening();
                } else if (msg.what == 1) {
                    initWhenOpen();
                } else if (msg.what == 2) {
                    initWhenClosing();
                } else if (msg.what == 3) {
                    initWhenClose();
                }
            }
        };
    }


    public void initWhenClose() {
        if (!wifiSwitch.isEnabled()) {
            wifiSwitch.setEnabled(true);
        }
        if (wifiSwitch.isChecked()) {
            wifiSwitch.setChecked(false);
        }
        wifiSwitch.setText("wifi不可用");
    }

    public void initWhenOpen() {
        if (!wifiSwitch.isEnabled()) {
            wifiSwitch.setEnabled(true);
        }
        if (!wifiSwitch.isChecked()) {
            wifiSwitch.setChecked(true);
        }
        if (wifiInfo.getSSID() != null) {
            wifiSwitch.setText(wifiInfo.getSSID());
        } else {
            wifiSwitch.setText("wifi可用,未连接");
        }


    }

    public void initWhenClosing() {
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            Log.i(TAG, "initWhenClosing: 关闭wifi");
            wifiManager.setWifiEnabled(false);
        }
        wifiSwitch.setEnabled(false);
        wifiSwitch.setText("wifi正在关闭");

    }

    public void initWhenOpening() {
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            wifiManager.setWifiEnabled(true);
        }
        wifiSwitch.setEnabled(false);
        wifiSwitch.setText("wifi正在打开");

    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) { // 变为选中
            Log.i(TAG, "onCheckedChanged: 开关打开");

            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
                handler.sendEmptyMessage(0);
//                        try {
//                            Thread.sleep(2000);
//                            handler.obtainMessage();
//                            handler.sendEmptyMessage(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                new Thread(runnable).start();

            }
        } else {
            Log.i(TAG, "onCheckedChanged: 开关关闭");
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
                handler.sendEmptyMessage(2);
//                        try {
//                            Thread.sleep(2000);
//                            handler.obtainMessage();
//                            handler.sendEmptyMessage(3);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
////                        }
//                    }
//                };
//                new Thread(runnable).start();
//
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册wifi消息处理器
        registerReceiver(wifiIntentReceiver, wifiIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiIntentReceiver);
    }

    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.i(TAG, "onReceive:wifi状态 接受广播一次");
                wifiManager = (WifiManager) MainActivity.this
                        .getSystemService(Context.WIFI_SERVICE);
                //获取wfi列表,看是否存在开放且名称为lzu的wifi

                wifiInfo = wifiManager.getConnectionInfo();
                int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                int level = wifiManager.getConnectionInfo().getRssi();
                switch (wifi_state) {
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.i(TAG, "WiFI正在关闭");
                        handler.sendEmptyMessage(2);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.i(TAG, "WiFI已经关闭");
                        handler.sendEmptyMessage(3);
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.i(TAG, "WiFI正在打开");
                        handler.sendEmptyMessage(0);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.i(TAG, "WiFI已经打开");
                        handler.sendEmptyMessage(1);
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.i(TAG, "WiFI状态异常");
                        break;
                }

            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                boolean isFirst = true;

                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                     isConcected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态

                    if (isConcected) { // 连接成功
                        wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            Log.e(TAG, "wifi连接成功");
                            handler.sendEmptyMessage(1);
                            //此时启动service来试探登陆wifi
                        }
                    } else { // 未连接wifi

                        if (isFirst) {
                            Log.i(TAG, "onReceive: 未连接wifi");
                            isFirst = false;
                            wifiManager.startScan();
                            ArrayList<ScanResult> list;                   //存放周围wifi热点对象的列表
                            list = (ArrayList<ScanResult>) wifiManager.getScanResults();
                            Log.i(TAG, "onReceive: "+list.size());
//                        进行排序
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 1; j < list.size(); j++) {
                                    if (list.get(i).level < list.get(j).level)    //level属性即为强度
                                    {
                                        ScanResult temp = null;
                                        temp = list.get(i);
                                        list.set(i, list.get(j));
                                        list.set(j, temp);
                                    }
                                }
                            }
                            for (int i = 0; i < list.size(); i++) {
                                ScanResult result = list.get(i);
                                if (result.SSID.equals("Lzu")) {
                                    //判断是否需要密码

                                }
//                                System.out.print(result.SSID);
//                                System.out.println("       "+result.level);
                            }
                        }

                    }
                }
            }


        }


    };
}
