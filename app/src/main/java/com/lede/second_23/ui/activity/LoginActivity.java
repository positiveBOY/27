package com.lede.second_23.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lede.second_23.MyApplication;
import com.lede.second_23.R;
import com.lede.second_23.bean.LoginBean;
import com.lede.second_23.bean.MsgBean;
import com.lede.second_23.bean.UserInfoBean;
import com.lede.second_23.global.GlobalConstants;
import com.lede.second_23.utils.Md5Util;
import com.lede.second_23.utils.SPUtils;
import com.lede.second_23.utils.T;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.tools.NetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 登陆页面
 */
public class LoginActivity extends AppCompatActivity implements OnResponseListener<String> {

    private static final int LOGIN_REQUEST = 3000;
    private static final int REPORTREGISTER=4000;

    @Bind(R.id.et_login_activity_phone)
    EditText etLoginPhone;
    @Bind(R.id.et_login_activity_pwd)
    EditText etLoginPwd;
    @Bind(R.id.tv_login_activity_login)
    TextView tv_login_activity_login;
    @Bind(R.id.btn_login_register)
    TextView btnLoginRegister;
    @Bind(R.id.btn_login_forget_pwd)
    TextView btnLoginForgetPwd;
    private RequestQueue requestQueue;
    private Gson mGson;

    private static final int LOAD_USER = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mGson = new Gson();
        //获取请求队列
        requestQueue = GlobalConstants.getRequestQueue();
        if (getIntent().getBooleanExtra("isRegister",false)) {
            reportRegister();
        }
    }

    @OnClick({R.id.tv_login_activity_login, R.id.btn_login_register, R.id.btn_login_forget_pwd})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {

            case R.id.tv_login_activity_login:
                //判断登陆信息是否完整并请求服务器
//                Toast.makeText(this, "点击登录", Toast.LENGTH_SHORT).show();
                tv_login_activity_login.setClickable(false);
                loginInfoIsFull();
                break;
            case R.id.btn_login_register:
                //跳转到注册页面
                intent = new Intent(this, RegisterActivity.class);
//                intent.putExtra("type",0);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_login_forget_pwd:
                //跳转到忘记密码页面
                intent = new Intent(this, ForgetPassword_PhoneActivity.class);
                startActivity(intent);
//                startActivity(new Intent(this,RePwdActivity.class));
                break;
//            case R.id.iv_login_back:
//                finish();
//                break;
        }
    }

    //判断登陆信息是否完整
    public void loginInfoIsFull() {
        //获取输入框内容
        String phone = etLoginPhone.getText().toString().trim();
        String pwd = etLoginPwd.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            T.showShort(this, "手机号码不能为空哦");
            tv_login_activity_login.setClickable(true);
            return;
        }
