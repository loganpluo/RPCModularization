package com.github.rpc.modularization;

import android.app.Application;

import com.github.rpc.module_login.LoginModuleServiceImpl;
import com.github.rpc.module_login_api.LoginModuleService;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RPCModuleServiceManager.init(getApplicationContext());
    }
}
