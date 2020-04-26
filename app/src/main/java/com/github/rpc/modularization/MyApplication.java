package com.github.rpc.modularization;

import android.app.Application;

import com.github.rpc.module_login.LoginModuleServiceImpl;
import com.github.rpc.module_login_api.LoginModuleService;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RPCModuleServiceManager.init(getApplicationContext());

        //todo 自动实例化， 模块有必要提供注册和反注册吗? 或者直接把 RPCModule合并到 RPCModuleService
//        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_login.LoginModel");
//        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_topic.TopicModule");
//        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_personalcenter.PersonalCenterModule");
//
//        //
//        RPCModuleServiceManager.getInstance().registerService(LoginModuleService.class, LoginModuleServiceImpl.class);

    }
}
