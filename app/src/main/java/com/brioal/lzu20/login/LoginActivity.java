package com.brioal.lzu20.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.EditText;

import com.brioal.lzu20.R;
import com.brioal.lzu20.service.LoginService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {
    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor ;
    private String userName ;
    private String passWord;
    private Handler handler ;

    @ViewById(R.id.login_toolbar)
    Toolbar toolbar ;
    @ViewById(R.id.login_username)
    EditText edt_username ;
    @ViewById(R.id.login_password)
    EditText edt_password ;
    @ViewById(R.id.login_check_rem)
    CheckBox isRember ;

    @ViewById(R.id.login_chenck_auto)
    CheckBox isAuto ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @AfterViews
    public void afterViews() {
        toolbar.setTitle("登陆");
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("Brioal", MODE_PRIVATE);
        editor = preferences.edit();
        userName = preferences.getString("username", "");
        passWord = preferences.getString("password", "");
        edt_username.setText(userName);
        edt_password.setText(passWord);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //此处接受信息,启动service
                Intent intent = new Intent(LoginActivity.this, LoginService.class);
                intent.putExtra("username", userName);
                intent.putExtra("password", passWord);
                startService(intent); // 启动
            }
        };

    }

    @Click(R.id.login_btn_commit)
    public void connit() {
        if (isRember.isChecked()) { // 保存密码
            editor.putBoolean("isRme", true);
            editor.putString("username", userName);
            editor.putString("password", passWord);
        } else { // 不保存密码
            editor.putBoolean("isRem", false);
            editor.putString("username", userName);

        }
        if (isAuto.isChecked()) {
            editor.putBoolean("isAuto", true);
        } else {
             editor.putBoolean("isAuto", false);
        }

        editor.commit();
//        判断是否登陆成功
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                    get网页内容,判断是否登陆成功
//                if () {
//                    //登陆成功
//
//                } else {
//                    //登陆失败
//                }

                
            }
        } ;

    }

}

