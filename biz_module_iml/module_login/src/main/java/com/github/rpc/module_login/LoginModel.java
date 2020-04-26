package com.github.rpc.module_login;

import android.content.Context;

import com.github.rpc.modularization.RPCModule;
import com.github.rpc.modularization.RPCModuleServiceManager;
import com.github.rpc.module_login_api.LoginModuleService;

public class LoginModel implements RPCModule {
    @Override
    public void onInit(Context context) {
        //todo 自动注册
        RPCModuleServiceManager.getInstance().registerService(LoginModuleService.class, LoginModuleServiceImpl.class);

    }
}