//        if (!Validator.isMobile(phone)) {
//            T.showShort(this, "请输入正确的手机号码");
//            return;
//        }
        if (TextUtils.isEmpty(pwd)) {
            T.showShort(this, "密码不能为空哦");
            tv_login_activity_login.setClickable(true);

            return;
        }

        //设置登陆不能点击
        tv_login_activity_login.setClickable(false);
        //请求服务器
        try {
//            Toast.makeText(this, "启动请求", Toast.LENGTH_SHORT).show();
            Login2Servce(phone, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //访问服务器
    public void Login2Servce(String keyword, String pwd) throws Exception {
        Log.i("TAG", "Login2Servce: -------开始请求服务器登录");
        //获取设备id
//        String deviceToken = GlobalConstants.getDeviceToken();
//        //生成token
//        Map<String, Object> map = new HashMap<>();
//        map.put("password", pwd);
//        String token = JwtSignUtil.signer(map);
//        //获取时间戳
//        long time = System.currentTimeMillis();
//        String sign = keyword + deviceToken + token + time;
//        sign = Md5Util.MD5(sign);

        //创建登陆请求
        final Request<String> loginRequest = NoHttp.createStringRequest(GlobalConstants.URL + "/login", RequestMethod.POST);
//        loginRequest.add("deviceToken", deviceToken);
//        loginRequest.add("token", token);
        loginRequest.add("keyword", keyword);
//        loginRequest.add("tm", String.valueOf(time));
//        loginRequest.add("sign", sign);
        loginRequest.add("password", pwd);

        Log.i("TAGG", "Login2Sezrvce: --------->loginRequest" + loginRequest.url());
        //添加请求到请求队列中
        requestQueue.add(LOGIN_REQUEST, loginRequest, this);
    }

    //解析登录成功的用户信息
    public void parseJson(String json) {

        LoginBean loginBean = mGson.fromJson(json, LoginBean.class);
        if (loginBean.getResult() == 10001 || loginBean.getResult() == 10002) {
            Toast.makeText(this, loginBean.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            //把access_token存储到sp中
            SPUtils.put(this, GlobalConstants.TOKEN, loginBean.getData().getAccess_token());
            SPUtils.put(this, GlobalConstants.EXPIRES_IN, loginBean.getData().getExpires_in());
            SPUtils.put(this, GlobalConstants.USERID, loginBean.getData().getUser().getId());
//        SPUtils.put(this, GlobalConstants.CERTIFICATION, loginBean.certification);
            //提示用户登陆成功并退出登陆界面
            T.showShort(this, "登录成功");
            SPUtils.put(LoginActivity.this, GlobalConstants.TOKENUNUSEFULL, false);
            loadUserInfo();


        }

    }

    /**
     * 请求服务器获取用户信息
     */
    public void loadUserInfo() {
        Request<String> loadUserRequest = NoHttp.createStringRequest(GlobalConstants.URL + "/users/" + SPUtils.get(this, GlobalConstants.USERID, "") + "/detail", RequestMethod.GET);
        loadUserRequest.add("access_token", (String) SPUtils.get(this, GlobalConstants.TOKEN, ""));
        requestQueue.add(LOAD_USER, loadUserRequest, this);
    }

    /**
     * 解析用户信息
     */
    private void parmeUserInfoJson(String json) {
        Gson gson = new Gson();
        UserInfoBean userInfoBean = gson.fromJson(json, UserInfoBean.class);

        if (userInfoBean.getData().getInfo() == null) {
            SPUtils.put(this, GlobalConstants.USER_HEAD_IMG, "");

            Intent intent = new Intent(this, EditRegisterInfoActivity.class);
            startActivity(intent);
        } else {
            MyApplication.instance.getRongIMTokenService();
            SPUtils.put(this,GlobalConstants.USERNAME,userInfoBean.getData().getInfo().getNickName());
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        finish();
    }

    /**
     * 上报注册
     */
    private void reportRegister() {
        Request<String> reportRegister = NoHttp.createStringRequest(GlobalConstants.URL + "/verifyGdtData.cgi", RequestMethod.POST);
        reportRegister.add("muid", Md5Util.getMD5(GlobalConstants.getDeviceToken()));
        reportRegister.add("conv_type", "MOBILEAPP_REGISTER");
        reportRegister.add("appid", GlobalConstants.APPID);
        reportRegister.add("client_ip", NetUtil.getLocalIPAddress());
        reportRegister.add("app_type", "unionandroid");
        reportRegister.add("conv_time", System.currentTimeMillis() / 1000);
        requestQueue.add(REPORTREGISTER, reportRegister , this);
    }

    @Override
    public void onStart(int what) {

    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        Log.i("LoginActivity", "onSucceed: "+response.get());
        switch (what) {
            case REPORTREGISTER:

                break;
            case LOAD_USER:
                parmeUserInfoJson(response.get());
                break;
            case LOGIN_REQUEST:
                tv_login_activity_login.setClickable(true);
                Log.i("TAGG", "onSucceed: ");
                if (response.responseCode() == 200) {
                    String json = response.get();
                    Log.i("TAGG", "onSucceed: 登录的response:" + response.get());
                    parseJson(json);
                } else {
                    if (!TextUtils.isEmpty(response.get())) {
                        MsgBean msgBean = mGson.fromJson(response.get(), MsgBean.class);
                        T.showShort(LoginActivity.this, msgBean.getMsg());
                    }
                }
                break;
        }
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        switch (what) {
            case LOGIN_REQUEST:
                T.showShort(LoginActivity.this, "网络访问失败，请检查网络重新登陆");
                Log.i("TAGG", "onFailed: --------->" + response.responseCode());
                tv_login_activity_login.setClickable(true);
                break;
            default:
                T.showShort(LoginActivity.this,"网络访问失败，请检查网络");
                break;
        }
    }

    @Override
    public void onFinish(int what) {

    }
}
