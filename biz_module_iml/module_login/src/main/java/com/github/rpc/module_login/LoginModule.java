package com.github.rpc.module_login;

import android.content.Context;
import android.util.Log;

import com.github.rpc.modularization.RPCModule;
import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_login_api.LoginModuleService;

public class LoginModule implements RPCModule {
    @Override
    public void onInit(Context context) {
        //todo 自动注册
//        RPCModuleServiceManager.getInstance().registerService(LoginModuleService.class, LoginModuleServiceImpl.class);
        Log.d("LoginModule","LoginModule onInit");

    }
}
