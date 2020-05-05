package com.github.rpc.modularization;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RPCModuleServiceManager.init(getApplicationContext());
//        RPCModuleServiceManager.init(getApplicationContext(),ModuleServiceType.SingleInstance);
    }
}
