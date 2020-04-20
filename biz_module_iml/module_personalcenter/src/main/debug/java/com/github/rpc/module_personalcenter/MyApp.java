package com.github.rpc.module_personalcenter;

import android.app.Application;

import com.github.rpc.modularization.RPCModuleHelper;
import com.github.rpc.modularization.RPCModuleServiceManager;


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_login.LoginModel");
        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_topic.TopicModule");
        RPCModuleHelper.newInstanceAndInit(getApplicationContext(),"com.github.rpc.module_personalcenter.PersonalCenterModule");
    }
}
