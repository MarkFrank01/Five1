package com.wjc.parttime.account.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.tools.utils.UIHandler;
import com.wjc.parttime.LitePalHelperDB.UserHelperDB;
import com.wjc.parttime.R;
import com.wjc.parttime.account.register.RegisterActivity;
import com.wjc.parttime.account.reset.ResetStepOneActivity;
import com.wjc.parttime.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static android.R.attr.action;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, PlatformActionListener, Handler.Callback{

    @BindView(R.id.et_login_username)
    EditText mLoginUsername;

    @BindView(R.id.et_login_pwd)
    EditText mLoginPassword;

    private PlatformDb platDB;
    private ProgressDialog progressDialog;
    private static final int MSG_ACTION_CCALLBACK = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
       // showSplashView();

        List<UserHelperDB> user= DataSupport.findAll(UserHelperDB.class);
        for (UserHelperDB userHelperDB :user){
            LogUtil.e("Login",userHelperDB.getUserId()+"");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
      //  if(keyCode== KeyEvent.KEYCODE_BACK){
            return false;//不执行父类点击事件
    //    }
       // return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    @OnClick({R.id.et_login_username, R.id.et_login_pwd, R.id.bt_login_submit, R.id.tv_login_other_way,
            R.id.bt_login_wx, R.id.bt_login_xl, R.id.tv_login_no_account, R.id.tv_login_forget_pwd})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_submit:
                //登录
                break;

            case R.id.et_login_username:
                mLoginPassword.clearFocus();
                mLoginUsername.setFocusableInTouchMode(true);
                mLoginUsername.requestFocus();
                break;

            case R.id.et_login_pwd:
                mLoginUsername.clearFocus();
                mLoginPassword.setFocusableInTouchMode(true);
                mLoginPassword.requestFocus();
                break;

            case R.id.tv_login_no_account:
                //注册
                Toast.makeText(this, "注册", Toast.LENGTH_SHORT).show();
                RegisterActivity.show(LoginActivity.this);
                break;

            case R.id.tv_login_forget_pwd:
                //忘记密码和重置密码共用
                Toast.makeText(this, "忘记密码", Toast.LENGTH_SHORT).show();
                ResetStepOneActivity.show(LoginActivity.this);
                break;

            case R.id.tv_login_other_way:
                //第三方登录
                // QQ登录
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(this);
                qq.SSOSetting(false);
                authorize(qq, 1);
                break;

            case R.id.bt_login_wx:
                // 微信登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(this);
                wechat.SSOSetting(false);
                authorize(wechat, 2);
                break;

            case R.id.bt_login_xl:
                //新浪
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                sina.setPlatformActionListener(this);
                sina.SSOSetting(false);
                authorize(sina, 3);
                break;

        }
    }


    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    //一键登录授权
    private void authorize(Platform plat, int type) {
        switch (type) {
            case 1:
                showProgressDialog("正在打开QQ，请稍后...");
                break;
            case 2:
                showProgressDialog("正在打开微信，请稍后...");
                break;
            case 3:
                showProgressDialog("正在打开微博，请稍后...");
                break;
        }
        if (plat.isAuthValid()) { //如果授权就删除授权资料
            plat.removeAccount(true);
        }
        plat.showUser(null);//授权并获取用户信息
    }

    //登录授权成功的回调
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> res) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);   //发送消息

    }

    //登陆授权错误的回调
    @Override
    public void onError(Platform platform, int i, Throwable t) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
    }

    //登陆授权取消的回调
    @Override
    public void onCancel(Platform platform, int i) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    //登陆发送的handle消息在这里处理
    @Override
    public boolean handleMessage(Message message) {
        hideProgressDialog();
        switch (message.arg1) {
            case 1: { // 成功
                Toast.makeText(LoginActivity.this, "授权登陆成功", Toast.LENGTH_SHORT).show();

                //获取用户资料
                Platform platform = (Platform) message.obj;
                String userId = platform.getDb().getUserId();//获取用户账号
                String userName = platform.getDb().getUserName();//获取用户名字
                String userIcon = platform.getDb().getUserIcon();//获取用户头像
                String userGender = platform.getDb().getUserGender(); //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
                Toast.makeText(LoginActivity.this, "用户信息为--用户名：" + userName + "  性别：" + userGender, Toast.LENGTH_SHORT).show();

                //下面就可以利用获取的用户信息登录自己的服务器或者做自己想做的事啦!
                //。。。

            }
            break;
            case 2: { // 失败
                Toast.makeText(LoginActivity.this, "授权登陆失败", Toast.LENGTH_SHORT).show();
            }
            break;
            case 3: { // 取消
                Toast.makeText(LoginActivity.this, "授权登陆取消", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return false;
    }

    //显示dialog
    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    //隐藏dialog
    public void hideProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }

    }
}